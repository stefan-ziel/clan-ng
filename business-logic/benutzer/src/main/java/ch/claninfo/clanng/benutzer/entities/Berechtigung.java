
package ch.claninfo.clanng.benutzer.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import ch.claninfo.clanng.domain.entities.HistEntity;
import ch.claninfo.clanng.domain.types.PermissionType;
import ch.claninfo.clanng.session.services.ModulService;

/**
 * The persistent class for the BERECHTIGUNG database table.
 */
@Entity
@Table(schema = "ALOW", name = "BERECH_MAP")
public class Berechtigung extends HistEntity implements GrantedAuthority, Serializable {

	private static final PermissionType[] PERMS = PermissionType.values();

	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false, length = 3)
	private String company;

	@Id
	@Column(name = "ROLLE_ID", unique = true, nullable = false, length = 30)
	private String rolleId;

	@Id
	private String modul;

	@Id
	@Column(name = "PERMISSION_TYP")
	private int permissionTyp;

	@Id
	@Column(name = "PERMISSION_NAME")
	private String permissionName;

	private transient String authority;

	public static String buildAuthority(String pCompany, String pModul, int pPermissionTyp, String pPermissionName) {
		return pCompany + '/' + pModul + '/' + (pPermissionTyp >= PERMS.length ? Integer.toString(pPermissionTyp) : PERMS[pPermissionTyp].toString()) + '/' + pPermissionName;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Berechtigung)) {
			return false;
		}
		Berechtigung castOther = (Berechtigung) other;
		return this.company.equals(castOther.company) && this.modul.equals(castOther.modul) && (this.permissionTyp == castOther.permissionTyp) && this.permissionName.equals(castOther.permissionName);
	}

	@Override
	public String getAuthority() {
		if (authority == null) {
			authority = buildAuthority(company, ModulService.owner2Modul(modul), permissionTyp, permissionName);
		}
		return authority;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	public String getModul() {
		return this.modul;
	}

	public String getPermissionName() {
		return this.permissionName;
	}

	public int getPermissionTyp() {
		return this.permissionTyp;
	}

	/**
	 * @return the rolleId
	 */
	public String getRolleId() {
		return rolleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.company.hashCode();
		hash = hash * prime + this.modul.hashCode();
		hash = hash * prime + this.permissionTyp;
		hash = hash * prime + this.permissionName.hashCode();

		return hash;
	}

	/**
	 * @param pCompany the company to set
	 */
	public void setCompany(String pCompany) {
		company = pCompany;
	}

	public void setModul(String modul) {
		this.modul = modul;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public void setPermissionTyp(int permissionTyp) {
		this.permissionTyp = permissionTyp;
	}

	/**
	 * @param pRolleId the rolleId to set
	 */
	public void setRolleId(String pRolleId) {
		rolleId = pRolleId;
	}

	@Override
	public String toString() {
		return getAuthority();
	}

}
