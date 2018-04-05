package ch.claninfo.clanng.versicherte.entities.pks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import ch.claninfo.clanng.domain.entities.Pk;


public class VslohnPk extends Pk {
	private BigDecimal vsnum;
	private LocalDate gdat;

	public BigDecimal getVsnum() {
		return vsnum;
	}

	public void setVsnum(BigDecimal vsnum) {
		this.vsnum = vsnum;
	}

	public LocalDate getGdat() {
		return gdat;
	}

	public void setGdat(LocalDate gdat) {
		this.gdat = gdat;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VslohnPk vslohnPk = (VslohnPk) o;
		return Objects.equals(vsnum, vslohnPk.vsnum) &&
		       Objects.equals(gdat, vslohnPk.gdat);
	}

	@Override
	public int hashCode() {
		return Objects.hash(vsnum, gdat);
	}
}