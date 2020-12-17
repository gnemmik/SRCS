package srcs.workflow.executor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import srcs.workflow.graph.Graph;
import srcs.workflow.job.Context;
import srcs.workflow.job.Job;
import srcs.workflow.job.JobValidator;
import srcs.workflow.job.LinkFrom;

public class JobExecutorSequential extends JobExecutor {

	public JobExecutorSequential(Job job) {
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws Exception {
		JobValidator jobValid = new JobValidator(super.getJob());
		Map<String, Object> results = new HashMap<>();
		Graph<String> graph = jobValid.getTaskGraph();
		int level = 0;
		
		while(graph.size() != results.size()) {
			for(String task : graph) {
				if(graph.getNeighborsIn(task).size() == level) {
					Method method = jobValid.getMethod(task);
					List<Object> params = new ArrayList<>();
					for(Parameter param : method.getParameters()) {
						LinkFrom link;
						if((link = param.getAnnotation(LinkFrom.class)) != null)
							params.add(results.get(link.value()));
						Context context;
						if((context = param.getAnnotation(Context.class)) != null)
							params.add(super.getJob().getContext().get(context.value()));
					}
					results.put(task, method.invoke(super.getJob(), params.toArray()));
				}
			}
			level++;
		}
		return results;
	}

}
