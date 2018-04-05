
package ch.claninfo.clanng.session.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ch.claninfo.clanng.domain.entities.HistEntity;

/**
 * The persistent class for the MODUL database table.
 */
@Entity
@Table(schema = "ALOW", name = "MODUL")
@NamedQuery(name = "Modul.findAll", query = "SELECT m FROM Modul m")
public class Modul extends HistEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	// uni-directional many-to-many association to Base modules
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "ALOW", name = "MOD_HIER", //
			joinColumns = @JoinColumn(name = "MODUL_KIND", referencedColumnName = "DB_OWNER", nullable = false, insertable = false, updatable = false), //
			inverseJoinColumns = @JoinColumn(name = "MODUL_ELTER", referencedColumnName = "DB_OWNER", nullable = false, insertable = false, updatable = false))
	private Set<Modul> baseModules;

	@Id
	@Column(name = "DB_OWNER")
	private String dbOwner;

	private String bez;

	@Column(name = "DB_ROLLE")
	private String dbRolle;

	private String projekt;

	public Set<Modul> getBaseModules() {
		return baseModules;
	}

	public String getBez() {
		return this.bez;
	}

	public String getDbOwner() {
		return this.dbOwner;
	}

	public String getDbRolle() {
		return this.dbRolle;
	}

	public String getProjekt() {
		return this.projekt;
	}

	public void setBaseModules(Set<Modul> pBaseModules) {
		baseModules = pBaseModules;
	}

	public void setBez(String bez) {
		this.bez = bez;
	}

	public void setDbOwner(String dbOwner) {
		this.dbOwner = dbOwner;
	}

	public void setDbRolle(String dbRolle) {
		this.dbRolle = dbRolle;
	}

	public void setProjekt(String projekt) {
		this.projekt = projekt;
	}

}
