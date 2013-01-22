package bit.crawl.server;

import java.util.*;
import java.util.concurrent.*;

import bit.crawl.util.Logger;
import bit.crawl.task.CrawlTask;
import bit.crawl.task.CrawlTaskSpec;

/**
 * A server that manages multiple stored tasks.
 * 
 * @author Kunshan Wang
 * 
 */
public class CrawlServer {
	private static Logger logger = new Logger();

	/**
	 * Mapping taskName of a task to a unique TaskRecord instance.
	 */
	private ConcurrentMap<String, TaskRecord> taskRecords = new ConcurrentHashMap<String, TaskRecord>();

	/**
	 * Get a list of all tasks managed by this CrawlServer.
	 * 
	 * @return A list of all tasks in this server.
	 */
	public Collection<TaskRecord> getTasks() {
		return taskRecords.values();
	}

	/**
	 * Add a new task.
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param task
	 *            The new task.
	 * 
	 * @throws TaskAlreadyExistsException
	 *             Thrown if another CrawlTask instance with the same appName
	 *             exists.
	 */
	public synchronized void addTask(CrawlTaskSpec taskSpec) {
		String taskName = taskSpec.getTaskName();
		if (taskRecords.containsKey(taskName)) {
			throw new CrawlServerException(String.format(
					"Task already exists: %s", taskName));
		}
		taskRecords.put(taskName, new TaskRecord(taskSpec));
		logger.info("Task %s added", taskName);
	}

	/**
	 * Remove a task from the server.
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param taskName
	 *            The taskName of the task to remove.
	 * @throws NoSuchTaskException
	 *             Thrown if the task specified by taskName does not exist.
	 */
	public synchronized void removeTask(String taskName) {
		TaskRecord taskRecord = taskRecords.remove(taskName);
		if (taskRecord == null) {
			throw new IllegalArgumentException(String.format(
					"No such task: %s", taskName));
		}

		taskRecord.notifyDeletion();
		logger.info("Task %s removed.", taskName);
	}

	/**
	 * Start a task in the server.
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param appName
	 *            The appName of the task to start.
	 * 
	 * @throws NoSuchTaskException
	 *             If the specified task does not exist.
	 * @throws TaskAlreadyStartedException
	 *             Thrown if the task has already started and has hot finished.
	 */
	public synchronized void startTask(String appName) {
		TaskRecord taskRecord = taskRecords.get(appName);
		if (taskRecord == null) {
			throw new IllegalArgumentException(String.format(
					"No such task: %s", appName));
		}

		taskRecord.startTask();
	}

	/**
	 * Stop a running task.
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param appName
	 *            The appName of the task.
	 * @throws NoSuchTaskException
	 *             Thrown if the specified task does not exist.
	 * @throws TaskNotRunningException
	 *             Thrown if the specified task is not running.
	 */
	public synchronized void stopTask(String appName) {
		TaskRecord taskRecord = taskRecords.get(appName);
		if (taskRecord == null) {
			throw new IllegalArgumentException(String.format(
					"No such task: %s", appName));
		}

		taskRecord.stopTask();
	}

	/**
	 * Shutdown the CrawlServer and immediately stop all running tasks.
	 * 
	 * @author Kunshan Wang
	 * 
	 */
	public synchronized void shutdown() {
		logger.info("Server shutting down.");
		for (Map.Entry<String, TaskRecord> e : taskRecords.entrySet()) {
			TaskRecord taskRecord = e.getValue();
			taskRecord.notifyDeletion();
		}
		taskRecords.clear();
	}

}
