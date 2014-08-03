package eap.web;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import eap.Env;
import eap.IEapContextHolder;
import eap.TopicManager;
import eap.WebEnv;
import eap.base.UserDetailsVO;
import eap.util.HttpUtil;

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
public class WebEapContextHolder implements IEapContextHolder {
	
	private Env env;
	private TopicManager topicManager;
	
	public Env getEnv() {
		return env;
	}
	public void setEnv(Env env) {
		this.env = env;
	}

	public TopicManager getTopicManager() {
		return topicManager;
	}
	public void setTopicManager(TopicManager topicManager) {
		this.topicManager = topicManager;
	}

	public UserDetailsVO getUserDetailsVO() {
		HttpSession session = this.getSession();
		if (session != null) {
			return (UserDetailsVO) session.getAttribute(WebEnv.SESSION_USER_DETAILS_KEY);
		}
		return null;
	}

	public Locale getLocale() {
		return RequestContextUtils.getLocale(this.getRequest());
	}

	public String getIp() {
		HttpServletRequest request = this.getRequest();
		return (request != null ? HttpUtil.getRemoteAddr(request) : null);
	}
	
	public <T> T get(String key, Class<T> requireType) {
		if ("request".equals(key)) {
			return (T) getRequest();
		} 
		else if ("session".equals(key)) {
			return (T) getSession();
		} 
		else if ("servletContext".equals(key)) {
			return (T) getServletContext();
		}
		
		return null;
	}
	
	public ServletContext getServletContext() {
		HttpServletRequest request = getRequest();
		return request != null ? request.getServletContext() : null;
	}
	
	public HttpSession getSession() {
		HttpServletRequest request = getRequest();
		return request != null ? request.getSession() : null;
	}
	
	public HttpServletRequest getRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
		return (requestAttributes != null ? requestAttributes.getRequest() : null);
	}
}