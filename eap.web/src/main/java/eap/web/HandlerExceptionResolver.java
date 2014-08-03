package eap.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import eap.base.BaseController;
import eap.comps.token.TokenExpiredException;
import eap.comps.webevent.WebEventsHelper;
import eap.exception.BizException;
import eap.util.ExceptionUtil;
import eap.util.HttpUtil;
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
public class HandlerExceptionResolver implements org.springframework.web.servlet.HandlerExceptionResolver { // not bizException;   all RuntimEexception
	
	private static final Logger logger = LoggerFactory.getLogger(HandlerExceptionResolver.class);
	
	private String errorViewPath = "/WEB-INF/views/error/%s.jsp";
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) 
	{
	    Throwable cause = ExceptionUtil.getRootCause(e);
	    
		if (e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException")) { // Tomcat Server // ClientAbortException:  java.net.SocketException: Software caused connection abort: socket write error
			logger.debug(e.getMessage(), cause);
			return null;
		}
		
		if (e instanceof TokenExpiredException) {
			logger.error(e.getMessage(), cause);
			
			if (HttpUtil.isAjaxRequest(request)) {
//				BaseController bc = (BaseController) handler;
//				String formId = bc.getParameter(env.getFormIdField());
//				if (StringUtils.isNotBlank(formId)) {
//					WebFormVO webFormVO = new WebFormVO();
//					this.setForm(formId, WebEventsHelper.toWebFormVO(request, e));
//				}
//				
//				String formId = r.getParameter(env.getFormIdField());
////				String viewName = bc.getParameter(env.getInputViewNameField());
////				String viewName = (String) this.getSession().getAttribute(env.getInputViewNameField() + formId);
//				String viewName = requestMapping.inputView();
//				
//				if (StringUtils.isNotBlank(formId)) {
//					this.setForm(formId, WebEventsHelper.toWebFormVO(request, e));
//				}
				
				WebEventsHelper.getWebEvents(request).alert("error", e.getMessage());
				return new ModelAndView(String.format(errorViewPath, "errorAjax"));
			} else {
				String referer = request.getHeader("referer");
				if (StringUtil.isNotBlank(referer) && !referer.equals(request.getRequestURL().toString())) {
					try {
						response.sendRedirect(referer);
						return new ModelAndView();
					} catch (IOException sre) {
						logger.error(sre.getMessage(), cause);
					}
				}
			}
			
			return this.getErrorModelAndView(request, HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
		
		ModelAndView mnv = null;
		if (handler instanceof BaseController) {
			BaseController bc = (BaseController) handler;
			
			if (e instanceof BindException) {
				bc.logger.error(e.getMessage(), cause);
				mnv = bc.validateError(request, response, (BindException) e);
			} else if (e instanceof BizException) {
				String viewName = bc.setException(e); // in setException ->  log error
				mnv = new ModelAndView(viewName, ((BizException) e).getModel());
			} 
//			else if (e instanceof HttpRequestMethodNotSupportedException) {
//				String referer = request.getHeader("referer");
//				if (StringUtil.isNotBlank(referer) && !referer.equals(request.getRequestURL().toString())) {
//					try {
//						response.sendRedirect(referer);
//						return new ModelAndView();
//					} catch (IOException sre) {
//						logger.error(e.getMessage(), e);
//					}
//				}
//			}
			else {
				bc.logger.error(e.getMessage(), cause);
				WebEventsHelper.getWebEvents(request).alert("error", "服务器繁忙");
			}
			
			if (HttpUtil.isAjaxRequest(request)) {
//				WebAjaxResponse responseObject = new WebAjaxResponse();
//				responseObject.setSystemTime(DateUtil.currDate());
//				responseObject.setEvents(WebEventsHelper.getWebEvents(request).getEvents());
				mnv =  new ModelAndView(String.format(errorViewPath, "errorAjax"));
			}
			
			if (mnv == null) {
				mnv = this.getErrorModelAndView(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} 
		else {
			logger.error(e.getMessage(), cause);
			mnv = this.getErrorModelAndView(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		return mnv;
	}
	
	private ModelAndView getErrorModelAndView(HttpServletRequest request, int statucCode) {
		boolean isAjaxRequest = HttpUtil.isAjaxRequest(request);
		return new ModelAndView(String.format(errorViewPath, String.format("error%d%s", statucCode, isAjaxRequest ? "Ajax" : "")));
	}

	public void setErrorViewPath(String errorViewPath) {
		this.errorViewPath = errorViewPath;
	}
}