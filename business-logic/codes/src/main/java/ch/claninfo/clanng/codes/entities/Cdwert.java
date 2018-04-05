
package ch.claninfo.clanng.codes.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ch.claninfo.clanng.codes.entities.pks.CdwertPk;
import ch.claninfo.clanng.domain.entities.HistEntity;

/**
 * The persistent class for the CDWERT database table.
 */
@Entity
@Table(schema = "ALOW", name = "CDWERT")
@IdClass(CdwertPk.class)
public class Cdwert extends HistEntity {

	@Id
	@Column(insertable = false, updatable = false)
	private String modul;

	@Id
	@Column(insertable = false, updatable = false)
	private String cdnam;

	@Id
	@Column(updatable = false)
	private String cdwert;

	private Byte sysexcl;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumns({//
								@JoinColumn(name = "MODUL", referencedColumnName = "MODUL", nullable = false, insertable = false, updatable = false), //
								@JoinColumn(name = "CDNAM", referencedColumnName = "CDNAM", nullable = false, insertable = false, updatable = false), //
								@JoinColumn(name = "CDWERT", referencedColumnName = "CDWERT", nullable = false, insertable = false, updatable = false)//
	})
	private List<Cdwbez> cdwbezs;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Cdwert)) {
			return false;
		}
		Cdwert castOther = (Cdwert) other;
		return this.cdnam.equals(castOther.cdnam) && this.modul.equals(castOther.modul) && this.cdwert.equals(castOther.cdwert);
	}

	public String getCdnam() {
		return this.cdnam;
	}

	public List<Cdwbez> getCdwbezs() {
		return this.cdwbezs;
	}

	public String getCdwert() {
		return this.cdwert;
	}

	public String getModul() {
		return this.modul;
	}

	public Byte getSysexcl() {
		return this.sysexcl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.cdnam.hashCode();
		hash = hash * prime + this.modul.hashCode();
		hash = hash * prime + this.cdwert.hashCode();

		return hash;
	}

	public void setCdnam(String cdnam) {
		this.cdnam = cdnam;
	}

	public void setCdwbezs(List<Cdwbez> cdwbezs) {
		this.cdwbezs = cdwbezs;
	}

	public void setCdwert(String cdwert) {
		this.cdwert = cdwert;
	}

	public void setModul(String modul) {
		this.modul = modul;
	}

	public void setSysexcl(Byte sysexcl) {
		this.sysexcl = sysexcl;
	}

	@Override
	public String toString() {
		return "Cdwert{" + "modul='" + modul + '\'' + ", cdnam='" + cdnam + '\'' + ", cdwert='" + cdwert + '\'' + ", sysexcl=" + sysexcl + ", cdwbezs=" + cdwbezs + '}';
	}

}
