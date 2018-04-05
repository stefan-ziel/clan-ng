package ch.claninfo.clanng.codes.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import ch.claninfo.clanng.codes.entities.pks.CdwbezPk;
import ch.claninfo.clanng.domain.entities.HistEntity;

/**
 * The persistent class for the CDWBEZ database table.
 */
@Entity
@Table(schema = "ALOW", name = "CDWBEZ")
//@NamedQuery(name = "Cdwbez.findAll", query = "SELECT c FROM Cdwbez c")
@IdClass(CdwbezPk.class)
public class Cdwbez extends HistEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(insertable = false, updatable = false)
	private String modul;

	@Id
	@Column(insertable = false, updatable = false)
	private String cdnam;

	@Id
	@Column(insertable = false, updatable = false)
	private String cdwert;

	private int sprcd;

	private String cdwbezk;

	private String cdwbezl;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Cdwbez)) {
			return false;
		}
		Cdwbez castOther = (Cdwbez) other;
		return this.cdnam.equals(castOther.cdnam) && this.modul.equals(castOther.modul) && this.cdwert.equals(castOther.cdwert) && (this.sprcd == castOther.sprcd);
	}

	public String getCdnam() {
		return this.cdnam;
	}

	public String getCdwbezk() {
		return this.cdwbezk;
	}

	public String getCdwbezl() {
		return this.cdwbezl;
	}

	public String getCdwert() {
		return this.cdwert;
	}

	public String getModul() {
		return this.modul;
	}

	public int getSprcd() {
		return this.sprcd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.cdnam.hashCode();
		hash = hash * prime + this.modul.hashCode();
		hash = hash * prime + this.cdwert.hashCode();
		hash = hash * prime + (this.sprcd ^ (this.sprcd >>> 32));

		return hash;
	}

	public void setCdnam(String cdnam) {
		this.cdnam = cdnam;
	}

	public void setCdwbezk(String cdwbezk) {
		this.cdwbezk = cdwbezk;
	}

	public void setCdwbezl(String cdwbezl) {
		this.cdwbezl = cdwbezl;
	}

	public void setCdwert(String cdwert) {
		this.cdwert = cdwert;
	}

	public void setModul(String modul) {
		this.modul = modul;
	}

	public void setSprcd(int sprcd) {
		this.sprcd = sprcd;
	}

	@Override
	public String toString() {
		return "Cdwbez{" +
		       "modul='" + modul + '\'' +
		       ", cdnam='" + cdnam + '\'' +
		       ", cdwert='" + cdwert + '\'' +
		       ", sprcd=" + sprcd +
		       ", cdwbezk='" + cdwbezk + '\'' +
		       ", cdwbezl='" + cdwbezl + '\'' +
		       '}';
	}
}