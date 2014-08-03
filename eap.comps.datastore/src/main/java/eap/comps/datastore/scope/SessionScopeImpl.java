package eap.comps.datastore.scope;

import javax.servlet.http.HttpSession;

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
public class SessionScopeImpl implements IDataScope {
	
	public String getScopeName() {
		return DataStore.SCOPE_SESSION;
	}
	public Object getLock() {
		return getSession();
	}
	
	public void set(String key, Object value) {
		getSession().setAttribute(key, value);
	}

	public Object get(String key) {
		return getSession().getAttribute(key);
	}
	
	public void delete(String key) {
		getSession().removeAttribute(key);
	}
	
	private HttpSession getSession() {
		return EapContext.get("session", HttpSession.class);
	}
}