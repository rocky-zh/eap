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
public interface IEapContextHolder {
	public Env getEnv();
	public TopicManager getTopicManager();
	public UserDetailsVO getUserDetailsVO();
	public Locale getLocale();
	public String getIp();
	public <T> T get(String key, Class<T> requireType);
}