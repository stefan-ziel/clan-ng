
package ch.claninfo.clanng.benutzer.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import ch.claninfo.clanng.benutzer.entities.pks.RollePk;
import ch.claninfo.clanng.domain.entities.HistEntity;

/**
 * The persistent class for the ROLLE database table.
 */
@Entity
@Table(schema = "ALOW", name = "ROLLE")
@IdClass(RollePk.class)
public class Rolle extends HistEntity implements Serializable, GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false, length = 3)
	private String company;

	@Id
	@Column(name = "ROLLE_ID", unique = true, nullable = false, length = 30)
	private String rolleId;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "COMPANY", referencedColumnName = "COMPANY", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "ROLLE_ID", referencedColumnName = "ROLLE_ID", nullable = false, insertable = false, updatable = false)})
	private Set<Berechtigung> berechtigungs;

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Rolle)) {
			return false;
		}
		Rolle castOther = (Rolle) other;
		return this.company.equals(castOther.company) && this.rolleId.equals(castOther.rolleId);
	}

	@Override
	public String getAuthority() {
		return company + '/' + rolleId;
	}

	public Set<Berechtigung> getBerechtigungen() {
		return berechtigungs;
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

	public void setBerechtigungen(Set<Berechtigung> pBerechtigungs) {
		berechtigungs = pBerechtigungs;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setRolleId(String rolleId) {
		this.rolleId = rolleId;
	}

	@Override
	public String toString() {
		return "Rolle [Company=" + company + ", Rolle=" + rolleId + "]";
	}

}
