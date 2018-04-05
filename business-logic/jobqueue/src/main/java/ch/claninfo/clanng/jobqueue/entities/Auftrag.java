
package ch.claninfo.clanng.jobqueue.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * The persistent class for the AUFTRAG database table.
 */
@Entity
@Table(schema = "ALOW", name = "AUFTRAG")
@NamedQuery(name = "Auftrag.findAll", query = "SELECT a FROM Auftrag a")
public class Auftrag implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(unique = true, nullable = false, precision = 20)
	@SequenceGenerator(name = "AUFTRAGNR", schema = "ALOW", sequenceName = "PROC_ID")
	@GeneratedValue(strategy = SEQUENCE, generator = "AUFTRAGNR")
	private long auftragnr;

	@Column(length = 255)
	private String bez;

	@Column(length = 3)
	private String company;

	private LocalDateTime enddatetime;

	@Column(length = 2000)
	private String error;

	@Column(name = "JOBDEF_NUM", precision = 20)
	private BigDecimal jobdefNum;

	@Lob
	private String meldung;

	@Column(precision = 1)
	private short prioritaet;

	@Lob
	private String resultat;

	@Column(name = "RUN_DATETIME")
	private LocalDateTime runDatetime;

	private LocalDateTime startdatetime;

	@Column(precision = 2)
	private short status;

	// bi-directional many-to-one association to Auftrmldg
	@OneToMany(mappedBy = "auftrag")
	private List<Auftrmldg> auftrmldgs;

	public Auftrag() {}

	public Auftrmldg addAuftrmldg(Auftrmldg auftrmldg) {
		getAuftrmldgs().add(auftrmldg);
		auftrmldg.setAuftrag(this);

		return auftrmldg;
	}

	public long getAuftragnr() {
		return this.auftragnr;
	}

	public List<Auftrmldg> getAuftrmldgs() {
		return this.auftrmldgs;
	}

	public String getBez() {
		return this.bez;
	}

	public String getCompany() {
		return this.company;
	}

	public LocalDateTime getEnddatetime() {
		return this.enddatetime;
	}

	public String getError() {
		return this.error;
	}

	public BigDecimal getJobdefNum() {
		return this.jobdefNum;
	}

	public String getMeldung() {
		return this.meldung;
	}

	public short getPrioritaet() {
		return this.prioritaet;
	}

	public String getResultat() {
		return this.resultat;
	}

	public LocalDateTime getRunDatetime() {
		return this.runDatetime;
	}

	public LocalDateTime getStartdatetime() {
		return this.startdatetime;
	}

	public short getStatus() {
		return this.status;
	}

	public Auftrmldg removeAuftrmldg(Auftrmldg auftrmldg) {
		getAuftrmldgs().remove(auftrmldg);
		auftrmldg.setAuftrag(null);

		return auftrmldg;
	}

	public void setAuftragnr(long auftragnr) {
		this.auftragnr = auftragnr;
	}

	public void setAuftrmldgs(List<Auftrmldg> auftrmldgs) {
		this.auftrmldgs = auftrmldgs;
	}

	public void setBez(String bez) {
		this.bez = bez;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setEnddatetime(LocalDateTime enddatetime) {
		this.enddatetime = enddatetime;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setJobdefNum(BigDecimal jobdefNum) {
		this.jobdefNum = jobdefNum;
	}

	public void setMeldung(String meldung) {
		this.meldung = meldung;
	}

	public void setPrioritaet(short prioritaet) {
		this.prioritaet = prioritaet;
	}

	public void setResultat(String resultat) {
		this.resultat = resultat;
	}

	public void setRunDatetime(LocalDateTime runDatetime) {
		this.runDatetime = runDatetime;
	}

	public void setStartdatetime(LocalDateTime startdatetime) {
		this.startdatetime = startdatetime;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public void setStatus(Status status) {
		setStatus((short) status.ordinal());
	}

	/** Auftrag status */
	public enum Status {
		/** unused */
		UNUSED0,
		/** not yet iniciated */
		NEU,
		/** working */
		IN_ARBEIT,
		/** terminated with success */
		FERTIG,
		/** unused */
		UNUSED4,
		/** unused */
		UNUSED5,
		/** unused */
		UNUSED6,
		/** unused */
		UNUSED7,
		/** terminated with error */
		FEHLER,
		/** unused */
		UNUSED9,
		/** terminated with user abort */
		ABBRUCH
	}

}
