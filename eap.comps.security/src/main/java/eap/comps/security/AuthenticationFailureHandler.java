package eap.comps.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.VcodeNotMatchException;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import eap.EapContext;
import eap.comps.webevent.WebFormVO;
import eap.util.DateUtil;
import eap.util.JsonUtil;
import eap.util.MessageUtil;
import eap.util.ReflectUtil;
import eap.util.UrlUtil;


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
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler { // PROTOTYPE
	
	/** 账号或密码错误 */
	public static final String LOGIN_ERROR_USERNAME_OR_PASSWORD_ERROR = "1";
	/** 验证码错误 */
	public static final String LOGIN_ERROR_VCODE_ERROR = "2";
	/** 登陆超时 */
	public static final String LOGIN_ERROR_SESSION_EXPIRED = "3";
	/** 账号已禁用 */
	public static final String LOGIN_ERROR_ACCOUNT_DISABLED = "10";
	/** 账号已过期 */
	public static final String LOGIN_ERROR_ACCOUNT_EXPIRED = "11";
	/** 账号已锁定 */
	public static final String LOGIN_ERROR_ACCOUNT_STATUS_LOCKED = "12";
	/** 账号认证过期？？ */
	public static final String LOGIN_ERROR_CREDENTIALS_EXPIRED = "13";
	
	private String failureUrl = "/login";
	
	private String ssoFrameTargetUrl = "/static/ssoFrameCallback.html";
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) 
		throws IOException, ServletException 
	{
		logger.error(exception.getMessage(), exception);
		
		String loginErrorCode = this.setDefaultFailureUrlForException(exception);
		
//		this.setDefaultFailureUrl("https://:8443/cas/login");
		
		if ("ssoFrame".equalsIgnoreCase(request.getParameter("type"))) { // /loginCheck
			this.getRedirectStrategy().sendRedirect(request, response, ssoFrameTargetUrl +"?login_error=" + loginErrorCode);
		} 
		else if (isAjaxRequest(request)) {
			this.onAuthenticationFailureForAjax(request, response, exception);
		} 
		else {
			super.onAuthenticationFailure(request, response, exception);
		}
		
		EapContext.publish("#login.failure", exception);
	}
	
	public void onAuthenticationFailureForAjax(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) 
		throws IOException, ServletException 
	{
		String defaultFailureUrl = ReflectUtil.getFieldValue(this, "defaultFailureUrl", String.class);
		Map<String, String> urlParamMap = UrlUtil.getUrlQueryStringAsMap(defaultFailureUrl);
		String loginError = urlParamMap.get("login_error");
		
		WebFormVO webFormVO = new WebFormVO();
		Locale locale = EapContext.getLocale();
		if (LOGIN_ERROR_USERNAME_OR_PASSWORD_ERROR.equalsIgnoreCase(loginError)) {
			webFormVO.addError("password", MessageUtil.getMessage("login.e.usernameOrPasswordError", null, "账号或密码不正确", locale));
		} 
		else if (LOGIN_ERROR_VCODE_ERROR.equalsIgnoreCase(loginError)) {
			webFormVO.addError("vcode", MessageUtil.getMessage("login.e.vcodeError", null, "验证码不正确", locale));
		} 
		else if (LOGIN_ERROR_ACCOUNT_STATUS_LOCKED.equalsIgnoreCase(loginError)) {
			webFormVO.addError("username", MessageUtil.getMessage("login.e.accountStatusLocked", null, "账号已被锁定，不允许登陆", locale));
		}
		else if (LOGIN_ERROR_SESSION_EXPIRED.equalsIgnoreCase(loginError)) {
			webFormVO.addError("username", MessageUtil.getMessage("login.e.sessionExpired", null, "登陆超时，请重新登陆", locale));
		}
		
//		WebEvents webEvents = new WebEvents();
//		String formId = JavaScriptUtils.javaScriptEscape(HtmlUtils.htmlEscape(request.getParameter(env.getFormIdField())));
//		if (StringUtils.isNotBlank(formId)) {
//			webEvents.setForm(formId, webFormVO);
//		}
		
//		WebAjaxResponse ajaxResponse = new WebAjaxResponse();
//		ajaxResponse.setSystemTime(DateUtil.currDate());
////		ajaxResponse.setEvents(webEvents.getEvents());
//		ajaxResponse.setResult(webFormVO);
		
		Map<String, Object> ajaxResponse = new HashMap<String, Object>();
		ajaxResponse.put("serverTime", DateUtil.currDate());
		ajaxResponse.put("result", webFormVO);
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
	
	private String setDefaultFailureUrlForException(Exception e) {
		String loginError = LOGIN_ERROR_USERNAME_OR_PASSWORD_ERROR;
		
		if (e instanceof AccountStatusException) {
			if (e instanceof DisabledException) {
				loginError = LOGIN_ERROR_ACCOUNT_DISABLED;
			} 
			else if (e instanceof AccountExpiredException) {
				loginError = LOGIN_ERROR_ACCOUNT_EXPIRED;
			}
			else if (e instanceof LockedException) {
				loginError = LOGIN_ERROR_ACCOUNT_STATUS_LOCKED;
			}
			else if (e instanceof CredentialsExpiredException) {
				loginError = LOGIN_ERROR_CREDENTIALS_EXPIRED;
			}
		}
		else if (e instanceof UsernameNotFoundException) {
			loginError = LOGIN_ERROR_USERNAME_OR_PASSWORD_ERROR;
		}
		else if (e instanceof VcodeNotMatchException) {
			loginError = LOGIN_ERROR_VCODE_ERROR;
		}
		else if (e instanceof NonceExpiredException) {
			loginError = LOGIN_ERROR_VCODE_ERROR; // TODO 
		}
		
		this.setDefaultFailureUrl(String.format("%s?login_error=%s", failureUrl, loginError));
		
		return loginError;
	}
	
	private boolean isAjaxRequest(HttpServletRequest request) {
		if ("text/html;type=ajax".equals(request.getHeader("Accept")) 
				|| "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) 
				|| StringUtils.isNotBlank(request.getParameter("AjaxSource"))) {
			return true;
		}
		
		return false;
	}
	
	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}

	public void setSsoFrameTargetUrl(String ssoFrameTargetUrl) {
		this.ssoFrameTargetUrl = ssoFrameTargetUrl;
	}
}