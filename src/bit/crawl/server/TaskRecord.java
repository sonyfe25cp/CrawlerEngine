package bit.crawl.server;

import java.util.*;

import bit.crawl.task.CrawlTask;
import bit.crawl.task.CrawlTaskSpec;

public class TaskRecord {
	private CrawlTaskSpec taskSpec;
	private Date dateAdded;

	public enum Status {
		READY, RUNNING, STOPPED
	}
	
	private CrawlTask runningTask;
	private CrawlTaskThread runningThread;
	
	public CrawlTaskSpec getTaskSpec() {
		return taskSpec;
	}

	public Status getStatus() {
		if (runningTask==null) {
			return Status.READY;
		} else {
			return Status.RUNNING;
		}
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public TaskRecord(CrawlTaskSpec taskSpec) {
		super();
		this.taskSpec = taskSpec;
		this.dateAdded = new Date();
		this.runningTask = null;
	}

	public synchronized void stopTask() {
		if (this.runningTask != null) {
			this.runningTask.stop();
		}
		
		this.runningTask = null;
		this.runningThread = null;
	}

	public synchronized void notifyDeletion() {
		stopTask();
	}

	public synchronized void startTask() {
		if(this.runningTask != null) {
			throw new CrawlServerException("Task already started.");
		}
		
		this.runningTask = taskSpec.createCrawlTask();
	}
	
	private synchronized void onTaskFinished() {
		this.runningTask = null;
		this.runningThread = null;
	}
	
	public class CrawlTaskThread extends Thread {
		@Override
		public void run() {
			try {
				runningTask.run();
			} finally {
				onTaskFinished();
			}
		}
	}
}