package ch.claninfo.clanng.versicherte.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ch.claninfo.clanng.domain.entities.HistEntity;
import ch.claninfo.clanng.versicherte.entities.pks.VerspendPk;

@Entity
@Table(name = "verspend", schema = "PEKA")
@IdClass(VerspendPk.class)
@NamedQuery(name = "Verspend.pendenciesCount", query = "SELECT count(*) FROM Verspend WHERE vsnum = :vsnum AND berstat = true AND berdat = :berdat")
public class Verspend extends HistEntity {
	@Id
	private BigDecimal vsnum;
	@Id
	private LocalDate berdat;
	@Id
	private String bercd;

	private boolean berstat;
	private Long beleg;

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

	public boolean isBerstat() {
		return berstat;
	}

	public void setBerstat(boolean berstat) {
		this.berstat = berstat;
	}

	public Long getBeleg() {
		return beleg;
	}

	public void setBeleg(Long beleg) {
		this.beleg = beleg;
	}

	@Override
	public String toString() {
		return "Verspend{" +
		       "vsnum=" + vsnum +
		       ", berdat=" + berdat +
		       ", bercd='" + bercd + '\'' +
		       ", berstat=" + berstat +
		       ", beleg=" + beleg +
		       '}';
	}
}