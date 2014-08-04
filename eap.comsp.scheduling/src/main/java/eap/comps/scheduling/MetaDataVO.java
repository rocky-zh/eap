package eap.comps.scheduling;

import java.util.Date;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class MetaDataVO {
	
	private String schedulerName;

	private String schedulerInstanceId;

	private Class schedulerClass;

	private boolean schedulerRemote;

	private boolean started;

	private boolean inStandbyMode;

	private boolean shutdown;

	private Date runningSince;

	private int numberOfJobsExecuted;

	private Class jobStoreClass;

	private boolean jobStoreSupportsPersistence;

	private boolean jobStoreClustered;

	private Class threadPoolClass;

	private int threadPoolSize;

	private String version;
	
	private String summary;
	
	public String getStatus() {
		 if (shutdown) {
			 return "stopped";
		 } 
		else if (inStandbyMode) {
			return "paused";
		}
		else {
				return "started";
		}
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getSchedulerInstanceId() {
		return schedulerInstanceId;
	}

	public void setSchedulerInstanceId(String schedulerInstanceId) {
		this.schedulerInstanceId = schedulerInstanceId;
	}

	public Class getSchedulerClass() {
		return schedulerClass;
	}

	public void setSchedulerClass(Class schedulerClass) {
		this.schedulerClass = schedulerClass;
	}

	public boolean isSchedulerRemote() {
		return schedulerRemote;
	}

	public void setSchedulerRemote(boolean schedulerRemote) {
		this.schedulerRemote = schedulerRemote;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isInStandbyMode() {
		return inStandbyMode;
	}

	public void setInStandbyMode(boolean inStandbyMode) {
		this.inStandbyMode = inStandbyMode;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public Date getRunningSince() {
		return runningSince;
	}

	public void setRunningSince(Date runningSince) {
		this.runningSince = runningSince;
	}

	public int getNumberOfJobsExecuted() {
		return numberOfJobsExecuted;
	}

	public void setNumberOfJobsExecuted(int numberOfJobsExecuted) {
		this.numberOfJobsExecuted = numberOfJobsExecuted;
	}

	public Class getJobStoreClass() {
		return jobStoreClass;
	}

	public void setJobStoreClass(Class jobStoreClass) {
		this.jobStoreClass = jobStoreClass;
	}

	public boolean isJobStoreSupportsPersistence() {
		return jobStoreSupportsPersistence;
	}

	public void setJobStoreSupportsPersistence(boolean jobStoreSupportsPersistence) {
		this.jobStoreSupportsPersistence = jobStoreSupportsPersistence;
	}

	public boolean isJobStoreClustered() {
		return jobStoreClustered;
	}

	public void setJobStoreClustered(boolean jobStoreClustered) {
		this.jobStoreClustered = jobStoreClustered;
	}

	public Class getThreadPoolClass() {
		return threadPoolClass;
	}

	public void setThreadPoolClass(Class threadPoolClass) {
		this.threadPoolClass = threadPoolClass;
	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}