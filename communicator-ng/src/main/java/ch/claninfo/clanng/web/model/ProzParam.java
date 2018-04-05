
package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the PROZ_PARAM database table.
 */
@Entity
@Table(schema = "ALOW", name = "PROZ_PARAM")
@NamedQuery(name = "ProzParam.findAll", query = "SELECT p FROM ProzParam p")
public class ProzParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ProzParamPK id;

	@Column(name = "JAVA_TYPE_NAME", length = 40)
	private String javaTypeName;

	@Column(precision = 1)
	private BigDecimal parman;

	// bi-directional many-to-one association to ProzParamZuord
	@OneToMany(mappedBy = "prozParam")
	private List<ProzParamZuord> prozParamZuords;

	public ProzParam() {}

	public ProzParamZuord addProzParamZuord(ProzParamZuord prozParamZuord) {
		getProzParamZuords().add(prozParamZuord);
		prozParamZuord.setProzParam(this);

		return prozParamZuord;
	}

	public ProzParamPK getId() {
		return this.id;
	}

	public String getJavaTypeName() {
		return this.javaTypeName;
	}

	public BigDecimal getParman() {
		return this.parman;
	}

	public List<ProzParamZuord> getProzParamZuords() {
		return this.prozParamZuords;
	}

	public ProzParamZuord removeProzParamZuord(ProzParamZuord prozParamZuord) {
		getProzParamZuords().remove(prozParamZuord);
		prozParamZuord.setProzParam(null);

		return prozParamZuord;
	}

	public void setId(ProzParamPK id) {
		this.id = id;
	}

	public void setJavaTypeName(String javaTypeName) {
		this.javaTypeName = javaTypeName;
	}

	public void setParman(BigDecimal parman) {
		this.parman = parman;
	}

	public void setProzParamZuords(List<ProzParamZuord> prozParamZuords) {
		this.prozParamZuords = prozParamZuords;
	}

}
