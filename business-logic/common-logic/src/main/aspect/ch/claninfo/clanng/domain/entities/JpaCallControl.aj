package ch.claninfo.clanng.domain.entities;

import java.util.ServiceLoader;

import ch.claninfo.clanng.business.logic.conversion.ClassMapper;

public aspect JpaCallControl {

	private static final ServiceLoader<ClassMapper> classMapperHandler = ServiceLoader.load(ClassMapper.class);

	pointcut jpaPersist(Object caller, SplittableBo bo):
			(call(public void javax.persistence.EntityManager.persist(..))
			|| call(public void javax.persistence.EntityManager.remove(..)))
					&& args(bo)
					&& this(caller);
	
	void around(Object caller, SplittableBo bo): jpaPersist(caller, bo) {
		for (ClassMapper classMapper : classMapperHandler) {
			if (classMapper.handles(bo.getClass())) {
				return;
			}
		}

		proceed(caller, bo);
	}
}