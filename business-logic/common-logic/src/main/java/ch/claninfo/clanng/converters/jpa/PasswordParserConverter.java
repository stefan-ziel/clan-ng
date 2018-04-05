package ch.claninfo.clanng.converters.jpa;

import javax.persistence.Converter;

import ch.claninfo.common.util.PasswordParser;

@Converter(autoApply = true)
public class PasswordParserConverter extends StandardAttributeConverter<PasswordParser, String> {

	@Override
	protected String safeConvertToDatabaseColumn(PasswordParser attribute) {
		return attribute.toString();
	}

	@Override
	protected PasswordParser safeConvertToEntityAttribute(String dbData) {
		return new PasswordParser(dbData);
	}
}