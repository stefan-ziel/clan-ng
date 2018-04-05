/* $Id: JobFacade.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.jobqueue.service;

import java.time.LocalDateTime;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Configurable;

import ch.claninfo.clanng.converters.ClanDateConverter;
import ch.claninfo.clanng.domain.services.JpaUtils;
import ch.claninfo.clanng.jobqueue.entities.Auftrag;
import ch.claninfo.clanng.jobqueue.entities.Jobdef;
import ch.claninfo.clanng.session.entities.ClanSession;
import ch.claninfo.clanng.session.services.SessionUtils;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.saxconnect.SessionFactory;
import ch.claninfo.common.util.batch.JobExecutionInterface;
import ch.claninfo.common.util.batch.queue.ConnectJobHelper;
import ch.claninfo.common.util.batch.queue.ConnectJobInterface;
import ch.claninfo.common.util.batch.queue.JobInterface;

import static ch.claninfo.clanng.jobqueue.entities.Auftrag.Status.ABBRUCH;
import static ch.claninfo.clanng.jobqueue.entities.Auftrag.Status.FEHLER;
import static ch.claninfo.clanng.jobqueue.entities.Auftrag.Status.FERTIG;
import static ch.claninfo.clanng.jobqueue.entities.Auftrag.Status.IN_ARBEIT;
import static ch.claninfo.clanng.jobqueue.entities.Auftrag.Status.NEU;
import static ch.claninfo.clanng.jobqueue.entities.Jobdef.Status.COMPLETED;
import static ch.claninfo.clanng.jobqueue.entities.Jobdef.Status.ERROR;
import static ch.claninfo.clanng.jobqueue.entities.Jobdef.Status.STARTED;
import static ch.claninfo.clanng.jobqueue.entities.Jobdef.Status.WAITING;

/**
 * 
 */
@Configurable
public class JobFacade implements JobInterface {

	private static final int ERROR_LENGTH = JpaUtils.getLength(Auftrag.class, "error"); //$NON-NLS-1$
	private static final int BEZ_LENGTH = JpaUtils.getLength(Auftrag.class, "bez"); //$NON-NLS-1$
	private static final String AUFTRAG = "Auftrag"; //$NON-NLS-1$
	private static final String JOB = "Jobdef"; //$NON-NLS-1$
	private static final String ENDTIME = "Endtime"; //$NON-NLS-1$
	private static final String BR_ABBRUCH = "91002"; //$NON-NLS-1$
	private static final short PRIO_NORMAL = 1;

	@Inject
	EntityManager technicalEntityManager;

	@Inject
	SessionFactory factory;

	JobInterface job;
	JobExecutionInterface jobExecution;
	Auftrag auftrag;
	Jobdef jobdef;

	/**
	 * @param pJob the encapsulated Job
	 * @param pJobExecution execution date time
	 */
	public JobFacade(JobInterface pJob, JobExecutionInterface pJobExecution) {
		if (pJob instanceof ConnectJobInterface) {
			job = new ConnectJobHelper(factory, (ConnectJobInterface) pJob);
		} else {
			job = pJob;
		}
		jobExecution = pJobExecution;
		ClanSession session = SessionUtils.getSession();
		auftrag = new Auftrag();
		auftrag.setBez(trunc(job.getName(), BEZ_LENGTH));
		auftrag.setCompany(session.getCompany());
		auftrag.setMeldung(job.toString());
		auftrag.setPrioritaet(PRIO_NORMAL);
		auftrag.setStatus(NEU);
		technicalEntityManager.persist(auftrag);
		job.setEnvironment(AUFTRAG, auftrag);

		jobdef = new Jobdef(jobExecution);
		jobdef.setJobName(job.getName());
		jobdef.setAuftragUser(session.getUser().getUsername());
		jobdef.setCommand(job.toString());

		technicalEntityManager.persist(jobdef);

		job.setEnvironment(JOB, jobdef);
		job.setEnvironment(ENDTIME, jobExecution.getEndDate());
	}

	@Override
	public void execute() throws CommException {
		auftrag.setStartdatetime(LocalDateTime.now());
		auftrag.setStatus(IN_ARBEIT);

		jobdef.setStatus(STARTED);

		ClanSession session = SessionUtils.getSession();
		session.setAuftragnr(auftrag.getAuftragnr());
		try {
			job.execute();
			LocalDateTime now = LocalDateTime.now();
			auftrag.setEnddatetime(now);
			auftrag.setResultat(job.toString());
			auftrag.setStatus(FERTIG);

			jobdef.setAnzAbgelaufen(jobdef.getAnzAbgelaufen() + 1);
			if (now.isAfter(ClanDateConverter.parseClanDateToTemporal(jobExecution.getEndDate(), LocalDateTime.class))) {
				jobdef.setStatus(COMPLETED);
			} else {
				jobdef.setStatus(WAITING);
			}
		}
		catch (CommException ce) {
			String errorMessage = ce.getMessage();
			auftrag.setEnddatetime(LocalDateTime.now());
			auftrag.setError(trunc(errorMessage, ERROR_LENGTH));
			if (errorMessage.contains(BR_ABBRUCH)) {
				auftrag.setStatus(ABBRUCH);
			} else {
				auftrag.setStatus(FEHLER);
			}

			jobdef.setStatus(ERROR);
			jobdef.setAnzAbgelaufen(-1);
		}
	}

	@Override
	public Object getEnvironment(String pName) {
		return job.getEnvironment(pName);
	}

	@Override
	public String getName() {
		return job.getName();
	}

	@Override
	public void setEnvironment(String pName, Object pData) {
		job.setEnvironment(pName, pData);
	}

	String trunc(String pString, int pLength) {
		if (pString != null && pString.length() > pLength) {
			return pString.substring(0, pLength);
		}
		return pString;
	}
}
