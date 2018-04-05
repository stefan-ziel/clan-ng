
package ch.claninfo.clanng.jobqueue.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the AUFTRMLDG database table.
 */
@Entity
@Table(schema = "ALOW", name = "AUFTRMLDG")
@NamedQuery(name = "Auftrmldg.findAll", query = "SELECT a FROM Auftrmldg a")
@IdClass(AuftrmldgPk.class)
public class Auftrmldg implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(insertable = false, updatable = false, unique = true, nullable = false, precision = 20)
	private long auftragnr;

	@Id
	@Column(unique = true, nullable = false, precision = 8)
	private long lfnr;

	@Lob
	private String meldung;

	@Column(nullable = false, length = 4000)
	private String mestext;

	@Column(nullable = false, length = 1)
	private String mestyp;

	// bi-directional many-to-one association to Auftrag
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUFTRAGNR", nullable = false, insertable = false, updatable = false)
	private Auftrag auftrag;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Auftrmldg)) {
			return false;
		}
		Auftrmldg castOther = (Auftrmldg) other;
		return (this.auftragnr == castOther.auftragnr) && (this.lfnr == castOther.lfnr);
	}

	public Auftrag getAuftrag() {
		return this.auftrag;
	}

	public long getAuftragnr() {
		return this.auftragnr;
	}

	public long getLfnr() {
		return this.lfnr;
	}

	public String getMeldung() {
		return this.meldung;
	}

	public String getMestext() {
		return this.mestext;
	}

	public String getMestyp() {
		return this.mestyp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.auftragnr ^ (this.auftragnr >>> 32)));
		hash = hash * prime + ((int) (this.lfnr ^ (this.lfnr >>> 32)));

		return hash;
	}

	public void setAuftrag(Auftrag auftrag) {
		this.auftrag = auftrag;
	}

	public void setAuftragnr(long auftragnr) {
		this.auftragnr = auftragnr;
	}

	public void setLfnr(long lfnr) {
		this.lfnr = lfnr;
	}

	public void setMeldung(String meldung) {
		this.meldung = meldung;
	}

	public void setMestext(String mestext) {
		this.mestext = mestext;
	}

	public void setMestyp(String mestyp) {
		this.mestyp = mestyp;
	}

}
