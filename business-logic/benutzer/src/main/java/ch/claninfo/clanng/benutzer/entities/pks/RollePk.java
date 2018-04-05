
package ch.claninfo.clanng.benutzer.entities.pks;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the ROLLE database table.
 */
@Embeddable
public class RollePk implements Serializable {

	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(unique = true, nullable = false, length = 3)
	private String company;

	@Column(name = "ROLLE_ID", unique = true, nullable = false, length = 30)
	private String rolleId;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RollePk)) {
			return false;
		}
		RollePk castOther = (RollePk) other;
		return this.company.equals(castOther.company) && this.rolleId.equals(castOther.rolleId);
	}

	public String getCompany() {
		return this.company;
	}

	public String getRolleId() {
		return this.rolleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.company.hashCode();
		hash = hash * prime + this.rolleId.hashCode();

		return hash;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setRolleId(String rolleId) {
		this.rolleId = rolleId;
	}
}
