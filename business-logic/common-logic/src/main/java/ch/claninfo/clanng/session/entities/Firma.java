
package ch.claninfo.clanng.session.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.claninfo.clanng.domain.entities.HistEntity;

/**
 * The persistent class for the FIRMA database table.
 */
@Entity
@Table(schema = "ALOW", name = "FIRMA")
public class Firma extends HistEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String company;

	private String bez;
	private String homepage;
	private String pvid;

	public String getBez() {
		return this.bez;
	}

	public String getCompany() {
		return this.company;
	}

	public String getHomepage() {
		return this.homepage;
	}

	public String getPvid() {
		return this.pvid;
	}

	public void setBez(String bez) {
		this.bez = bez;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public void setPvid(String pvid) {
		this.pvid = pvid;
	}
}
