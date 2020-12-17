package srcs.workflow.server.distributed;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.executor.JobExecutor;
import srcs.workflow.job.Job;
import srcs.workflow.server.JobExecutorRemote;
import srcs.workflow.server.central.JobTrackerCentral;

public class JobExecutorRemoteDistributed extends JobExecutor{

	public JobExecutorRemoteDistributed(Job job) {
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws Exception {
		Map<String, Object> res = null;
		int treated;
		// Déploiement de la méthode sur le serveur
		JobTrackerCentral.main(null);
		try {
			Remote r = Naming.lookup("rmi://127.0.0.1/executeRMI");
			if(r instanceof JobExecutorRemote) {
				res = ((JobExecutorRemote) r).execute(getJob());
				
				treated = ((JobExecutorRemote) r).getTasksTreated(getJob().getName());
				System.out.println(treated);
			}
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return res;
	}

	
}
