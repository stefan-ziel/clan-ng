package ch.claninfo.clanng.converters.beanutils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * Base Converter for BeanUtil Converters
 *
 * @param <Y> Denotes the destination class of this converter.
 */
public abstract class BeanUtilsConverter<Y> implements Converter {

	@Override
	public final <T> T convert(Class<T> targetClass, Object value) {

		if (value == null) {
			return null;
		}

		Class<?> valueClass = value.getClass();
		if (targetClass.isAssignableFrom(valueClass)) {
			return (T) value;
		} else if (handlesFromType(valueClass)) {
			return (T) convert(value);
		}

		throw new ConversionException(String.format("Cannot convert from %s to %s", value.getClass(), targetClass));
	}

	public abstract boolean handlesFromType(Class<?> type);

	public abstract Y convert(Object value);
}