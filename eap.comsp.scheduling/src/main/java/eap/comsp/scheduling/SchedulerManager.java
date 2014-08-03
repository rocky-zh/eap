package eap.comsp.scheduling;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;

import eap.UM;
import eap.util.BeanUtil;
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
public class SchedulerManager implements DisposableBean, Ordered {
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
	
	private static Logger logger = LoggerFactory.getLogger(SchedulerManager.class);
	
	private static Map<String, SchedulerFactoryBean> schedulerFactory = new ConcurrentHashMap<String , SchedulerFactoryBean>();
	
	public static boolean execSchedulerCmd(String name, String cmd) {
		Scheduler scheduler = getSchedulerAndCheck(name);
		
		try {
			if ("start".equals(cmd)) {
				if (scheduler.isShutdown()) {
					scheduler = getNewScheduler(name);
				}
				scheduler.start();
			} else if ("standby".equals(cmd)) {
				scheduler.standby();
			} else if ("stop".equals(cmd)) {
				scheduler.shutdown();
			} else if ("forceStop".equals(cmd)) {
				scheduler.shutdown(true);
			} else if ("pauseAll".equals(cmd)) {
				scheduler.pauseAll();
			} else if ("resumeAll".equals(cmd)) {
				scheduler.resumeAll();
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		
		return true;
	}
	public static boolean execJobCmd(String schedulerName, String jobName, String jobGroup, String cmd, Map<String, Object> data) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		JobKey jobKey = new JobKey(jobName, jobGroup);
		
		try {
			if ("resume".equals(cmd)) {
				scheduler.resumeJob(jobKey);
			} else if ("pause".equals(cmd)) {
				scheduler.pauseJob(jobKey);
			} else if ("run".equals(cmd)) {
				scheduler.triggerJob(jobKey);
			} else if ("delete".equals(cmd)) {
				scheduler.deleteJob(jobKey);
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		
		return true;
	}
	public static boolean execTriggerCmd(String schedulerName, String triggerName, String triggerGroup, String cmd) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
		
		try {
			if ("resume".equals(cmd)) {
				scheduler.resumeTrigger(triggerKey);
			} else if ("pause".equals(cmd)) {
				scheduler.pauseTrigger(triggerKey);
			} else if ("delete".equals(cmd)) {
				scheduler.unscheduleJob(triggerKey);
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		
		return true;
	}
	
	public static Collection<Scheduler> getAllScheduler() {
		return SchedulerRepository.getInstance().lookupAll();
	}
	public static List<String> getSchedulerNames() {
		List<String> names = new ArrayList<String>(schedulerFactory.keySet().size());
		for (String name : schedulerFactory.keySet()) {
			names.add(name);
		}
		return names;
		
//		try {
//			Collection<Scheduler> schedulers = SchedulerRepository.getInstance().lookupAll();
//			List<String> names = new ArrayList<String>(schedulers.size());
//			for (Scheduler scheduler : schedulers) {
//				names.add(scheduler.getSchedulerName());
//			}
//			
//			return names;
//		} catch (SchedulerException e) {
//			return Collections.EMPTY_LIST;
//		}
	}
	
	public static Scheduler getScheduler(String name) {
//		return SchedulerRepository.getInstance().lookup(name);
		SchedulerFactoryBean sfb = schedulerFactory.get(name);
		if (sfb != null) {
			return sfb.getScheduler();
		}
		
		return null;
	}
	public static Scheduler getNewScheduler(String name) {
		SchedulerFactoryBean sfb = schedulerFactory.get(name);
		Assert.isTrue(sfb != null, "scheduler '"+ name +"' not found");
		
		try {
			sfb.destroy();
		} catch (SchedulerException e) {
		}
		
		try {
			sfb.afterPropertiesSet();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return sfb.getObject();
	}
	public static Scheduler getSchedulerAndCheck(String name) {
//		Scheduler scheduler = SchedulerRepository.getInstance().lookup(name);
		Scheduler scheduler = getScheduler(name);
		Assert.isTrue(scheduler != null, "scheduler '"+ name +"' not found");
		
		return scheduler;
	}
	
	public static MetaDataVO getMetaData(String name) {
		Scheduler scheduler = getSchedulerAndCheck(name);
		
		try {
			SchedulerMetaData metaData = scheduler.getMetaData();
			
			MetaDataVO metaDataVO = new MetaDataVO();
			metaDataVO.setSchedulerName(metaData.getSchedulerName());
			metaDataVO.setSchedulerInstanceId(metaData.getSchedulerInstanceId());
			metaDataVO.setSchedulerClass(metaData.getSchedulerClass());
			metaDataVO.setSchedulerRemote(metaData.isSchedulerRemote());
			metaDataVO.setStarted(metaData.isStarted());
			metaDataVO.setInStandbyMode(metaData.isInStandbyMode());
			metaDataVO.setShutdown(metaData.isShutdown());
			metaDataVO.setRunningSince(metaData.getRunningSince());
			metaDataVO.setNumberOfJobsExecuted(metaData.getNumberOfJobsExecuted());
			metaDataVO.setJobStoreClass(metaData.getJobStoreClass());
			metaDataVO.setJobStoreSupportsPersistence(metaData.isJobStoreSupportsPersistence());
			metaDataVO.setJobStoreClustered(metaData.isJobStoreClustered());
			metaDataVO.setThreadPoolClass(metaData.getThreadPoolClass());
			metaDataVO.setThreadPoolSize(metaData.getThreadPoolSize());
			metaDataVO.setVersion(metaData.getVersion());
			metaDataVO.setSummary(metaData.getSummary());
			
			return metaDataVO;
		} catch (SchedulerException e) {
			return null;
		}
		
//		
//		scheduler.getJobGroupNames();
//		scheduler.getTriggersOfJob(jobKey);
	}
	
	public static List<ExecutingJobVO> getCurrentlyExecutingJobs(String schedulerName) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		
		try {
			List<JobExecutionContext> jeCtxList = scheduler.getCurrentlyExecutingJobs();
			List<ExecutingJobVO> ejVOList = new ArrayList<ExecutingJobVO>(jeCtxList.size());
			ExecutingJobVO ejVO = null;
			for (JobExecutionContext jeCtx : jeCtxList) {
				ejVO = new ExecutingJobVO();
				ejVO.setFireInstanceId(jeCtx.getFireInstanceId());
				ejVO.setRecovering(jeCtx.isRecovering());
				ejVO.setRefireCount(jeCtx.getRefireCount());
				ejVO.setFireTime(jeCtx.getFireTime());
				ejVO.setScheduledFireTime(jeCtx.getScheduledFireTime());
				ejVO.setPrevFireTime(jeCtx.getPreviousFireTime());
				ejVO.setNextFireTime(jeCtx.getNextFireTime());
				ejVO.setJobRunTime(jeCtx.getJobRunTime());
				
				ejVO.setJobDetailVO(toJobDetailVO(jeCtx.getJobDetail()));
				ejVO.setTriggerVO(toTriggerVO(jeCtx.getTrigger(), scheduler.getTriggerState(jeCtx.getTrigger().getKey())));
				
				ejVOList.add(ejVO);
			}
			
			return ejVOList;
		} catch (SchedulerException e) {
			return Collections.EMPTY_LIST;
		}
	}
	
	public static List<JobDetailVO> getJobDetailList(String schedulerName, String likeJobGroup, String likeJobName) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		
		try {
			List<JobDetailVO> jobDetailVOList = new ArrayList<JobDetailVO>();
			Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupContains(StringUtil.defaultIfBlank(likeJobGroup, "")));
			boolean hasFilterJobName = StringUtil.isNotBlank(likeJobName);
			for (JobKey jobKey : jobKeys) {
				if (hasFilterJobName) {
					if (!jobKey.getName().contains(likeJobName)) {
						continue;
					}
				}
				
				jobDetailVOList.add(toJobDetailVO(scheduler.getJobDetail(jobKey)));
			}
			
			return jobDetailVOList;
		} catch (SchedulerException e) {
			return Collections.EMPTY_LIST;
		}
	}
	
	public static List<TriggerVO> getTriggerVOListOfJob(String schedulerName, String jobName, String jobGroup) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		
		try {
			List triggerList = scheduler.getTriggersOfJob(new JobKey(jobName, StringUtil.defaultIfEmpty(jobGroup, Scheduler.DEFAULT_GROUP)));
			List<TriggerVO> triggerVOList = new ArrayList<TriggerVO>(triggerList.size());
			for (Object trigger : triggerList) {
				triggerVOList.add(toTriggerVO((Trigger) trigger, scheduler.getTriggerState(((Trigger) trigger).getKey())));
			}
			
			return triggerVOList;
		} catch (SchedulerException e) {
			return Collections.EMPTY_LIST;
		}
	}
	
	public static JobDetailVO getJobDetail(String schedulerName, String name, String group) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		
		try {
			JobDetail jobDetail = scheduler.getJobDetail(new JobKey(name, StringUtil.defaultIfEmpty(group, Scheduler.DEFAULT_GROUP)));
			
			return toJobDetailVO(jobDetail);
		} catch (SchedulerException e) {
			return null;
		}
	}
	
