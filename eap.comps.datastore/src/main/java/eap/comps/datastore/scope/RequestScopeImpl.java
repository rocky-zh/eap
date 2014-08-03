package eap.comps.datastore.scope;

import javax.servlet.http.HttpServletRequest;

import eap.EapContext;
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
public class RequestScopeImpl implements IDataScope {
	
	public String getScopeName() {
		return DataStore.SCOPE_REQUEST;
	}
	public Object getLock() {
		return getRequest();
	}

	public void set(String key, Object value) {
		getRequest().setAttribute(key, value);
	}

	public Object get(String key) {
		return getRequest().getAttribute(key);
	}
	
	public void delete(String key) {
		getRequest().removeAttribute(key);
	}
	
	private HttpServletRequest getRequest() {
		return EapContext.get("request", HttpServletRequest.class);
	}
}