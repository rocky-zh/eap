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
public class AbstractCache implements ICache {
	
	public Map<String, Map<String, String>> getStats() {
		throw new UnsupportedOperationException("not implemented");
	}
	
	public Map<String, Object> get(String[] keys) {
		throw new UnsupportedOperationException("not implemented");
	}

	public Object get(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	public boolean set(String key, Object value) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	public boolean set(String key, Object value, int exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void setWithNoReply(String key, Object value, int exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public Object getAndTouch(String key, int newExp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public boolean add(String key, Object value, int exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void addWithNoReply(String key, Object value, int exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public long decr(String key, long delta) {
		throw new UnsupportedOperationException("not implemented");
	}

	public long decr(String key, long delta, long initValue) {
		throw new UnsupportedOperationException("not implemented");
	}

	public long decr(String key, long delta, long initValue, int exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void decrWithNoReply(String key, long delta) {
		throw new UnsupportedOperationException("not implemented");
	}

	public boolean delete(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void deleteWithNoReply(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void flushAll() {
		throw new UnsupportedOperationException("not implemented");
	}

	public void flushAllWithNoReply() {
		throw new UnsupportedOperationException("not implemented");
	}

	public long incr(String key, long delta) {
		throw new UnsupportedOperationException("not implemented");
	}

	public long incr(String key, long delta, long initValue) {
		throw new UnsupportedOperationException("not implemented");
	}

	public long incr(String key, long delta, long initValue, long exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public long incrWithNoReply(String key, long delta) {
		throw new UnsupportedOperationException("not implemented");
	}

	public boolean replace(String key, Object value, long exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void replaceWithNoReply(String key, Object value, long exp) {
		throw new UnsupportedOperationException("not implemented");
	}

	public boolean touch(String key, long exp) {
		throw new UnsupportedOperationException("not implemented");
	}
}