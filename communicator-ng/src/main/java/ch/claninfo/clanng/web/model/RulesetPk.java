
package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RULESET database table.
 */
@Embeddable
public class RulesetPk implements Serializable {

	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private long procnr;

	private String modul;

	private String company;

	private int rulesettyp;

	private int rulesetnr;

	private LocalDateTime gdat;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RulesetPk)) {
			return false;
		}
		RulesetPk castOther = (RulesetPk) other;
		return (this.procnr == castOther.procnr) && this.modul.equals(castOther.modul) && this.company.equals(castOther.company) && (this.rulesettyp == castOther.rulesettyp) && (this.rulesetnr == castOther.rulesetnr) && this.gdat.equals(castOther.gdat);
	}

	public String getCompany() {
		return this.company;
	}

	public LocalDateTime getGdat() {
		return this.gdat;
	}

	public String getModul() {
		return this.modul;
	}

	public long getProcnr() {
		return this.procnr;
	}

	public int getRulesetnr() {
		return this.rulesetnr;
	}

	public int getRulesettyp() {
		return this.rulesettyp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.procnr ^ (this.procnr >>> 32)));
		hash = hash * prime + this.modul.hashCode();
		hash = hash * prime + this.company.hashCode();
		hash = hash * prime + (this.rulesettyp ^ (this.rulesettyp >>> 32));
		hash = hash * prime + (this.rulesetnr ^ (this.rulesetnr >>> 32));
		hash = hash * prime + this.gdat.hashCode();

		return hash;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setGdat(LocalDateTime gdat) {
		this.gdat = gdat;
	}

	public void setModul(String modul) {
		this.modul = modul;
	}

	public void setProcnr(long procnr) {
		this.procnr = procnr;
	}

	public void setRulesetnr(int rulesetnr) {
		this.rulesetnr = rulesetnr;
	}

	public void setRulesettyp(int rulesettyp) {
		this.rulesettyp = rulesettyp;
	}
}
