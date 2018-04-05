
package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the PROZ_DEF database table.
 */
@Entity
@Table(schema = "ALOW", name = "PROZ_DEF")
@NamedQuery(name = "ProzDef.findAll", query = "SELECT p FROM ProzDef p")
public class ProzDef implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ProzDefPK id;

	@Column(length = 2000)
	private String config;

	@Lob
	@Column(name = "PROZESS_DEF")
	private String prozessDef;

	// bi-directional many-to-one association to ProzKlasse
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROZ_KLASSE_NAME", nullable = false)
	private ProzKlasse prozKlasse;

	// bi-directional many-to-one association to ProzParamZuord
	@OneToMany(mappedBy = "prozDef")
	private List<ProzParamZuord> prozParamZuords;

	public ProzDef() {}

	public ProzParamZuord addProzParamZuord(ProzParamZuord prozParamZuord) {
		getProzParamZuords().add(prozParamZuord);
		prozParamZuord.setProzDef(this);

		return prozParamZuord;
	}

	public String getConfig() {
		return this.config;
	}

	public ProzDefPK getId() {
		return this.id;
	}

	public String getProzessDef() {
		return this.prozessDef;
	}

	public ProzKlasse getProzKlasse() {
		return this.prozKlasse;
	}

	public List<ProzParamZuord> getProzParamZuords() {
		return this.prozParamZuords;
	}

	public ProzParamZuord removeProzParamZuord(ProzParamZuord prozParamZuord) {
		getProzParamZuords().remove(prozParamZuord);
		prozParamZuord.setProzDef(null);

		return prozParamZuord;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setId(ProzDefPK id) {
		this.id = id;
	}

	public void setProzessDef(String prozessDef) {
		this.prozessDef = prozessDef;
	}

	public void setProzKlasse(ProzKlasse prozKlasse) {
		this.prozKlasse = prozKlasse;
	}

	public void setProzParamZuords(List<ProzParamZuord> prozParamZuords) {
		this.prozParamZuords = prozParamZuords;
	}

}
