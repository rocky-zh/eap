package eap.comps.scheduling;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import eap.util.JsonUtil;
import eap.util.StringUtil;

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
public class TriggerVO {
	
	public static final String TRIGGER_TYPE_CRON = "cron";
	public static final String TRIGGER_TYPE_SIMPLE = "simple";
	public static final String TRIGGER_TYPE_DAILY_TIME = "dailyTime";
	public static final String TRIGGER_TYPE_CALENDAR = "calendar";
	
	private String type = TRIGGER_TYPE_CRON;
	private String name;
	private String group;
	private String jobName;
	private String jobGroup;
	private String calendarName;
	private Date startTime;
	private Date endTime;
	private Date nextFireTime;
	private Date previousFireTime;
	private Date finalFireTime;
	private int priority;
	private boolean mayFireAgain;
	private int misfireInstruction;
	private String description;
	
	private String cronEx;
	private TimeZone timeZone;
	
	private String status;
	
	private Map jobDataMap;
	private String jobParams;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getCalendarName() {
		return calendarName;
	}
	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getNextFireTime() {
		return nextFireTime;
	}
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}
	public Date getPreviousFireTime() {
		return previousFireTime;
	}
	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}
	public Date getFinalFireTime() {
		return finalFireTime;
	}
	public void setFinalFireTime(Date finalFireTime) {
		this.finalFireTime = finalFireTime;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public boolean isMayFireAgain() {
		return mayFireAgain;
	}
	public void setMayFireAgain(boolean mayFireAgain) {
		this.mayFireAgain = mayFireAgain;
	}
	public int getMisfireInstruction() {
		return misfireInstruction;
	}
	public void setMisfireInstruction(int misfireInstruction) {
		this.misfireInstruction = misfireInstruction;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCronEx() {
		return cronEx;
	}
	public void setCronEx(String cronEx) {
		this.cronEx = cronEx;
	}
	public TimeZone getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Map getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(Map jobDataMap) {
		this.jobDataMap = jobDataMap;
		
		if (jobDataMap != null) {
			this.jobParams = JsonUtil.toJson(jobDataMap);
		}
	}

	public String getJobParams() {
		return jobParams;
	}
	
	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
		
		if (StringUtil.isNotBlank(jobParams)) {
			this.jobDataMap = JsonUtil.parseJson(jobParams, HashMap.class); // TODO htmlUnescape
		}
	}
}