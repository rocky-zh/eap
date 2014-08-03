package eap.comps.datastore;

import java.util.HashMap;
import java.util.List;
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
public class DataStore {
	
	public static final String SCOPE_APPLICATION = "APPLICATION";
	public static final String SCOPE_SESSION = "SESSION";
	public static final String SCOPE_REQUEST = "REQUEST";
	public static final String SCOPE_MEM = "MEM";
	
	private static Map<String, IDataScope> dataScopes = new HashMap<String, IDataScope>();  // TODO CONCURR
	
	public static void set(String key, Object value, String scope) {
		dataScopes.get(scope).set(key, value);
	}
	public static Object get(String key, String scope) {
		return dataScopes.get(scope).get(key);
	}
	
	public static void setInRequest(String key, Object value) {
		set(key, value, SCOPE_REQUEST);
	}
	public static Object getInRequest(String key) {
		return get(key, SCOPE_REQUEST);
	}
	
	public static void setInSession(String key, Object value) {
		set(key, value, SCOPE_SESSION);
	}
	public static Object getInSession(String key) {
		return get(key, SCOPE_SESSION);
	}
	
	public static void setInApplication(String key, Object value) {
		set(key, value, SCOPE_APPLICATION);
	}
	public static Object getInApplication(String key) {
		return get(key, SCOPE_APPLICATION);
	}
	
	public static void setInMem(String key, Object value) {
		set(key, value, SCOPE_MEM);
	}
	public static Object getInMem(String key) {
		return get(key, SCOPE_MEM);
	}
	
	public static IDataScope getDataScope(String key) {
		return dataScopes.get(key);
	}
	
	public void setDataScopeList(List<IDataScope> dataScopeList) {
		for (IDataScope dataScope : dataScopeList) {
			dataScopes.put(dataScope.getScopeName(), dataScope);
		}
	}
}