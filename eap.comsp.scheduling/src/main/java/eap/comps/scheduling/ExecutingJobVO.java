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
public class ExecutingJobVO {
	
	private String fireInstanceId;

	private TriggerVO triggerVO;

	private JobDetailVO jobDetailVO;
	
	private boolean recovering = false;

	private int refireCount = 0;

	private Date fireTime;

	private Date scheduledFireTime;

	private Date prevFireTime;

	private Date nextFireTime;
	
	private long jobRunTime = -1;
	
	public String getFireInstanceId() {
		return fireInstanceId;
	}

	public void setFireInstanceId(String fireInstanceId) {
		this.fireInstanceId = fireInstanceId;
	}

	public TriggerVO getTriggerVO() {
		return triggerVO;
	}

	public void setTriggerVO(TriggerVO triggerVO) {
		this.triggerVO = triggerVO;
	}

	public JobDetailVO getJobDetailVO() {
		return jobDetailVO;
	}

	public void setJobDetailVO(JobDetailVO jobDetailVO) {
		this.jobDetailVO = jobDetailVO;
	}

	public boolean isRecovering() {
		return recovering;
	}

	public void setRecovering(boolean recovering) {
		this.recovering = recovering;
	}

	public int getRefireCount() {
		return refireCount;
	}

	public void setRefireCount(int refireCount) {
		this.refireCount = refireCount;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public Date getScheduledFireTime() {
		return scheduledFireTime;
	}

	public void setScheduledFireTime(Date scheduledFireTime) {
		this.scheduledFireTime = scheduledFireTime;
	}

	public Date getPrevFireTime() {
		return prevFireTime;
	}

	public void setPrevFireTime(Date prevFireTime) {
		this.prevFireTime = prevFireTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public long getJobRunTime() {
		return jobRunTime;
	}

	public void setJobRunTime(long jobRunTime) {
		this.jobRunTime = jobRunTime;
	}
}