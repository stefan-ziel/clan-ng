
package ch.claninfo.clanng.jobqueue.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the JOBCHAIN database table.
 */
@Entity
@Table(schema = "ALOW", name = "JOBCHAIN")
@NamedQuery(name = "Jobchain.findAll", query = "SELECT j FROM Jobchain j")
public class Jobchain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false, precision = 22)
	private long jobchainid;

	@Column(nullable = false, length = 100)
	private String bez;

	@Column(length = 10)
	private String status;

	// bi-directional many-to-one association to Jobdef
	@OneToMany(mappedBy = "jobchain")
	private List<Jobdef> jobdefs;

	public Jobchain() {}

	public Jobdef addJobdef(Jobdef jobdef) {
		getJobdefs().add(jobdef);
		jobdef.setJobchain(this);

		return jobdef;
	}

	public String getBez() {
		return this.bez;
	}

	public long getJobchainid() {
		return this.jobchainid;
	}

	public List<Jobdef> getJobdefs() {
		return this.jobdefs;
	}

	public String getStatus() {
		return this.status;
	}

	public Jobdef removeJobdef(Jobdef jobdef) {
		getJobdefs().remove(jobdef);
		jobdef.setJobchain(null);

		return jobdef;
	}

	public void setBez(String bez) {
		this.bez = bez;
	}

	public void setJobchainid(long jobchainid) {
		this.jobchainid = jobchainid;
	}

	public void setJobdefs(List<Jobdef> jobdefs) {
		this.jobdefs = jobdefs;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
