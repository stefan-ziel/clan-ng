
package ch.claninfo.clanng.jobqueue.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ch.claninfo.clanng.converters.ClanDateConverter;
import ch.claninfo.common.util.batch.CommonBatchConstants;
import ch.claninfo.common.util.batch.JobExecutionInterface;

/**
 * The persistent class for the JOBDEF database table.
 */
@Entity
@Table(schema = "ALOW", name = "JOBDEF")
@NamedQuery(name = "Jobdef.findAll", query = "SELECT j FROM Jobdef j")
public class Jobdef implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "JOBDEF_NUM", unique = true, nullable = false, precision = 20)
	private long jobdefNum;

	@Column(name = "ABORT_AFTER_ERROR", length = 1)
	private String abortAfterError;

	@Column(name = "ANZ_ABGELAUFEN", precision = 6)
	private int anzAbgelaufen;

	@Column(name = "ANZ_WIEDERHOLUNG", precision = 6)
	private int anzWiederholung;

	@Column(name = "AUFTRAG_DATUM")
	private LocalDateTime auftragDatum;

	@Column(name = "AUFTRAG_USER", length = 30)
	private String auftragUser;

	@Column(name = "BENUTZTER_JOB_NAME", length = 256)
	private String benutzterJobName;

	@Lob
	@Column(name = "\"COMMAND\"")
	private String command;

	@Column(name = "CRONTAB_START_DATE")
	private LocalDateTime crontabStartDate;

	@Column(precision = 1)
	private BigDecimal enabled;

	@Column(name = "IS_RECOVERABLE", length = 1)
	private String isRecoverable;

	@Column(length = 80)
	private String jahr;

	@Column(name = "JOB_BEENDET_DATUM")
	private LocalDateTime jobBeendetDatum;

	@Column(name = "JOB_BESCHREIBUNG", length = 256)
	private String jobBeschreibung;

	@Column(name = "JOB_GROUP", length = 80)
	private String jobGroup;

	@Column(name = "JOB_NAME", length = 80)
	private String jobName;

	@Column(name = "JOB_TYPE", length = 10)
	private String jobType;

	@Column(name = "\"MINUTE\"", length = 80)
	private String minute;

	@Column(length = 80)
	private String monat;

	@Column(name = "\"SECOND\"", length = 80)
	private String second;

	@Column(precision = 3)
	private BigDecimal sortid;

	@Column(length = 16)
	private String status;

	@Column(length = 80)
	private String stunde;

	@Column(name = "TAEGLICH_PERIODISCH_FREQUENZ", length = 80)
	private String taeglichPeriodischFrequenz;

	@Column(name = "TAEGLICH_SERIE_LAUFTYP", length = 2)
	private String taeglichSerieLauftyp;

	@Column(name = "TAEGLICH_WIE_OFT", length = 6)
	private String taeglichWieOft;

	@Column(name = "TAG_DER_WOCHE", length = 80)
	private String tagDerWoche;

	@Column(name = "TAG_DES_MONATES", length = 80)
	private String tagDesMonates;

	@Column(name = "TRIGGER_GROUP", length = 80)
	private String triggerGroup;

	@Column(name = "TRIGGER_NAME", length = 80)
	private String triggerName;

	// bi-directional many-to-one association to Jobchain
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JOBCHAINID")
	private Jobchain jobchain;

	// bi-directional many-to-one association to Jobdef
//	@OneToMany(mappedBy = "jobdef")
//	private List<Jobdef> jobdefs;

	public Jobdef() {}

	public Jobdef(JobExecutionInterface jobExecution) {
		setJobGroup(CommonBatchConstants.JOB_GROUP);
		setTriggerName(CommonBatchConstants.TRIGGER_NAME);
		setTriggerGroup(CommonBatchConstants.TRIGGER_GROUP);
		setStatus(Status.WAITING);
		setAnzWiederholung(jobExecution.getIteration());
		setAnzAbgelaufen(0);
		setTagDerWoche(jobExecution.getDayOfWeek());
		setTagDesMonates(jobExecution.getDayOfMonth());
		setMonat(jobExecution.getMonth());
		setJahr(jobExecution.getYear());
		setStunde(jobExecution.getHours());
		setMinute(jobExecution.getMinutes());
		setSecond("0"); //$NON-NLS-1$
		setAuftragDatum(LocalDateTime.now());
		setJobType(jobExecution.getJobType());
		setJobBeendetDatum(ClanDateConverter.parseClanDateToTemporal(jobExecution.getEndDate(), LocalDateTime.class));
		setBenutzterJobName(jobExecution.getUserJobName());
		setTaeglichSerieLauftyp(jobExecution.getDailyRunType());
		setTaeglichWieOft(jobExecution.getDailyCrontabHowOften());
		setTaeglichPeriodischFrequenz(jobExecution.getDailyCrontabFrequency());
		setJobBeschreibung(jobExecution.getDescription());
		setIsRecoverable(jobExecution.isRecoverable());
		setCrontabStartDate(ClanDateConverter.parseClanDateToTemporal(jobExecution.getCrontabStartDate(), LocalDateTime.class));
	}

	public String getAbortAfterError() {
		return this.abortAfterError;
	}

	public int getAnzAbgelaufen() {
		return this.anzAbgelaufen;
	}

	public int getAnzWiederholung() {
		return this.anzWiederholung;
	}

	public LocalDateTime getAuftragDatum() {
		return this.auftragDatum;
	}

	public String getAuftragUser() {
		return this.auftragUser;
	}

	public String getBenutzterJobName() {
		return this.benutzterJobName;
	}

	public String getCommand() {
		return this.command;
	}

	public LocalDateTime getCrontabStartDate() {
		return this.crontabStartDate;
	}

	public BigDecimal getEnabled() {
		return this.enabled;
	}

	public String getIsRecoverable() {
		return this.isRecoverable;
	}

	public String getJahr() {
		return this.jahr;
	}

	public LocalDateTime getJobBeendetDatum() {
		return this.jobBeendetDatum;
	}

	public String getJobBeschreibung() {
		return this.jobBeschreibung;
	}

	public Jobchain getJobchain() {
		return this.jobchain;
	}

	public long getJobdefNum() {
		return this.jobdefNum;
	}

