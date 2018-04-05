
package ch.claninfo.clanng.codes.entities.pks;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the CDWERT database table.
 */
@Embeddable
public class CdwertPk implements Serializable {

	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable = false, updatable = false)
	private String cdnam;

	private String modul;

	private String cdwert;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CdwertPk)) {
			return false;
		}
		CdwertPk castOther = (CdwertPk) other;
		return this.cdnam.equals(castOther.cdnam) && this.modul.equals(castOther.modul) && this.cdwert.equals(castOther.cdwert);
	}

	public String getCdnam() {
		return this.cdnam;
	}

	public String getCdwert() {
		return this.cdwert;
	}

	public String getModul() {
		return this.modul;
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

	public void setCdwert(String cdwert) {
		this.cdwert = cdwert;
	}

	public void setModul(String modul) {
		this.modul = modul;
	}
}
