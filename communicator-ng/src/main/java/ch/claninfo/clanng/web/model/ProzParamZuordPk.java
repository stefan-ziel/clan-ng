package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PROZ_PARAM_ZUORD database table.
 * 
 */
@Embeddable
public class ProzParamZuordPk implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false, unique=true, nullable=false, length=3)
	private String company;

	@Column(insertable=false, updatable=false, unique=true, nullable=false, length=30)
	private String modul;

	@Column(name="PROZ_DEF_NAME", insertable=false, updatable=false, unique=true, nullable=false, length=50)
	private String prozDefName;

	@Column(name="PROZ_PARAM_NAME", insertable=false, updatable=false, unique=true, nullable=false, length=50)
	private String prozParamName;

	public ProzParamZuordPk() {
	}
	public String getCompany() {
		return this.company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getModul() {
		return this.modul;
	}
	public void setModul(String modul) {
		this.modul = modul;
	}
	public String getProzDefName() {
		return this.prozDefName;
	}
	public void setProzDefName(String prozDefName) {
		this.prozDefName = prozDefName;
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
		if (!(other instanceof ProzParamZuordPk)) {
			return false;
		}
		ProzParamZuordPk castOther = (ProzParamZuordPk)other;
		return 
			this.company.equals(castOther.company)
			&& this.modul.equals(castOther.modul)
			&& this.prozDefName.equals(castOther.prozDefName)
			&& this.prozParamName.equals(castOther.prozParamName);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.company.hashCode();
		hash = hash * prime + this.modul.hashCode();
		hash = hash * prime + this.prozDefName.hashCode();
		hash = hash * prime + this.prozParamName.hashCode();
		
		return hash;
	}
}