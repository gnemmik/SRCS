package srcs.workflow.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.rmi.RemoteException;
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

public class JobExecutorParallel extends JobExecutor{
	
	public JobExecutorParallel(Job job) throws RemoteException{
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws Exception {
		Lock lock = new ReentrantLock();
		JobValidator jobValid = new JobValidator(super.getJob());
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
					params.add(super.getJob().getContext().get(context.value()));
			}
			
			try {
				synchronized (lock) {
					results.put(task, method.invoke(super.getJob(), params.toArray()));
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

}
