package eap.web.interceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import eap.comps.webevent.WebEvents;
import eap.comps.webevent.WebEventsHelper;
import eap.util.JsonUtil;

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
public class WebEventsHandlerInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		if (DispatcherType.REQUEST.compareTo(request.getDispatcherType()) == 0) {
			WebEvents webEventsInSession = WebEventsHelper.getWebEventsInSession(request);
			if (webEventsInSession != null) {
				WebEventsHelper.removeWebEventsInSession(request);
				
				WebEventsHelper.setWebEvents(request, webEventsInSession);
			}
		}
		
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) 
		throws Exception 
	{
		if (ex == null) {
//			if (handler instanceof BaseController) { // TODO OPTION: without Ajax request // TODO remove  webflow
				Object webEvents = request.getAttribute(WebEventsHelper.REQUEST_WEB_EVENTS_KEY);
				if (webEvents instanceof WebEvents) { // 多次转发请求， 只处理一次
					request.setAttribute(WebEventsHelper.REQUEST_WEB_EVENTS_KEY, JsonUtil.toJson(((WebEvents) webEvents).getEvents()));
				}
				
//				BaseController bc = (BaseController) handler;
//				if (bc == bc.getRequest().getAttribute(Env.REQUEST_WEB_EVENTS_FIRST_HANDLER_KEY)) {
//					WebEvents webEvents = bc.getWebEvents();
//					if (webEvents != null) {
//						request.setAttribute(Env.REQUEST_WEB_EVENTS_KEY, JsonUtil.toJson(webEvents.getEvents()));
//					}
//				}
//			} // TODO remove  webflow
		}
	}
}