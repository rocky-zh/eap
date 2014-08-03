package eap.web;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.filter.DelegatingFilterProxy;

import eap.EapContext;
import eap.EapStartupLogger;
import eap.Env;
import eap.TopicManager;
import eap.UM;
import eap.WebEnv;
import eap.util.BeanUtil;
import eap.util.JsonUtil;
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
public class WebListener extends ContextLoaderListener implements ServletContextListener, HttpSessionListener, ServletRequestListener {
	
	private static final Logger logger = LoggerFactory.getLogger(WebListener.class);
	
	private Env env = null;
	
	private List<ServletContextListener> servletContextListeners = new ArrayList<ServletContextListener>();
	private List<HttpSessionListener> httpSessionListeners = new ArrayList<HttpSessionListener>();
	private List<ServletRequestListener> servletRequestListeners = new ArrayList<ServletRequestListener>();
	
	// START: ServletContextListener
	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.gc();
		System.setProperty("java.awt.headless", "true");
		
		EapStartupLogger.printStartingMessage();
		
		WebEapContextHolder contextHolder = new WebEapContextHolder();
		contextHolder.setEnv(new WebEnv());
		contextHolder.setTopicManager(new TopicManager());
		
		EapContext.init(contextHolder);
		env = EapContext.getEnv();
		
		if (UM.isEnabled()) {
			try {
				UM.start();
				
				final CountDownLatch firstLoadedLatch = new CountDownLatch(1); // first loaded form UM
				UM.addListener(UM.envPath, new UM.NodeListener() {
					public void nodeChanged(CuratorFramework client, ChildData childData) throws Exception {
						byte[] data = childData != null ? childData.getData() : null;
						Map<String, String> envMap = null;
						if (data != null && data.length > 0) {
							envMap = JsonUtil.parseJson(new String(data), LinkedHashMap.class);
						} else {
							envMap = new LinkedHashMap<String, String>();
						}
						env.refresh(envMap);
						firstLoadedLatch.countDown();
					}
				});
				firstLoadedLatch.await();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				return;
			}
		} else {
			env.refresh(null);
		}
		
		EapContext.publish("$context.initializing", event);
		
		ServletContext servletContext = event.getServletContext();
		WebEnv.webContextPath = servletContext.getContextPath();
		if (!env.containsKey("app.web.rootPath")) {
			env.getEnvProperties().put("app.web.rootPath", servletContext.getRealPath(""));
		}
		
		this.setInitParameters(servletContext);
		this.setListeners(servletContext);
		this.setFilters(servletContext);
		this.setSerlvets(servletContext);
		super.contextInitialized(event);
		
		for (int i = 0; i < servletContextListeners.size(); i++) {
			((ServletContextListener) servletContextListeners.get(i)).contextInitialized(event);
		}
		
