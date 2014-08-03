package eap.comsp.scheduling;

import java.util.HashMap;
import java.util.Map;

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
public class JobDetailVO {
	
	private String name;

	private String group;

	private String description;

	private Class jobClass;

	private Map jobDataMap;
	private String jobParams;

	private boolean durability = false; // 完成后持续

	private boolean shouldRecover = false; // 恢复
	
	private boolean concurrentExectionDisallowed;
	
	private boolean persistJobDataAfterExecution;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class jobClass) {
		this.jobClass = jobClass;
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

	public boolean isDurability() {
		return durability;
	}

	public void setDurability(boolean durability) {
		this.durability = durability;
	}

	public boolean isShouldRecover() {
		return shouldRecover;
	}

	public void setShouldRecover(boolean shouldRecover) {
		this.shouldRecover = shouldRecover;
	}

	public boolean isConcurrentExectionDisallowed() {
		return concurrentExectionDisallowed;
	}

	public void setConcurrentExectionDisallowed(boolean concurrentExectionDisallowed) {
		this.concurrentExectionDisallowed = concurrentExectionDisallowed;
	}

	public boolean isPersistJobDataAfterExecution() {
		return persistJobDataAfterExecution;
	}

	public void setPersistJobDataAfterExecution(boolean persistJobDataAfterExecution) {
		this.persistJobDataAfterExecution = persistJobDataAfterExecution;
	}
}