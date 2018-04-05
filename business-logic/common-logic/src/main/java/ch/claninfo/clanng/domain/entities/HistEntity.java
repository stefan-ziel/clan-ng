
package ch.claninfo.clanng.domain.entities;

import java.time.LocalDateTime;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import ch.claninfo.clanng.session.services.SessionUtils;

@MappedSuperclass
public abstract class HistEntity extends ClanEntity {

	@NotNull
	private LocalDateTime dc;

	@NotNull
	private String uc;
	private LocalDateTime dm;
	private String um;

	public LocalDateTime getDc() {
		return dc;
	}

	public LocalDateTime getDm() {
		return dm;
	}

	public LocalDateTime getLastUpdate() {
		if (dm == null) {
			if (dc == null) {
				return LocalDateTime.MIN;
			}
			return dc;
		}
		return dm;
	}

	public String getUc() {
		return uc;
	}

	public String getUm() {
		return um;
	}

	public void setDc(LocalDateTime dc) {
		this.dc = dc;
	}

	public void setDm(LocalDateTime dm) {
		this.dm = dm;
	}

	public void setUc(String uc) {
		this.uc = uc;
	}

	public void setUm(String um) {
		this.um = um;
	}

	@PrePersist
	public void updated() {
		if (dc == null) {
			dc = LocalDateTime.now();
			uc = SessionUtils.getUserName();
		} else {
			dm = LocalDateTime.now();
			um = SessionUtils.getUserName();
		}
	}
}