		EapContext.publish("$context.initialized", event);
	}
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		EapContext.publish("$context.destroying", event);
		for (int i = servletContextListeners.size() - 1; i >= 0 ; i--) {
			((ServletContextListener) servletContextListeners.get(i)).contextDestroyed(event);
		}
		super.contextDestroyed(event);
		EapContext.publish("$context.destroyed", event);
		UM.stop();
		WebEnv.webContextPath = null;
	}
	private void setInitParameters(ServletContext servletContext) {
		Map<String, Object> initParameters = env.filterForPrefix("app.web.initParameter.");
		for (Map.Entry<String, Object> entry : initParameters.entrySet()) {
			if (StringUtil.isBlank(servletContext.getInitParameter(entry.getKey())) && entry.getValue() != null && entry.getValue().toString().length() > 0) {
				servletContext.setInitParameter(entry.getKey(), entry.getValue().toString());
			}
		}
	}
	private void setListeners(ServletContext servletContext) {
		Map<String, Object> listeners = env.filterForPrefix("app.web.listener.");
		for (Map.Entry<String, Object> entry : listeners.entrySet()) {
			String value = (String) entry.getValue();
			Object listener = null;
			try {
				listener = Class.forName(value).newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException("listener class error: " + value);
			}
			if (listener instanceof ServletContextListener) {
				servletContextListeners.add((ServletContextListener) listener);
			} else if (listener instanceof HttpSessionListener) {
				httpSessionListeners.add((HttpSessionListener) listener);
			} else if (listener instanceof ServletRequestListener) {
				servletRequestListeners.add((ServletRequestListener) listener);
			}
		}
	}
	private void setFilters(ServletContext servletContext) {
		Map<String, Object> filters = env.filterForPrefix("app.web.filter.");
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			String key = entry.getKey();
			if (key.lastIndexOf(".") == -1) { // app.web.filter.*=className
				if (Boolean.parseBoolean((String)filters.get(key + ".enable"))) {
					if (servletContext.getFilterRegistration(key) != null) {
						continue;
					}
					
					String value = (String) entry.getValue();
					
					Filter filter = null;
					if (value.startsWith("proxy:")) {
						filter = new DelegatingFilterProxy(value.substring("proxy:".length()));
					} else {
						try {
							filter = (Filter) Class.forName(value).newInstance();
						} catch (Exception e) {
							throw new IllegalArgumentException("filter class error: " + value);
						}
					}
					
					for (Map.Entry<String, Object> entry0 : filters.entrySet()) {
						String key0 = entry0.getKey();
						String propertyPrefix = key + ".property.";
						if (key0.startsWith(propertyPrefix)) {
							BeanUtil.setProperty(filter, key0.substring(propertyPrefix.length()), entry0.getValue());
						}
					}
					
					FilterRegistration.Dynamic filterDinamic = servletContext.addFilter(key, filter);
					for (Map.Entry<String, Object> entry0 : filters.entrySet()) {
						String key0 = entry0.getKey();
						String initParameterPrefix = key + ".initParameter.";
						if (key0.startsWith(initParameterPrefix)) {
							filterDinamic.setInitParameter(key0.substring(initParameterPrefix.length()), (String)entry0.getValue());
						}
					}
					filterDinamic.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, StringUtil.split((String)filters.get(key + ".urlPatterns")));
				}
			}
		}
	}
	private void setSerlvets(ServletContext servletContext) {
		Map<String, Object> serlvets = env.filterForPrefix("app.web.servlet.");
		for (Map.Entry<String, Object> entry : serlvets.entrySet()) {
			String key = entry.getKey();
			if (key.lastIndexOf(".") == -1) { // app.web.servlet.*=className
				if (Boolean.parseBoolean((String)serlvets.get(key + ".enable"))) {
					if (servletContext.getServletRegistration(key) != null) {
						continue;
					}
					
					String value = (String) entry.getValue();
					
					Servlet serlvet = null;
					try {
//						if ("org.springframework.web.servlet.DispatcherServlet".equals(value)) {
//							serlvet = (Servlet) Class.forName(value).getConstructor(WebApplicationContext.class).newInstance(getCurrentWebApplicationContext());
//						} else {
							serlvet = (Servlet) Class.forName(value).newInstance();
//						}
					} catch (Exception e) {
						throw new IllegalArgumentException("serlvet class error: " + value);
					}
					
					for (Map.Entry<String, Object> entry0 : serlvets.entrySet()) {
						String key0 = entry0.getKey();
						String propertyPrefix = key + ".property.";
						if (key0.startsWith(propertyPrefix)) {
							BeanUtil.setProperty(serlvet, key0.substring(propertyPrefix.length()), entry0.getValue());
						}
					}
					
					ServletRegistration.Dynamic servletDynamic = servletContext.addServlet(key, serlvet);
					for (Map.Entry<String, Object> entry0 : serlvets.entrySet()) {
						String key0 = entry0.getKey();
						String initParameterPrefix = key + ".initParameter.";
						if (key0.startsWith(initParameterPrefix)) {
							servletDynamic.setInitParameter(key0.substring(initParameterPrefix.length()), (String)entry0.getValue());
						}
					}
					String LoadOnStartupStr = (String)serlvets.get(key + ".loadOnStartup");
					if (StringUtil.isNotBlank(LoadOnStartupStr)) {
						servletDynamic.setLoadOnStartup(Integer.parseInt(LoadOnStartupStr));
					}
					servletDynamic.addMapping(StringUtil.split((String) serlvets.get(key + ".mapping"), WebEnv.VALUES_SEPARATOR));
				}
			}
		}
	}
	// END: ServletContextListener
	
	
	// START: HttpSessionListener
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		EapContext.publish("$session.creating", event);
		String sessionTimeout = env.getProperty("app.web.session.timeout");
		if (StringUtil.isNotBlank(sessionTimeout)) {
			event.getSession().setMaxInactiveInterval(new Integer(sessionTimeout) * 60); // seconds
		}
		
		for (int i = 0; i < httpSessionListeners.size(); i++) {
			((HttpSessionListener) httpSessionListeners.get(i)).sessionCreated(event);
		}
		
		EapContext.publish("$session.created", event);
	}
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		EapContext.publish("$session.destroying", event);
		for (int i = httpSessionListeners.size() - 1; i >= 0 ; i--) {
			((HttpSessionListener) httpSessionListeners.get(i)).sessionDestroyed(event);
		}
		EapContext.publish("$session.destroyed", event);
	}
	// END: HttpSessionListener
	
	
	// START: ServletRequestListener
	@Override
	public void requestInitialized(ServletRequestEvent event) {
		EapContext.publish("$request.initializing", event);
		for (int i = 0; i < servletRequestListeners.size(); i++) {
			((ServletRequestListener) servletRequestListeners.get(i)).requestInitialized(event);
		}
		EapContext.publish("$request.initialized", event);
	}
	@Override
	public void requestDestroyed(ServletRequestEvent event) {
		EapContext.publish("$request.destroying", event);
		for (int i = servletRequestListeners.size() - 1; i >= 0 ; i--) {
			((ServletRequestListener) servletRequestListeners.get(i)).requestDestroyed(event);
		}
		EapContext.publish("$request.destroyed", event);
	}
	// END: ServletRequestListener
}