package eap;

import java.util.Locale;

import eap.base.UserDetailsVO;

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
public class EapContext {
	
	private static IEapContextHolder holder = new IEapContextHolder() {
		public UserDetailsVO getUserDetailsVO() {
			return null;
		}
		public Env getEnv() {
			Env env = new Env();
			env.refresh(null);
			return env;
		}
		public TopicManager getTopicManager() {
			return new TopicManager();
		}
		public Locale getLocale() {
			return Locale.getDefault();
		}
		public String getIp() {
			return "127.0.0.1";
		}
		public <T> T get(String key, Class<T> requireType) {
			return null;
		}
	};
	
	public static void init(IEapContextHolder holder) {
		EapContext.holder = holder;
	}
	public static IEapContextHolder getHolder() {
		return holder;
	}
	
	public static Env getEnv() {
		checkInit();
		return holder.getEnv();
	}
	
	public static UserDetailsVO getUserDetailsVO() {
		checkInit();
		return holder.getUserDetailsVO();
	}
	
	public static Locale getLocale() {
		checkInit();
		return holder.getLocale();
	}
	
	public static String getIp() {
		checkInit();
		return holder.getIp();
	}
	
	public static <T> T get(String key, Class<T> requireType) {
		checkInit();
		return holder.get(key, requireType);
	}
	
	public static void publish(String topic, Object data) {
		checkTopicManager();
		holder.getTopicManager().publish(topic, data);
	}
	public static void subscribe(String topic, TopicListener listener) {
		checkTopicManager();
		holder.getTopicManager().subscribe(topic, listener);
	}
	public static void unsubscribe(String topic) {
		checkTopicManager();
		holder.getTopicManager().unsubscribe(topic);
	}
	public static void unsubscribe(String topic, TopicListener listener) {
		checkTopicManager();
		holder.getTopicManager().unsubscribe(topic, listener);
	}
	
	private static void checkInit() {
		if (holder == null) {
			throw new IllegalStateException("holder not initialized");
		}
	}
	
	private static void checkTopicManager() {
		checkInit();
		if (holder.getTopicManager() == null) {
			throw new IllegalStateException("topicManager not initialized");
		}
	}
}