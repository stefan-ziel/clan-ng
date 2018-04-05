package ch.claninfo.clanng.domain.types;

public enum SexCd {
	MALE(1), FEMALE(2);

	private final Integer cd;

	public static SexCd fromCd(Integer cd) {
		if (cd == null) {
			return null;
		}

		for (SexCd sexCd : SexCd.values()) {
			if (cd.equals(sexCd.getCd())) {
				return sexCd;
			}
		}
		return null;
	}

	SexCd(int cd) {
		this.cd = cd;
	}

	public int getCd() {
		return cd;
	}
}