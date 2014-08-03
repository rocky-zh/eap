package eap.comps.datastore.scope;

import org.springframework.cache.Cache;

import eap.comps.datastore.DataStore;
import eap.comps.datastore.IDataScope;

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
public class MemScopeImpl implements IDataScope {
	
	private Cache cache;
	
	public String getScopeName() {
		return DataStore.SCOPE_MEM;
	}
	public Object getLock() {
		return cache;
	}

	public void set(String key, Object value) {
		cache.put(key, value);
	}

	public Object get(String key) {
		return cache.get(key);
	}
	
	public void delete(String key) {
		cache.evict(key);
	}
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
