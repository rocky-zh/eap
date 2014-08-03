package eap.comps.webevent;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import eap.EapContext;
import eap.util.MessageUtil;

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
public class WebEventsHelper {
	
	public static final String REQUEST_WEB_EVENTS_KEY = "__web_events";
	public static final String SESSION_LAST_WEB_EVENTS_KEY = "__last_web_events";
	
	public static WebEvents getWebEvents(HttpServletRequest request) {
		WebEvents webEvents = (WebEvents) request.getAttribute(REQUEST_WEB_EVENTS_KEY);
		if (webEvents == null) {
			webEvents = new WebEvents();
			request.setAttribute(REQUEST_WEB_EVENTS_KEY, webEvents);
		}
		
		return webEvents;
	}
	public static WebEvents getWebEvents0(HttpServletRequest request) {
		return (WebEvents) request.getAttribute(REQUEST_WEB_EVENTS_KEY);
	}
	public static void setWebEvents(HttpServletRequest request, WebEvents webEvents) {
		request.setAttribute(REQUEST_WEB_EVENTS_KEY, webEvents);
	}
	
	public static void setWebEventsInSession(HttpServletRequest request) {
		WebEvents webEvents = getWebEvents(request);
		
		request.getSession(true).setAttribute(SESSION_LAST_WEB_EVENTS_KEY, webEvents);
	}
	public static WebEvents getWebEventsInSession(HttpServletRequest request) {
		return (WebEvents) request.getSession(true).getAttribute(SESSION_LAST_WEB_EVENTS_KEY);
	}
	public static void removeWebEventsInSession(HttpServletRequest request) {
		request.getSession(true).removeAttribute(SESSION_LAST_WEB_EVENTS_KEY);
	}
	
	public static WebFormVO toWebFormVO(HttpServletRequest request, BindException be) { // TODO
		Locale locale = EapContext.getLocale();
		WebFormVO webFormVO = new WebFormVO();
		for (ObjectError objectError : be.getAllErrors()) {
			if (objectError instanceof FieldError) {
				FieldError fieldError = (FieldError) objectError;
				String message = null;
				for (String code : fieldError.getCodes()) {
					message = MessageUtil.getMessage(code, null, locale); // , new Object[] {(fieldError.getObjectName() +"." + fieldError.getField()), fieldError.getRejectedValue()}
					if (StringUtils.isNotBlank(message)) {
						break;
					}
				}
				
				webFormVO.addError(fieldError.getField(), StringUtils.defaultIfBlank(message, fieldError.getDefaultMessage()));
			} else {
				// TODO 
			}
		}
		
		return webFormVO;
	}
}