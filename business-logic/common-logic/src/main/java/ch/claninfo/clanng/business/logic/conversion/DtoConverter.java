
package ch.claninfo.clanng.business.logic.conversion;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;

import ch.claninfo.clanng.domain.entities.ClanEntity;
import ch.claninfo.clanng.domain.entities.SplittableBo;
import ch.claninfo.common.dao.BaseBo;

@Configurable
public class DtoConverter {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final ServiceLoader<ClassMapper> classMapperHandler = ServiceLoader.load(ClassMapper.class);

	public static boolean handledBo(SplittableBo bo) {
		for (ClassMapper classMapper : classMapperHandler) {
			if (classMapper.handles(bo.getClass())) {
				return true;
			}
		}

		return false;
	}

	public static Collection<ClanEntity> toEntity(SplittableBo bo) {
		for (ClassMapper classMapper : classMapperHandler) {
			if (classMapper.handles(bo.getClass())) {
				return classMapper.map(bo);
			}
		}

		return Collections.emptyList();
	}

//	public static <V extends ClanEntity> V updateDto(SplittableBo bo, V entity) {
//		try {
//			List<PropertyDescriptor> changedProperties = getChangedProperties(bo);
//
//			ClassMapper mapper = null;
//			for (ClassMapper classMapper : classMapperHandler) {
//				if (classMapper.handles(bo.getClass())) {
//					mapper = classMapper;
//					break;
//				}
//			}
//			if (mapper == null) {
//				throw new NullPointerException(String.format("Mapper value not set. No handlers for %s", bo));
//			}
//
//			for (PropertyDescriptor property : changedProperties) {
//				mapper.setAttribute(entity, property.getName(), property.getReadMethod().invoke(bo));
//			}
//
//			return entity;
//		}
//		catch (IllegalAccessException | InvocationTargetException e) {
//			throw new RuntimeException(e);
//		}
//	}

	private static List<PropertyDescriptor> getChangedProperties(BaseBo bo) {
		List<PropertyDescriptor> changedProperties = new ArrayList<>();
		Class<? extends BaseBo> boClass = bo.getClass();

		for (String p : bo.getChangedProperties()) {
			try {
				changedProperties.add(new PropertyDescriptor(p.toLowerCase(), boClass));

			}
			catch (IntrospectionException e) {
				LOGGER.error("Bo property name incorrectly set.", e);
				throw new RuntimeException(e);
			}
		}
		return changedProperties;
	}
}
