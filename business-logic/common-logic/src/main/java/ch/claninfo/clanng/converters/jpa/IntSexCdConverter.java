package ch.claninfo.clanng.converters.jpa;

import javax.persistence.Converter;

import ch.claninfo.clanng.domain.types.SexCd;

@Converter(autoApply = true)
public class IntSexCdConverter extends StandardAttributeConverter<SexCd, Integer> {

	@Override
	public Integer safeConvertToDatabaseColumn(SexCd sexCd) {
		return sexCd.getCd();
	}

	@Override
	public SexCd safeConvertToEntityAttribute(Integer cd) {
		return SexCd.fromCd(cd);
	}
}