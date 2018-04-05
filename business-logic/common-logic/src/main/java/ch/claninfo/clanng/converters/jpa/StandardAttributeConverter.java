package ch.claninfo.clanng.converters.jpa;

import javax.persistence.AttributeConverter;

public abstract class StandardAttributeConverter<X, Y> implements AttributeConverter<X, Y> {
	@Override
	public final Y convertToDatabaseColumn(X attribute) {
		if (attribute == null) {
			return null;
		}

		return safeConvertToDatabaseColumn(attribute);
	}

	/**
	 * Null safe convert to database column, i.e. attribute is never null.
	 * @param attribute The entity attribute
	 * @return The jdbc type
	 */
	protected abstract Y safeConvertToDatabaseColumn(X attribute);

	@Override
	public final X convertToEntityAttribute(Y dbData) {
		if (dbData == null) {
			return null;
		}
		return safeConvertToEntityAttribute(dbData);
	}

	/**
	 * Null safe convert to entity attribuet, i.e. attribute is never null.
	 * @param dbData jdbc object
	 * @return entity attribute
	 */
	protected abstract X safeConvertToEntityAttribute(Y dbData);
}