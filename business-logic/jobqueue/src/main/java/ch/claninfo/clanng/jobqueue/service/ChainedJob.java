/* $Id: ChainedJob.java 1232 2017-05-30 15:46:54Z zis $ */

package ch.claninfo.clanng.jobqueue.service;

import java.util.ArrayList;

import ch.claninfo.clanng.jobqueue.entities.Jobchain;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.util.batch.queue.JobInterface;

/**
 * 
 */
public class ChainedJob extends ArrayList<JobInterface> implements JobInterface {

	Jobchain jobchain;

	@Override
	public void execute() throws CommException {
		for (JobInterface job : this) {
			job.execute();
		}
	}

	@Override
	public Object getEnvironment(String pName) {
		return get(0).getEnvironment(pName);
	}

	@Override
	public String getName() {
		return get(0).getName();
	}

	@Override
	public void setEnvironment(String pName, Object pData) {
		for (JobInterface job : this) {
			job.setEnvironment(pName, pData);
		}
	}
}
