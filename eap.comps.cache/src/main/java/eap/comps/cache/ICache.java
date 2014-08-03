package eap.comps.cache;

import java.util.Map;

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
public interface ICache {
	
	public Map<String, Map<String,String>> getStats();
	
	public Map<String, Object> get(String[] keys);
	public Object get(String key);
	public boolean set(String key, Object value);
	public boolean set(String key, Object value, int exp);
	public void setWithNoReply(String key, Object value, int exp);
	public Object getAndTouch(String key, int newExp);
	public boolean add(String key, Object value, int exp);
	public void addWithNoReply(String key, Object value, int exp);
	public long decr(String key, long delta);
	public long decr(String key, long delta, long initValue);
	public long decr(String key, long delta, long initValue, int exp);
	public void decrWithNoReply(String key, long delta);
	public boolean delete(String key);
	public void deleteWithNoReply(String key);
	public void flushAll();
	public void flushAllWithNoReply();
	public long incr(String key, long delta);
	public long incr(String key, long delta, long initValue);
	public long incr(String key, long delta, long initValue, long exp);
	public long incrWithNoReply(String key, long delta);
	public boolean replace(String key, Object value, long exp);
	public void replaceWithNoReply(String key, Object value, long exp);
	public boolean touch(String key, long exp);
}