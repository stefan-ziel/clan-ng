package ch.claninfo.clanng.domain.services;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;

import ch.claninfo.common.dao.BaseBo;

public class JpaUtils {

	private JpaUtils() {}

	/**
	 * Gets the maximum length of a field, returns null when it does not exist.
	 * 
	 * @param c
	 * @param attribute
	 * @return
	 */
	public static int getLength(Class<?> c, String attribute) {
		try {
			Field declaredField = c.getDeclaredField(attribute);

			for (Annotation annotation : declaredField.getDeclaredAnnotations()) {
				if (annotation instanceof Column) {
					return ((Column) annotation).length();
				}
			}
		}
		catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(e);
		}

		return 255; // Default for Column Annotation so we use it here
	}

	/**
	 * Updates unequal propertie fields between src and dst in the dst object.
	 *
	 * @param src The first object for comparison and permanent values.
	 * @param dst The second object and destination of changed properties.
	 * @param <T> Object type for both src and dst.
	 */
	public static <T> void updateDiff(T src, T dst) {
		Class<?> tClass = src.getClass();

		try {
			while (tClass != null && tClass != BaseBo.class) {
				for (Field field : tClass.getDeclaredFields()) {
					int fieldModifiers = field.getModifiers();
					if (field.isSynthetic()
					    || Modifier.isTransient(fieldModifiers)
					    || Modifier.isFinal(fieldModifiers)) {
						continue;
					}
					field.setAccessible(true);
					Object srcProperty = field.get(src);
					Object dstProperty = field.get(dst);
					if (!Objects.equals(srcProperty, dstProperty)) {
						try {
							PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), tClass);
							Method writeMethod = propertyDescriptor.getWriteMethod();
							writeMethod.setAccessible(true);
							writeMethod.invoke(dst, srcProperty);
						}
						catch (IntrospectionException e) {
							continue;
						}
					}
				}

				tClass = tClass.getSuperclass();
			}
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Field> getBoProperties(BaseBo o) {
		Class<?> boClass = o.getClass();

		List<Field> properties = new ArrayList<>();
		for (Field field : boClass.getDeclaredFields()) {
			int fieldModifiers = field.getModifiers();
			if (field.isSynthetic()
			    || Modifier.isTransient(fieldModifiers)
			    || Modifier.isFinal(fieldModifiers)) {
				continue;
			}
			field.setAccessible(true);

			properties.add(field);
		}

		return properties;
	}
}