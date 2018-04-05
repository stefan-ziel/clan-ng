package ch.claninfo.clanng.domain.entities;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

public aspect DtoSetterControl {

	pointcut entityFieldSetters(ClanEntity e, Object newValue):
			set(!static !transient !final * ch.claninfo.clanng.domain.entities.ClanEntity+.*)
					&& target(e)
					&& args(newValue);

	void around(ClanEntity entity, Object newValue) : entityFieldSetters(entity, newValue) {
		Field field;
		Object oldValue;
		try {
			String propertyField = thisJoinPoint.getSignature().getName();
			field = ReflectionUtils.findField(entity.getClass(), propertyField);
			field.setAccessible(true);
			oldValue = field.get(entity);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		proceed(entity, newValue);

		entity.callChangeListeners(new PropertyChangeEvent(entity, field.getName(), oldValue, newValue));
	}

	pointcut boFieldSetters(SplittableBo bo, Object newValue):
			set(!static !transient !final * ch.claninfo.clanng.domain.entities.SplittableBo+.*)
					&& target(bo)
					&& args(newValue);

	void around(SplittableBo bo, Object newValue): boFieldSetters(bo, newValue) {
		Field field;
		Object oldValue;
		try {
			String propertyField = thisJoinPoint.getSignature().getName();
			field = ReflectionUtils.findField(bo.getClass(), propertyField);
			field.setAccessible(true);
			oldValue = field.get(bo);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}


		proceed(bo, newValue);

		bo.callChangeListeners(new PropertyChangeEvent(bo, field.getName(), oldValue, newValue));
	}
}