package ch.claninfo.clanng.business.logic.conversion;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.springframework.util.ReflectionUtils;

import ch.claninfo.clanng.domain.entities.ClanEntity;
import ch.claninfo.clanng.domain.entities.SplittableBo;
import ch.claninfo.clanng.domain.services.JpaUtils;

public abstract class ClassMapper {

	private static final List<String> NON_ENTITY_PROPERTIES = Arrays.asList("procnr", "updflag");

	public abstract boolean handles(Class<? extends SplittableBo> bo);

	public abstract <B extends SplittableBo> Collection<ClanEntity> map(B bo);

	protected abstract List<Class<? extends ClanEntity>> getClassMappings(Class<? extends SplittableBo> bo);

	protected <B extends SplittableBo> Collection<ClanEntity> defaultMap(B bo) {

		List<ClanEntity> entities = new ArrayList<>();

		try {
			Class<B> boClass = (Class<B>) bo.getClass();

			for (Class<? extends ClanEntity> entityClass : getClassMappings(boClass)) {
				ClanEntity entity = entityClass.newInstance();

				for (Field property : JpaUtils.getBoProperties(bo)) {
					linkProperty(bo, entity, property.getName());
				}

				entities.add(entity);
			}

			return Collections.unmodifiableList(entities);
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private <B extends SplittableBo> void linkProperty(B bo, ClanEntity entity, String boProperty) {

		String entityProperty = toClanEntityProperty(boProperty);
		if (entityProperty == null) {
			return;
		}

		LinkedDtoChanger boChanger = new LinkedDtoChanger(bo, boProperty);
		LinkedDtoChanger entityChanger = new LinkedDtoChanger(entity, entityProperty);

		entityChanger.setProperty(boChanger.getProperty());

		entity.addChangedListener(entityProperty, boChanger);
		bo.addChangedListener(boProperty, entityChanger);
	}

	protected abstract String toClanEntityProperty(String boProperty);

	protected String defaultToClanEntityProperty(String boProperty) {
		if (NON_ENTITY_PROPERTIES.contains(boProperty)) {
			return null;
		}

		return boProperty;
	}

	private class LinkedDtoChanger implements PropertyChangeListener {

		private final ConvertUtilsBean converter = BeanUtilsBean.getInstance().getConvertUtils();

		private final Object dto;
		private final Field field;
		private final Method setter;
		private final Method getter;

		public LinkedDtoChanger(Object dto, String property) {
			this.field = ReflectionUtils.findField(dto.getClass(), property);
			field.setAccessible(true);
			String camelCaseName = property.substring(0, 1).toUpperCase() + property.substring(1);
			setter = ReflectionUtils.findMethod(dto.getClass(), "set" + camelCaseName, field.getType());
			setter.setAccessible(true);

			getter = ReflectionUtils.findMethod(dto.getClass(), "get" + camelCaseName);
			getter.setAccessible(true);

			this.dto = dto;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setProperty(evt.getNewValue());
		}

		void setProperty(Object newValue) {
			try {
				Object currentValue = field.get(dto);

				if (currentValue == newValue) {
					return;
				}

				Class<?> fieldType = field.getType();
				if (newValue != null && fieldType != newValue.getClass()) {
					newValue = converter.convert(newValue, fieldType);
				}

				if (Objects.equals(currentValue, newValue)) {
					return;
				}

				setter.invoke(dto, newValue);
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				ReflectionUtils.handleReflectionException(e);
			}
		}

		Object getProperty() {
			try {
				return getter.invoke(dto);
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				ReflectionUtils.handleReflectionException(e);
			}
			throw new IllegalStateException("Should not reach this point.");
		}
	}
}