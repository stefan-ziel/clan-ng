package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PROZ_PARAM database table.
 * 
 */
@Embeddable
public class ProzParamPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(unique=true, nullable=false, length=30)
	private String modul;

	@Column(name="PROZ_PARAM_NAME", unique=true, nullable=false, length=50)
	private String prozParamName;

	public ProzParamPK() {
	}
	public String getModul() {
		return this.modul;
	}
	public void setModul(String modul) {
		this.modul = modul;
	}
	public String getProzParamName() {
		return this.prozParamName;
	}
	public void setProzParamName(String prozParamName) {
		this.prozParamName = prozParamName;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ProzParamPK)) {
			return false;
		}
		ProzParamPK castOther = (ProzParamPK)other;
		return 
			this.modul.equals(castOther.modul)
			&& this.prozParamName.equals(castOther.prozParamName);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.modul.hashCode();
		hash = hash * prime + this.prozParamName.hashCode();
		
		return hash;
	}
}