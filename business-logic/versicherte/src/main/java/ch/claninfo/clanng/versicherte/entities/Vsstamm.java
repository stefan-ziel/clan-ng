package ch.claninfo.clanng.versicherte.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ch.claninfo.clanng.domain.entities.HistEntity;

@Entity
@Table(name = "VSSTAMM", schema = "PEKA")
@NamedQuery(name = "Vsstam.findByVsnum", query = "select v from Vsstamm v where vsnum = :vsnum")
public class Vsstamm extends HistEntity {
	@Id
	private BigDecimal vsnum;
	private LocalDate einfid;
	private LocalDate einvod;
	private LocalDate ausfid;
	private LocalDate ausvod;
	private Long ausgrund;
	private boolean pendenz;
	private boolean aktiv;
	private short gesvorb;
	private Long vsnumAtmi;

	public BigDecimal getVsnum() {
		return vsnum;
	}

	public void setVsnum(BigDecimal vsnum) {
		this.vsnum = vsnum;
	}

	public LocalDate getEinfid() {
		return einfid;
	}

	public void setEinfid(LocalDate einfid) {
		this.einfid = einfid;
	}

	public LocalDate getEinvod() {
		return einvod;
	}

	public void setEinvod(LocalDate einvod) {
		this.einvod = einvod;
	}

	public LocalDate getAusfid() {
		return ausfid;
	}

	public void setAusfid(LocalDate ausfid) {
		this.ausfid = ausfid;
	}

	public LocalDate getAusvod() {
		return ausvod;
	}

	public void setAusvod(LocalDate ausvod) {
		this.ausvod = ausvod;
	}

	public Long getAusgrund() {
		return ausgrund;
	}

	public void setAusgrund(Long ausgrund) {
		this.ausgrund = ausgrund;
	}

	public boolean isPendenz() {
		return pendenz;
	}

	public void setPendenz(boolean pendenz) {
		this.pendenz = pendenz;
	}

	public boolean isAktiv() {
		return aktiv;
	}

	public void setAktiv(boolean aktiv) {
		this.aktiv = aktiv;
	}

	public short getGesvorb() {
		return gesvorb;
	}

	public void setGesvorb(short gesvorb) {
		this.gesvorb = gesvorb;
	}

	public Long getVsnumAtmi() {
		return vsnumAtmi;
	}

	public void setVsnumAtmi(Long vsnumAtmi) {
		this.vsnumAtmi = vsnumAtmi;
	}
}