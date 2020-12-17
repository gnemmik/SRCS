package srcs.workflow.executor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.job.Job;

public abstract class JobExecutor implements Remote{
	private Job job;
	
	public JobExecutor(Job job) throws RemoteException{
		this.job = job;
	}

	public abstract Map<String, Object> execute() throws Exception;
	
	public Job getJob() {
		return job;
	}
}
