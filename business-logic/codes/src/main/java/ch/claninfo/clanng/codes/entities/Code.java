
package ch.claninfo.clanng.codes.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ch.claninfo.clanng.domain.entities.HistEntity;

/**
 * The persistent class for the CODES database table.
 */
@Entity
@Table(schema = "ALOW", name = "CODES")
@NamedQuery(name = "Code.findAll", query = "SELECT c FROM Code c")
public class Code extends HistEntity {

	@Id
	private String cdnam;

	private Byte sysexcl;

	// uni-directional many-to-one association to Cdwert
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDNAM", referencedColumnName = "CDNAM", nullable = false, insertable = false, updatable = false)
	private List<Cdwert> cdwerts;

	public String getCdnam() {
		return this.cdnam;
	}

	public List<Cdwert> getCdwerts() {
		return this.cdwerts;
	}

	public Byte getSysexcl() {
		return this.sysexcl;
	}

	public void setCdnam(String cdnam) {
		this.cdnam = cdnam;
	}

	public void setCdwerts(List<Cdwert> cdwerts) {
		this.cdwerts = cdwerts;
	}

	public void setSysexcl(Byte sysexcl) {
		this.sysexcl = sysexcl;
	}

}
