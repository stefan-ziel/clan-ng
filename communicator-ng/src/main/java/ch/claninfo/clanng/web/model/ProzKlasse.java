
package ch.claninfo.clanng.web.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the PROZ_KLASSE database table.
 */
@Entity
@Table(schema = "ALOW", name = "PROZ_KLASSE")
@NamedQuery(name = "ProzKlasse.findAll", query = "SELECT p FROM ProzKlasse p")
public class ProzKlasse implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PROZ_KLASSE_NAME", unique = true, nullable = false, length = 50)
	private String prozKlasseName;

	@Column(name = "CLASS_REF", length = 255)
	private String classRef;

	@Column(length = 2000)
	private String config;

	// bi-directional many-to-one association to ProzDef
	@OneToMany(mappedBy = "prozKlasse")
	private List<ProzDef> prozDefs;

	public ProzKlasse() {}

	public ProzDef addProzDef(ProzDef prozDef) {
		getProzDefs().add(prozDef);
		prozDef.setProzKlasse(this);

		return prozDef;
	}

	public String getClassRef() {
		return this.classRef;
	}

	public String getConfig() {
		return this.config;
	}

	public List<ProzDef> getProzDefs() {
		return this.prozDefs;
	}

	public String getProzKlasseName() {
		return this.prozKlasseName;
	}

	public ProzDef removeProzDef(ProzDef prozDef) {
		getProzDefs().remove(prozDef);
		prozDef.setProzKlasse(null);

		return prozDef;
	}

	public void setClassRef(String classRef) {
		this.classRef = classRef;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setProzDefs(List<ProzDef> prozDefs) {
		this.prozDefs = prozDefs;
	}

	public void setProzKlasseName(String prozKlasseName) {
		this.prozKlasseName = prozKlasseName;
	}

}
