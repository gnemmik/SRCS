package srcs.workflow.server.central;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import srcs.workflow.server.JobExecutorParallelRemote;
import srcs.workflow.server.JobExecutorRemote;

public class JobTrackerCentral {
	
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(1099);
			JobExecutorRemote jobexe = new JobExecutorParallelRemote();
			String url = "rmi://"+ InetAddress.getLocalHost().getHostAddress() + "/executeRMI";
			Naming.rebind(url, jobexe);

		} catch (RemoteException e) {
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
