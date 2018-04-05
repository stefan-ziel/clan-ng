package ch.claninfo.clanng.jobqueue.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the AUFTRMLDG database table.
 * 
 */
@Embeddable
public class AuftrmldgPk implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false, unique=true, nullable=false, precision=20)
	private long auftragnr;

	@Column(unique=true, nullable=false, precision=8)
	private long lfnr;

	public AuftrmldgPk() {
	}
	public long getAuftragnr() {
		return this.auftragnr;
	}
	public void setAuftragnr(long auftragnr) {
		this.auftragnr = auftragnr;
	}
	public long getLfnr() {
		return this.lfnr;
	}
	public void setLfnr(long lfnr) {
		this.lfnr = lfnr;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AuftrmldgPk)) {
			return false;
		}
		AuftrmldgPk castOther = (AuftrmldgPk)other;
		return 
			(this.auftragnr == castOther.auftragnr)
			&& (this.lfnr == castOther.lfnr);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.auftragnr ^ (this.auftragnr >>> 32)));
		hash = hash * prime + ((int) (this.lfnr ^ (this.lfnr >>> 32)));
		
		return hash;
	}
}