
package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import ch.claninfo.clanng.domain.types.XMLDocument;
import ch.claninfo.clanng.domain.types.XMLTypeProxy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 * The persistent class for the RULESET database table.
 */
@Entity
@Table(schema = "ALOW", name = "RULESET")
@TypeDefs({@TypeDef(name = "XMLType", typeClass = XMLTypeProxy.class)})
@IdClass(RulesetPk.class)
public class Ruleset implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalDateTime gdat;

	@Id
	private long procnr;

	@Id
	private String modul;

	@Id
	private String company;

	@Id
	private int rulesettyp;

	@Id
	private int rulesetnr;

	private String rulesetbez;

	@Type(type = "XMLType")
	private XMLDocument rulesetxml;

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

	public String getRulesetbez() {
		return this.rulesetbez;
	}

	public int getRulesetnr() {
		return this.rulesetnr;
	}

	public int getRulesettyp() {
		return this.rulesettyp;
	}

	public XMLDocument getRulesetxml() {
		return this.rulesetxml;
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

	public void setRulesetbez(String rulesetbez) {
		this.rulesetbez = rulesetbez;
	}

	public void setRulesetnr(int rulesetnr) {
		this.rulesetnr = rulesetnr;
	}

	public void setRulesettyp(int rulesettyp) {
		this.rulesettyp = rulesettyp;
	}

	@Type(type = "XMLType")
	public void setRulesetxml(XMLDocument rulesetxml) {
		this.rulesetxml = rulesetxml;
	}

}