	public static void addJob(String schedulerName, JobDetailVO jobDetailVO) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		JobDetail jobDetail = toJobDetail(jobDetailVO);
		
		try {
			scheduler.addJob(jobDetail, true); // TODO replace
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static void addTrigger(String schedulerName, TriggerVO triggerVO) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		Trigger trigger = toTrigger(triggerVO);
		
		try {
			TriggerKey triggerKey = trigger.getKey();
			if (scheduler.checkExists(triggerKey)) {
				if (Trigger.TriggerState.NORMAL.compareTo(scheduler.getTriggerState(triggerKey)) == 0) {
					scheduler.resumeTrigger(triggerKey);
				}
				scheduler.unscheduleJob(triggerKey);
			}
			
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static TriggerVO getTriggerVO(String schedulerName, String name, String group) {
		Scheduler scheduler = getSchedulerAndCheck(schedulerName);
		
		try {
			TriggerKey triggerKey = new TriggerKey(name, StringUtil.defaultIfEmpty(group, Scheduler.DEFAULT_GROUP));
			Trigger trigger = scheduler.getTrigger(triggerKey);
			
			return toTriggerVO(trigger, scheduler.getTriggerState(triggerKey));
		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	private static JobDetailVO toJobDetailVO(JobDetail jobDetail) {
		JobDetailVO jobDetailVO = new JobDetailVO();
		jobDetailVO.setName(jobDetail.getKey().getName());
		jobDetailVO.setGroup(jobDetail.getKey().getGroup());
		jobDetailVO.setDescription(jobDetail.getDescription());
		jobDetailVO.setJobClass(jobDetail.getJobClass());
		if (jobDetail.getJobDataMap() != null) {
			jobDetailVO.setJobDataMap(new HashMap(jobDetail.getJobDataMap()));	
		}
		
		jobDetailVO.setDurability(jobDetail.isDurable());
		jobDetailVO.setShouldRecover(jobDetail.requestsRecovery());
		jobDetailVO.setConcurrentExectionDisallowed(jobDetail.isConcurrentExectionDisallowed());
		jobDetailVO.setPersistJobDataAfterExecution(jobDetail.isPersistJobDataAfterExecution());
		
		return jobDetailVO;
	}
	private static JobDetail toJobDetail(JobDetailVO jobDetailVO) {
		JobDetailImpl jobDetail = new JobDetailImpl();
		jobDetail.setKey(new JobKey(jobDetailVO.getName(), jobDetailVO.getGroup()));
		jobDetail.setDescription(jobDetailVO.getDescription());
		jobDetail.setJobClass(jobDetailVO.getJobClass());
		if (jobDetailVO.getJobDataMap() != null) {
			jobDetail.setJobDataMap(new JobDataMap(jobDetailVO.getJobDataMap()));
		}
		jobDetail.setDurability(jobDetailVO.isDurability());
		jobDetail.setRequestsRecovery(jobDetailVO.isShouldRecover());
		
		return jobDetail;
	}
	
	private static TriggerVO toTriggerVO(Trigger trigger, TriggerState triggerState) {
		TriggerVO triggerVO = new TriggerVO();
		triggerVO.setName(trigger.getKey().getName());
		triggerVO.setGroup(trigger.getKey().getGroup());
		triggerVO.setJobName(trigger.getJobKey().getName());
		triggerVO.setJobGroup(trigger.getJobKey().getGroup());
		triggerVO.setCalendarName(trigger.getCalendarName());
		triggerVO.setStartTime(trigger.getStartTime());
		triggerVO.setEndTime(trigger.getEndTime());
		triggerVO.setNextFireTime(trigger.getNextFireTime());
		triggerVO.setPreviousFireTime(trigger.getPreviousFireTime());
		triggerVO.setFinalFireTime(trigger.getFinalFireTime());
		triggerVO.setPriority(trigger.getPriority());
		triggerVO.setMayFireAgain(trigger.mayFireAgain());
		triggerVO.setMisfireInstruction(trigger.getMisfireInstruction());
		triggerVO.setDescription(trigger.getDescription());
		triggerVO.setJobDataMap(trigger.getJobDataMap());
		
		if (triggerState != null) {
			triggerVO.setStatus(triggerState.name().toLowerCase());
		}
		
		if (trigger instanceof CronTrigger) {
			CronTrigger ct = (CronTrigger) trigger;
			triggerVO.setType(TriggerVO.TRIGGER_TYPE_CRON);
			triggerVO.setCronEx(ct.getCronExpression());
			triggerVO.setTimeZone(ct.getTimeZone());
		} 
		else if (trigger  instanceof SimpleTrigger) {
			triggerVO.setType(TriggerVO.TRIGGER_TYPE_SIMPLE);
		} 
		else if (trigger instanceof DailyTimeIntervalTrigger) {
			triggerVO.setType(TriggerVO.TRIGGER_TYPE_DAILY_TIME);
		} 
		else if (trigger instanceof CalendarIntervalTrigger) {
			triggerVO.setType(TriggerVO.TRIGGER_TYPE_CALENDAR);
		}
		else {
			triggerVO.setType(trigger.getClass().getName());
		}
		
		return triggerVO;
	}
	
	private static Trigger toTrigger(TriggerVO triggerVO) {
		String type = triggerVO.getType();
		
		try {
			if (TriggerVO.TRIGGER_TYPE_CRON.equals(type)) {
				CronTriggerImpl trigger = new CronTriggerImpl();
				trigger.setName(triggerVO.getName());
				trigger.setGroup(triggerVO.getGroup());
				trigger.setJobName(triggerVO.getJobName());
				trigger.setJobGroup(triggerVO.getJobGroup());
				trigger.setCalendarName(triggerVO.getCalendarName());
				trigger.setStartTime(triggerVO.getStartTime());
				trigger.setEndTime(triggerVO.getEndTime());
				trigger.setPriority(triggerVO.getPriority());
				trigger.setMisfireInstruction(triggerVO.getMisfireInstruction());
				trigger.setDescription(triggerVO.getDescription());
				trigger.setTimeZone(triggerVO.getTimeZone());
				trigger.setCronExpression(triggerVO.getCronEx());
				if (triggerVO.getJobDataMap() != null) {
					trigger.setJobDataMap(new JobDataMap(triggerVO.getJobDataMap()));
				}
				
				return trigger;
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		return null;
	}
	
	public void setSchedulerList(List<Map<String, Object>> schedulerList) {
		Assert.isTrue(schedulerList != null && schedulerList.size() > 0, "'schedulerList' must not be empty");
		
		for (Map<String, Object> schedulerProps : schedulerList) {
			try {
				SchedulerFactoryBean sfb = new SchedulerFactoryBean();
				BeanUtil.copyProperties(schedulerProps, sfb);
				sfb.afterPropertiesSet();
				
				Object startupDelayObj = schedulerProps.get("startupDelay");
				if (startupDelayObj != null && StringUtil.isNumeric(startupDelayObj.toString()) && Integer.parseInt(startupDelayObj.toString()) >= 0) {
					if (UM.isEnabled() && UM.isStarted()) {
						if (UM.isLeader()) {
							sfb.start();
						}
					} else {
						sfb.start();
					}
				}
				
				Scheduler scheduler = sfb.getObject();
			
				schedulerFactory.put(scheduler.getSchedulerName(), sfb);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void destroy() throws Exception {
		for (SchedulerFactoryBean sfb : schedulerFactory.values()) {
			sfb.stop();
			sfb.destroy();
		}
		
		schedulerFactory = null;
	}
}