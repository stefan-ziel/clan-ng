package ch.claninfo.clanng.converters.jpa;

import java.math.BigDecimal;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DoubleDecimalConverter extends StandardAttributeConverter<Double, BigDecimal> {
	@Override
	public BigDecimal safeConvertToDatabaseColumn(Double attribute) {
		return BigDecimal.valueOf(attribute);
	}

	@Override
	public Double safeConvertToEntityAttribute(BigDecimal dbData) {
		return dbData.doubleValue();
	}
}