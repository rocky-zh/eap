package eap.comps.datastore.scope;

import javax.servlet.ServletContext;

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
public class ServletContextScopeImpl implements IDataScope {
	
	public String getScopeName() {
		return DataStore.SCOPE_APPLICATION;
	}
	public Object getLock() {
		return getServletContext();
	}

	public void set(String key, Object value) {
		getServletContext().setAttribute(key, value);
	}
	
	public Object get(String key) {
		return getServletContext().getAttribute(key);
	}
	
	public void delete(String key) {
		getServletContext().removeAttribute(key);
	}
	
	private ServletContext getServletContext() {
		return EapContext.get("servletContext", ServletContext.class);
	}
}