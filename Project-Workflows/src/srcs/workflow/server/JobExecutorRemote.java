package srcs.workflow.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.job.Job;

public interface JobExecutorRemote extends Remote{
	public Map<String, Object> execute(Job job) throws Exception;
	int getTasksTreated(String jobName) throws RemoteException;
}
