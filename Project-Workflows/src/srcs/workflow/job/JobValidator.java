package srcs.workflow.job;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import srcs.workflow.graph.Graph;
import srcs.workflow.graph.GraphImpl;

public class JobValidator {
	private Graph<String> graph;
	private Job job;
	
	public JobValidator(Job job) throws ValidationException {
		graph = new GraphImpl<String>();
		
		for(Method method : job.getClass().getDeclaredMethods()) {
			Task task;
			if((task = method.getAnnotation(Task.class)) != null) {
				if(Modifier.isStatic(method.getModifiers())) 
					throw new ValidationException();
				if(method.getReturnType().toString().equals("void"))
					throw new ValidationException();
				if(graph.existNode(task.value()))
					throw new ValidationException();
				graph.addNode(task.value());
			}
		}
		if(graph.isEmpty()) throw new ValidationException();
		
		for(Method method : job.getClass().getDeclaredMethods()) {
			Task task;
			if((task = method.getAnnotation(Task.class)) != null) {
				for(Parameter param : method.getParameters()) {
					if(!param.isAnnotationPresent(Context.class) && !param.isAnnotationPresent(LinkFrom.class))
						throw new ValidationException();
					LinkFrom link;
					if((link = param.getAnnotation(LinkFrom.class)) != null) {
						if(!graph.existNode(link.value()))
							throw new ValidationException();
						for(Method m : job.getClass().getDeclaredMethods()) {
							Task t;
							if((t = m.getAnnotation(Task.class)) != null && t.value().equals(link.value())) 
								if(!param.getType().isAssignableFrom(m.getReturnType()))
									throw new ValidationException();
						}
						graph.addEdge(link.value(), task.value());
					}
					Context context;
					if((context = param.getAnnotation(Context.class)) != null) {
						if(!job.getContext().containsKey(context.value()))
							throw new ValidationException();
						Object object = job.getContext().get(context.value());
						if(!param.getType().isAssignableFrom(object.getClass()))
							 throw new ValidationException();
					}
				}
			}
		}
		if(!graph.isDAG()) throw new ValidationException();
		this.job = job;
	}

	public Job getJob() {
		return job;
	}

	public Graph<String> getTaskGraph() {
		return graph;
	}

	public Method getMethod(String id){
		Method m = null;
		if(graph.existNode(id)) {
			for(Method method : job.getClass().getDeclaredMethods()) {
				Task task;
				if((task = method.getAnnotation(Task.class)) != null)
					if(task.value().equals(id))
						m = method;
			}
		}
		else {
			throw new IllegalArgumentException();
		}
		return m;
	}
}
