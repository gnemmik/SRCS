package srcs.workflow.server.distributed;

public class TaskTracker {

	private String name;
	private int tasksAtSameTime;

	public TaskTracker(String name, int tasksAtSameTime) {
		this.name = name;
		this.tasksAtSameTime = tasksAtSameTime;
	}
	
	
}