//	public List<Jobdef> getJobdefs() {
//		return this.jobdefs;
//	}

	public String getJobGroup() {
		return this.jobGroup;
	}

	public String getJobName() {
		return this.jobName;
	}

	public String getJobType() {
		return this.jobType;
	}

	public String getMinute() {
		return this.minute;
	}

	public String getMonat() {
		return this.monat;
	}

	public String getSecond() {
		return this.second;
	}

	public BigDecimal getSortid() {
		return this.sortid;
	}

	public String getStatus() {
		return this.status;
	}

	public String getStunde() {
		return this.stunde;
	}

	public String getTaeglichPeriodischFrequenz() {
		return this.taeglichPeriodischFrequenz;
	}

	public String getTaeglichSerieLauftyp() {
		return this.taeglichSerieLauftyp;
	}

	public String getTaeglichWieOft() {
		return this.taeglichWieOft;
	}

	public String getTagDerWoche() {
		return this.tagDerWoche;
	}

	public String getTagDesMonates() {
		return this.tagDesMonates;
	}

	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	public String getTriggerName() {
		return this.triggerName;
	}

	public void setAbortAfterError(String abortAfterError) {
		this.abortAfterError = abortAfterError;
	}

	public void setAnzAbgelaufen(int anzAbgelaufen) {
		this.anzAbgelaufen = anzAbgelaufen;
	}

	public void setAnzWiederholung(int anzWiederholung) {
		this.anzWiederholung = anzWiederholung;
	}

	public void setAuftragDatum(LocalDateTime auftragDatum) {
		this.auftragDatum = auftragDatum;
	}

	public void setAuftragUser(String auftragUser) {
		this.auftragUser = auftragUser;
	}

	public void setBenutzterJobName(String benutzterJobName) {
		this.benutzterJobName = benutzterJobName;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setCrontabStartDate(LocalDateTime crontabStartDate) {
		this.crontabStartDate = crontabStartDate;
	}

	public void setEnabled(BigDecimal enabled) {
		this.enabled = enabled;
	}

	public void setIsRecoverable(String isRecoverable) {
		this.isRecoverable = isRecoverable;
	}

	public void setJahr(String jahr) {
		this.jahr = jahr;
	}

	public void setJobBeendetDatum(LocalDateTime jobBeendetDatum) {
		this.jobBeendetDatum = jobBeendetDatum;
	}

	public void setJobBeschreibung(String jobBeschreibung) {
		this.jobBeschreibung = jobBeschreibung;
	}

	public void setJobchain(Jobchain jobchain) {
		this.jobchain = jobchain;
	}

	public void setJobdefNum(long jobdefNum) {
		this.jobdefNum = jobdefNum;
	}

//	public void setJobdefs(List<Jobdef> jobdefs) {
//		this.jobdefs = jobdefs;
//	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public void setMonat(String monat) {
		this.monat = monat;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public void setSortid(BigDecimal sortid) {
		this.sortid = sortid;
	}

	public void setStatus(Status status) {
		setStatus(Integer.toString(status.ordinal()));
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStunde(String stunde) {
		this.stunde = stunde;
	}

	public void setTaeglichPeriodischFrequenz(String taeglichPeriodischFrequenz) {
		this.taeglichPeriodischFrequenz = taeglichPeriodischFrequenz;
	}

	public void setTaeglichSerieLauftyp(String taeglichSerieLauftyp) {
		this.taeglichSerieLauftyp = taeglichSerieLauftyp;
	}

	public void setTaeglichWieOft(String taeglichWieOft) {
		this.taeglichWieOft = taeglichWieOft;
	}

	public void setTagDerWoche(String tagDerWoche) {
		this.tagDerWoche = tagDerWoche;
	}

	public void setTagDesMonates(String tagDesMonates) {
		this.tagDesMonates = tagDesMonates;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	/**
	 * Status information
	 */
	public enum Status {
		/** unused */
		UNUSED0,
		/** in progress */
		STARTED,
		/** not in progress */
		WAITING,
		/** completed */
		COMPLETED,
		/** unused */
		UNUSED4,
		/** unused */
		UNUSED5,
		/** unused */
		UNUSED6,
		/** unused */
		UNUSED7,
		/** failed */
		ERROR
	}

}
