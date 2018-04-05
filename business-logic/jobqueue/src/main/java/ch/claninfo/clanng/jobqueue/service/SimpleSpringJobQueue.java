/* $Id: SimpleSpringJobQueue.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.jobqueue.service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import ch.claninfo.clanng.converters.ClanDateConverter;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.util.batch.JobExecutionInterface;
import ch.claninfo.common.util.batch.parser.ExecutionTimeParser;
import ch.claninfo.common.util.batch.queue.JobInterface;
import ch.claninfo.common.util.batch.queue.JobQueueInterface;

/**
 * Schedule clan jobs to a pre existing quartz scheduler
 */
@Component
public class SimpleSpringJobQueue implements JobQueueInterface {

	static final String JOB_KEY = "job"; //$NON-NLS-1$
	static final AtomicLong INSTANCE_ID_GENERATOR = new AtomicLong(0);

	@Inject
	private TaskScheduler scheduler;

	@Override
	public void enqueu(JobInterface pJob, JobExecutionInterface pExecution) throws CommException {
		try {
			SimpleSpringJob job = new SimpleSpringJob(new JobFacade(pJob, pExecution));
			if (pExecution.isExecuteImmediate()) {
				scheduler.schedule(job, new Date());
			} else {
				CronTrigger trigger = new ClanCronTrigger(ExecutionTimeParser.convert2CrontabExpress(pExecution),
				                                          ClanDateConverter.parseClanDateToTemporal(pExecution.getStartHour(), Instant.class),
				                                          ClanDateConverter.parseClanDateToTemporal(pExecution.getEndDate(), Instant.class));
				scheduler.schedule(job, trigger);
			}
		}
		catch (DateTimeParseException e) {
			throw new CommException(e);
		}
	}

	static class ClanCronTrigger extends CronTrigger {

		Instant startTime;
		Instant endTime;

		/**
		 * @param pExpression
		 * @param pStartTime
		 * @param pEndTime
		 */
		public ClanCronTrigger(String pExpression, Instant pStartTime, Instant pEndTime) {
			super(pExpression);
			startTime = pStartTime;
			endTime = pEndTime;
		}

		@Override
		public Date nextExecutionTime(TriggerContext pTriggerContext) {
			Instant now = Instant.now();
			if (endTime != null && now.isAfter(endTime)) {
				return null;
			}
			if (startTime != null && now.compareTo(startTime) <= 0) {
				return Date.from(startTime);
			}

			return super.nextExecutionTime(pTriggerContext);
		}

	}

	/**
	 * Just executes the job
	 */
	class SimpleSpringJob implements Runnable {

		JobInterface clanJob;

		public SimpleSpringJob(JobInterface pJob) {
			super();
			clanJob = pJob;
		}

		@Override
		public void run() {
			try {
				clanJob.execute();
			}
			catch (CommException e) {
				// TODO
			}
		}
	}

}
