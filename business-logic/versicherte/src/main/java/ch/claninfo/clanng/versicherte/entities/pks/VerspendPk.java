package ch.claninfo.clanng.versicherte.entities.pks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import ch.claninfo.clanng.domain.entities.Pk;

public class VerspendPk extends Pk {
	private BigDecimal vsnum;
	private LocalDate berdat;
	private String bercd;

	public BigDecimal getVsnum() {
		return vsnum;
	}

	public void setVsnum(BigDecimal vsnum) {
		this.vsnum = vsnum;
	}

	public LocalDate getBerdat() {
		return berdat;
	}

	public void setBerdat(LocalDate berdat) {
		this.berdat = berdat;
	}

	public String getBercd() {
		return bercd;
	}

	public void setBercd(String bercd) {
		this.bercd = bercd;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VerspendPk that = (VerspendPk) o;
		return Objects.equals(vsnum, that.vsnum) &&
		       Objects.equals(berdat, that.berdat) &&
		       Objects.equals(bercd, that.bercd);
	}

	@Override
	public int hashCode() {
		return Objects.hash(vsnum, berdat, bercd);
	}
}