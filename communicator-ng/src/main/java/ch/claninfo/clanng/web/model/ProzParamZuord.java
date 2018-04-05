
package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the PROZ_PARAM_ZUORD database table.
 */
@Entity
@Table(schema = "ALOW", name = "PROZ_PARAM_ZUORD")
@IdClass(ProzParamZuordPk.class)
public class ProzParamZuord implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(insertable = false, updatable = false, unique = true, nullable = false, length = 3)
	private String company;

	@Id
	@Column(insertable = false, updatable = false, unique = true, nullable = false, length = 30)
	private String modul;

	@Id
	@Column(name = "PROZ_DEF_NAME", insertable = false, updatable = false, unique = true, nullable = false, length = 50)
	private String prozDefName;

	@Id
	@Column(name = "PROZ_PARAM_NAME", insertable = false, updatable = false, unique = true, nullable = false, length = 50)
	private String prozParamName;

	@Column(name = "\"ALIAS\"", length = 50)
	private String alias;

	@Column(precision = 1)
	private BigDecimal parenabled;

	@Column(precision = 1)
	private BigDecimal parman;

	// bi-directional many-to-one association to ProzDef
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "COMPANY", referencedColumnName = "COMPANY", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "MODUL", referencedColumnName = "MODUL", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "PROZ_DEF_NAME", referencedColumnName = "PROZ_DEF_NAME", nullable = false, insertable = false, updatable = false)})
	private ProzDef prozDef;

	// bi-directional many-to-one association to ProzParam
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "MODUL", referencedColumnName = "MODUL", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "PROZ_PARAM_NAME", referencedColumnName = "PROZ_PARAM_NAME", nullable = false, insertable = false, updatable = false)})
	private ProzParam prozParam;

	public ProzParamZuord() {}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ProzParamZuord)) {
			return false;
		}
		ProzParamZuord castOther = (ProzParamZuord) other;
		return this.company.equals(castOther.company) && this.modul.equals(castOther.modul) && this.prozDefName.equals(castOther.prozDefName) && this.prozParamName.equals(castOther.prozParamName);
	}

	public String getAlias() {
		return this.alias;
	}

	public String getCompany() {
		return this.company;
	}

	public String getModul() {
		return this.modul;
	}

	public BigDecimal getParenabled() {
		return this.parenabled;
	}

	public BigDecimal getParman() {
		return this.parman;
	}

	public ProzDef getProzDef() {
		return this.prozDef;
	}

	public String getProzDefName() {
		return this.prozDefName;
	}

	public ProzParam getProzParam() {
		return this.prozParam;
	}

	public String getProzParamName() {
		return this.prozParamName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.company.hashCode();
		hash = hash * prime + this.modul.hashCode();
		hash = hash * prime + this.prozDefName.hashCode();
		hash = hash * prime + this.prozParamName.hashCode();

		return hash;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setModul(String modul) {
		this.modul = modul;
	}

	public void setParenabled(BigDecimal parenabled) {
		this.parenabled = parenabled;
	}

	public void setParman(BigDecimal parman) {
		this.parman = parman;
	}

	public void setProzDef(ProzDef prozDef) {
		this.prozDef = prozDef;
	}

	public void setProzDefName(String prozDefName) {
		this.prozDefName = prozDefName;
	}

	public void setProzParam(ProzParam prozParam) {
		this.prozParam = prozParam;
	}

	public void setProzParamName(String prozParamName) {
		this.prozParamName = prozParamName;
	}

}
