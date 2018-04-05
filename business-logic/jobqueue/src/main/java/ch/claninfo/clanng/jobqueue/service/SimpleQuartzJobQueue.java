/* $Id: SimpleQuartzJobQueue.java 1167 2017-04-28 21:26:17Z $ */

package ch.claninfo.clanng.jobqueue.service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import ch.claninfo.clanng.converters.ClanDateConverter;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.util.batch.JobExecutionInterface;
import ch.claninfo.common.util.batch.parser.ExecutionTimeParser;
import ch.claninfo.common.util.batch.queue.JobInterface;
import ch.claninfo.common.util.batch.queue.JobQueueInterface;

/**
 * Schedule clan jobs to a pre existing quartz scheduler
 */
public class SimpleQuartzJobQueue implements JobQueueInterface {

	static final String JOB_KEY = "job"; //$NON-NLS-1$
	static final AtomicLong INSTANCE_ID_GENERATOR = new AtomicLong(0);
	static SimpleQuartzJobQueue instance;
	Scheduler scheduler;

	/**
	 * @param pFactory Connection Factory
	 * @param pScheduler a quartz scheduler
	 */
	private SimpleQuartzJobQueue(Scheduler pScheduler) {
		super();
		scheduler = pScheduler;
	}

	/**
	 * this mut be called once to initilize the queue.
	 * 
	 * @param pScheduler a Quartz Scheduler
	 * @return the queue singleton
	 */
	public static SimpleQuartzJobQueue initQueue(Scheduler pScheduler) {
		assert instance == null : "instance already initialized"; //$NON-NLS-1$
		if (instance == null) {
			instance = new SimpleQuartzJobQueue(pScheduler);
		}
		return instance;
	}

	/**
	 * @return the instance
	 */
	static SimpleQuartzJobQueue getInstance() {
		assert instance != null : "no instance initialized"; //$NON-NLS-1$
		return instance;
	}

	@Override
	public void enqueu(JobInterface pJob, JobExecutionInterface pExecution) throws CommException {
		try {
			JobDataMap data = new JobDataMap();
			data.put(JOB_KEY, pJob);
			JobDetail job = JobBuilder.newJob(getJobClass()).withIdentity("NF-JOB-" + System.currentTimeMillis() + '-' + INSTANCE_ID_GENERATOR.incrementAndGet(), "CLAN-NG").usingJobData(data).build(); //$NON-NLS-1$ //$NON-NLS-2$

			TriggerBuilder<Trigger> tbuilder = TriggerBuilder.newTrigger().forJob(job).withIdentity("NG-TRIGGER-" + System.currentTimeMillis() + '-' + INSTANCE_ID_GENERATOR.incrementAndGet(), "CLAN-NG"); //$NON-NLS-1$ //$NON-NLS-2$

			if (pExecution.isExecuteImmediate()) {
				tbuilder.startNow();
			} else {
				String crontabExpression = ExecutionTimeParser.convert2CrontabExpress(pExecution);
				tbuilder.withSchedule(CronScheduleBuilder.cronSchedule(crontabExpression).withMisfireHandlingInstructionDoNothing());

				String endeString = pExecution.getEndDate();
				if (endeString != null) {
					tbuilder.endAt(Date.from(ClanDateConverter.parseClanDateToTemporal(endeString, Instant.class)));
				}

				String startString = pExecution.getStartHour();
				if (startString != null) {
					tbuilder.startAt(Date.from(ClanDateConverter.parseClanDateToTemporal(startString, Instant.class)));
				} else {
					tbuilder.startNow();
				}
			}
			scheduler.scheduleJob(job, tbuilder.build());

		}
		catch (DateTimeParseException | SchedulerException e) {
			throw new CommException(e);
		}
	}

	/**
	 * @return the actual JobClass
	 */
	protected Class<? extends Job> getJobClass() {
		return SimpleQuartzJob.class;
	}

	/**
	 * Just executes the job
	 */
	protected static class SimpleQuartzJob implements Job {

		@Override
		public void execute(JobExecutionContext pContext) throws JobExecutionException {
			try {
				JobInterface clanJob = (JobInterface) pContext.get(JOB_KEY);
				clanJob.execute();
			}
			catch (CommException e) {
				throw new JobExecutionException(e, false);
			}
		}
	}

}
