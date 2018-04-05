package ch.claninfo.clanng.converters.jpa;

import java.text.ParseException;
import javax.persistence.Converter;

import ch.claninfo.common.util.AhvNummer;

@Converter(autoApply = true)
public class AhvNummerConverter extends StandardAttributeConverter<AhvNummer, String> {

	@Override
	protected String safeConvertToDatabaseColumn(AhvNummer attribute) {
		return attribute.toDbString();
	}

	@Override
	protected AhvNummer safeConvertToEntityAttribute(String dbData) {
		try {
			return new AhvNummer(dbData);
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}