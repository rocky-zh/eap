package eap.comps.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.ClassUtils;

import eap.EapContext;
import eap.base.IUserDetailsVOWrapper;
import eap.base.UserDetailsVO;
import eap.util.JsonUtil;
import eap.util.ReflectUtil;
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
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    private static final boolean casAvailable = ClassUtils.isPresent("org.springframework.security.cas.ServiceProperties", AuthenticationSuccessHandler.class.getClassLoader());
	
	private String ssoFrameTargetUrl = "/static/ssoFrameCallback.html";
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
		throws ServletException, IOException 
	{
		Object principal = authentication.getPrincipal();
		IUserDetailsVOWrapper userDetailsVOWrapper = (principal instanceof IUserDetailsVOWrapper) ? (IUserDetailsVOWrapper) principal : null;
		UserDetailsVO userDetailsVO = userDetailsVOWrapper.getUserDetailsVO();
		if (userDetailsVO != null) {
			
//			if (request.getServletContext().getFilterRegistration("areaFilter") != null) { // TODO
//				AreaDetailsManager.setCurrAreaDetailsVO(request.getSession(), AreaDetailsManager.getCityAreaDetailsVO(userDetailsVO.getAreaCd()));
//			}
			
//			userDetailsVO.setIp(HttpUtil.getRemoteAddr(request));
//			userDetailsVO.setAdvertisingMedia(GlobalVars.getAdvertisingMedia(request));
			
//			if (casAvailable) {
//    			if (authentication instanceof CasAuthenticationToken) {
//    				CasAuthenticationToken cat = (CasAuthenticationToken) authentication;
////    				userDetailsVO.setLoginAcctNo(cat.getAssertion().getPrincipal().getName());
//    			} else if (authentication instanceof CasAssertionAuthenticationToken) {
//    				CasAssertionAuthenticationToken casat = (CasAssertionAuthenticationToken) authentication;
////    				userDetailsVO.setLoginAcctNo(casat.getAssertion().getPrincipal().getName());
//    			}
//			}
		}
		
		if ("ssoFrame".equalsIgnoreCase(request.getParameter("type"))) { // /loginCheck
			RequestCache requestCache = ReflectUtil.getFieldValue(this, "requestCache", RequestCache.class);
			SavedRequest savedRequest = requestCache.getRequest(request, response);
			if (savedRequest == null) {
				if (!response.isCommitted()) {
					String targetUrl = determineTargetUrl(request, response);
					DefaultRedirectStrategy redirectStrategy = (DefaultRedirectStrategy) this.getRedirectStrategy();
					String redirectUrl = (String) ReflectUtil.invokeMethod(redirectStrategy, "calculateRedirectUrl", new Object[] {request.getContextPath(), targetUrl});
					redirectUrl = response.encodeRedirectURL(redirectUrl);
					
					HttpSession session = request.getSession(true);
					String ssoAuto = (String) session.getAttribute("_SECURITY_SSO_AUTO"); // 自动登录
					if (Boolean.parseBoolean(ssoAuto)) {
						session.removeAttribute("_SECURITY_SSO_AUTO");
						String callbackUrl = (String) session.getAttribute("_SECURITY_SSO_CALLBACK_URL");
						session.removeAttribute("_SECURITY_SSO_CALLBACK_URL");
						
						redirectStrategy.sendRedirect(request, response, StringUtil.defaultIfBlank(callbackUrl, redirectUrl));
					} else {
						redirectStrategy.sendRedirect(request, response, ssoFrameTargetUrl + "?targetUrl=" + redirectUrl);
					}
				}
				
				clearAuthenticationAttributes(request);
			} else {
				requestCache.removeRequest(request, response);
				clearAuthenticationAttributes(request);
				this.getRedirectStrategy().sendRedirect(request, response, ssoFrameTargetUrl + "?targetUrl=" + savedRequest.getRedirectUrl());
			}
		}
		else if (isAjaxRequest(request)) {
			this.onAuthenticationSuccessForAjax(request, response, authentication);
		} 
		else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
		
		EapContext.publish("#login.success", authentication);
	}
	
	public void onAuthenticationSuccessForAjax(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
		throws ServletException, IOException 
	{
//		WebEvents webEvents = new WebEvents();
//		String formId = JavaScriptUtils.javaScriptEscape(HtmlUtils.htmlEscape(request.getParameter(env.getFormIdField())));
//		if (StringUtils.isNotBlank(formId)) {
//			webEvents.setForm(formId, new WebFormVO());
//		}
		
//		WebAjaxResponse ajaxResponse = new WebAjaxResponse();
//		ajaxResponse.setSystemTime(DateUtil.currDate());
////		ajaxResponse.setEvents(webEvents.getEvents());
//		ajaxResponse.setResult(new WebFormVO());
		Map<String, Object> ajaxResponse = new HashMap<String, Object>();
		String ajaxResponseJson = JsonUtil.toJson(ajaxResponse);
		
		ServletOutputStream sos = null;
		try {
			String encoding = EapContext.getEnv().getEncoding();
			response.setContentType(String.format("application/json;charset=%s", encoding));
			
			sos = response.getOutputStream();
			sos.write(ajaxResponseJson.getBytes(encoding));
			sos.flush();
		} finally {
			if (sos != null) {
				sos.close();
			}
		}
	}
	
	private boolean isAjaxRequest(HttpServletRequest request) {
		if ("text/html;type=ajax".equals(request.getHeader("Accept")) 
				|| "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) 
				|| StringUtils.isNotBlank(request.getParameter("AjaxSource"))) {
			return true;
		}
		
		return false;
	}

	public void setSsoFrameTargetUrl(String ssoFrameTargetUrl) {
		this.ssoFrameTargetUrl = ssoFrameTargetUrl;
	}
}