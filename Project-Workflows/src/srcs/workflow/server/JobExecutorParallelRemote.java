package srcs.workflow.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import srcs.workflow.job.Context;
import srcs.workflow.job.Job;
import srcs.workflow.job.JobValidator;
import srcs.workflow.job.LinkFrom;

public class JobExecutorParallelRemote extends UnicastRemoteObject implements JobExecutorRemote{
	private Map<String,Integer> tasksTreated;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5870790174161492982L;

	public JobExecutorParallelRemote() throws RemoteException{
		super();
		tasksTreated = new HashMap<>();
	}


	@Override
	public synchronized Map<String, Object> execute(Job job) throws Exception {
		Lock lock = new ReentrantLock();
		JobValidator jobValid = new JobValidator(job);
		
		tasksTreated.put(job.getName(), 0);
		
		Map<String, Object> results = new HashMap<>();
		List<Thread> threads = new ArrayList<>();
		
		for(String task : jobValid.getTaskGraph()) {
			
			threads.add(new Thread(() -> {
			Method method = jobValid.getMethod(task);
			List<Object> params = new ArrayList<>();
			for(Parameter param : method.getParameters()) {
				
				LinkFrom link;
				if((link = param.getAnnotation(LinkFrom.class)) != null) {
					synchronized (lock) {
						while(!results.containsKey(link.value())) {
							try {
								lock.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						params.add(results.get(link.value()));
					}
				}
				
				Context context;
				if((context = param.getAnnotation(Context.class)) != null)
					params.add(job.getContext().get(context.value()));
			}
			
			try {
				synchronized (lock) {
					results.put(task, method.invoke(job, params.toArray()));
					tasksTreated.put(job.getName(), tasksTreated.get(job.getName())+1);
					lock.notifyAll();
				}
				
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}}));
		}
		for(Thread thread : threads) {
			thread.start();
		}
		
		for(Thread thread : threads) {
			thread.join();
		}
		return results;
	}


	@Override
	public int getTasksTreated(String jobName) throws RemoteException {
		return tasksTreated.get(jobName);
	}


}
