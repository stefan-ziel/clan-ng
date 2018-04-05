package ch.claninfo.clanng.converters.beanutils;

import ch.claninfo.clanng.domain.types.SexCd;

public class ToSexCdConverter extends BeanUtilsConverter<SexCd> {

	@Override
	public boolean handlesFromType(Class<?> type) {
		return String.class.isAssignableFrom(type);
	}

	@Override
	public SexCd convert(Object value) {
		return SexCd.fromCd(Integer.parseInt((String) value));
	}
}