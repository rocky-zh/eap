package eap.base;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import eap.EapContext;
import eap.WebEnv;
import eap.comps.cookie.CookieManager;
import eap.comps.webevent.WebEvents;
import eap.comps.webevent.WebEventsHelper;
import eap.comps.webevent.WebFormVO;
import eap.exception.BizException;
import eap.exception.ErrorMsg;
import eap.util.AntiSamyUtil;
import eap.util.EDcodeUtil;
import eap.util.ExceptionUtil;
import eap.util.MessageUtil;
import eap.util.Paginator;
import eap.util.SpringMvcUtil;
import eap.util.StringUtil;
import eap.util.UrlUtil;
import eap.util.propertyeditor.DateEditor;

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
@SuppressWarnings("unchecked")
public class BaseController {
	
	public static final RequestMethod[] NOT_POST = {
		RequestMethod.GET,
		RequestMethod.HEAD,
		RequestMethod.OPTIONS,
		RequestMethod.PUT,
		RequestMethod.DELETE,
		RequestMethod.TRACE
	};
	public static final int DEFAULT_DOWNLOAD_BUFFER_SIZE = 4096;
	
	public Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected boolean enableRequestParamHtmlEscape = true;
	protected boolean enableRequestParamEmptyAsNull = true;
	
//	private WebEvents webEvents = new WebEvents();
	
//	private MessageSource messageSource;
	
	public String init(Model model, HttpServletResponse response) throws Exception { // TODO abstract
		return null;
	}
	public HttpServletRequest getRequestWrapper() {
		MultipartResolver multipartResolver = null;
		try {
			multipartResolver = this.getApplicationContext().getBean("multipartResolver", MultipartResolver.class);
		} catch (NoSuchBeanDefinitionException ex) {}
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		if (multipartResolver != null && multipartResolver.isMultipart(request)) { // request instanceof MultipartHttpServletRequest
			return multipartResolver.resolveMultipart(request);
		}
		
		return request;
	}
	public HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}
//	public HttpSession getSession() {
//		return this.getRequest().getSession();
//	}
	
	public ApplicationContext getApplicationContext() {
		return RequestContextUtils.getWebApplicationContext(this.getRequest());
	}
	public <T> T getBean(String name, Class<T> requiredType) {
		return this.getApplicationContext().getBean(name, requiredType);
	}
	public <T> T getBean(Class<T> requiredType) {
		return this.getApplicationContext().getBean(requiredType);
	}
	
	public String getParameter(String name) {
		String p = this.getRequestWrapper().getParameter(name);
		if (this.isEnableRequestParamEmptyAsNull() && StringUtil.isEmpty(p)) {
			return null;
		}
		if (StringUtil.isNotBlank(p)) {
			if (this.isEnableRequestParamHtmlEscape()) {
				p = HtmlUtils.htmlEscape(p);
			}
			
			p = p.trim();
		}
		
		return p;
	}
	public BigDecimal getParameterAsBigDecimal(String name) {
		String pStr = this.getParameter(name);
		if (StringUtil.isNotBlank(pStr)) {
			try {
				return new BigDecimal(pStr);
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
				return null;
			}
		}
		
		return null;
	}
	public Integer getParameterAsInteger(String name) {
		String pStr = this.getParameter(name);
		if (StringUtil.isNotBlank(pStr)) {
			try {
				return new Integer(pStr);
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
				return null;
			}
		}
		
		return null;
	}
	public <T> T getParameter(Class<T> paramClass) {
		return getParameter(paramClass, true);
	}
	public <T> T getParameter(Class<T> paramClass, boolean htmlEscape) {
		BeanWrapper bean = null;
		try {
			bean = PropertyAccessorFactory.forBeanPropertyAccess(paramClass.newInstance());
			bean.registerCustomEditor(Date.class, new DateEditor()); // TODO
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		for (Enumeration<String> pnEnum = this.getRequestWrapper().getParameterNames(); pnEnum.hasMoreElements(); ) {
			String paramName = pnEnum.nextElement();
			if (bean.isWritableProperty(paramName)) {
				bean.setPropertyValue(paramName, htmlEscape ? this.getParameter(paramName) : getRequestWrapper().getParameter(paramName));
			}
		}
		
		return (T) bean.getWrappedInstance();
		
//		BeanWrapper bean = PropertyAccessorFactory.forBeanPropertyAccess(paramClass);
//		PropertyDescriptor[] pds = bean.getPropertyDescriptors();
//		for (PropertyDescriptor pd : pds) {
//			String name = pd.getName();
//			if ("class".equals(name)) {
//				continue;
//			}
//			
//			String value = this.getParameter(name);
//			if (StringUtil.isNotBlank(value)) {
//				bean.setPropertyValue(name, value);
//			}
//		}
//		
//		return (T) bean.getWrappedInstance();
	}
	public String getParameterAsCleanHtml(String name) {
		String html = this.getRequestWrapper().getParameter(name);
		if (StringUtil.isNotBlank(html)) {
			html = AntiSamyUtil.getCleanHtml(html);
		}
		
		return html;
	}
	
	public UserDetailsVO currUserDetailsVO() {
//		return (UserDetailsVO) this.getSession().getAttribute(Env.SESSION_USER_DETAILS_KEY);
		return EapContext.getUserDetailsVO();
	}
	
	public Locale getLocale() {
		return EapContext.getLocale();
	}
	
	public Paginator getPaginator() {
		HttpServletRequest request = this.getRequestWrapper();
		WebEnv env = (WebEnv) EapContext.getEnv();
		
		Paginator paginator = new Paginator();
		String currPage = request.getParameter(env.getProperty("app.paging.currPageParam", "currPage"));
		if (StringUtil.isNotBlank(currPage)) {
			try {
				paginator.setCurrPage(Integer.parseInt(currPage));
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}
		String pageSize = request.getParameter(env.getProperty("app.paging.pageSizeParam", "pageSize"));
		if (StringUtil.isNotBlank(pageSize)) {
			try {
				paginator.setPageSize(Integer.parseInt(pageSize));
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}
		
		return paginator;
	}
	
	public String forward(String path) {
		return String.format("forward:%s", path);
	}
	public String redirect(String path) {
		return String.format("redirect:%s", path);
	}
	public String redirect(String path, boolean saveWebEvents) {
		if (saveWebEvents) {
			this.saveWebEvents();
		}
		
		return String.format("redirect:%s", path);
	}
	public String forwardAutoLogin(String userName) {
		return this.forwardAutoLogin(userName, null);
	}
	public String forwardAutoLogin(String userName, String targetUrl) {
		Assert.hasText(userName, "userName must not be empty");
		WebEnv env = (WebEnv) EapContext.getEnv();
		
		this.getRequest().setAttribute("_SECURITY_AUTO_LOGIN", "true");
		
		String checkUri = env.getProperty("app.security.login.checkUri");
		String userNameParameter = env.getProperty("app.security.login.userName.parameter");
		String targetUrlParameter = env.getProperty("app.security.login.targetUrl.parameter");
		return this.forward(String.format("%s?%s=%s&%s=%s", checkUri, userNameParameter, userName, targetUrlParameter, UrlUtil.encode(targetUrl))); //targetUrl = (targetUrl.contains("://") ? targetUrl : this.getRequest().getContextPath() + targetUrl);
	}
	public String forwardLogout() {
		WebEnv env = (WebEnv) EapContext.getEnv();
		return this.forward(env.getProperty("app.security.logout.uri"));
	}
	public boolean hasLogin(){
		return (this.currUserDetailsVO() != null ? true : false);
	}
//	public void sendRedirectToPassport(HttpServletResponse response, String userNum, String server, String targetUrl) {
//		try {
//			PassportUtil.sendRedirectToPassport(response, userNum, server, targetUrl);
//		} catch (Exception e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
//	public void sendRedirectToPassportSso(HttpServletResponse response, String userNum, String targetUrl) {
//		try {
//			PassportSsoUtil.sendRedirectToPassport(this.getRequest(), response, userNum, targetUrl);
//		} catch (Exception e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
	
	public String M(String code, Object[] args, String defaultMessage) {
		return MessageUtil.getMessage(code, args, defaultMessage, this.getLocale());
	}
	public String M(String code, String defaultMessage) {
		return MessageUtil.getMessage(code, null, defaultMessage, this.getLocale());
	}
	public String M(String code, Object[] args) {
		return this.M(code, args, "");
	}
	public String M(String code) {
		return this.M(code, null, "");
	}
	
	public WebEvents getWebEvents() {
		return WebEventsHelper.getWebEvents(this.getRequest());
	}
	public void clearWebEvents() {
		WebEventsHelper.setWebEvents(this.getRequest(), null);
	}
	public void saveWebEvents() {
		WebEventsHelper.setWebEventsInSession(this.getRequest());
	}
	
	public void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
		CookieManager.addCookie(this.getRequest(), response, cookieName, cookieValue);
	}
	public Cookie getCookie(String cookieName) {
		return CookieManager.getCookie(this.getRequest(), cookieName);
	}
	public String getCookieValue(String cookieName) {
		Cookie cookie = this.getCookie(cookieName);
		if (cookie != null) {
			return cookie.getValue();
		}
		
		return null;
	}
	public void removeCookie(HttpServletResponse response, String cookieName) {
		CookieManager.removeCookie(response, cookieName);
	}
	
	public String setException(Exception e, String viewName) {
		Throwable cause = ExceptionUtil.getRootCause(e);
		logger.error(e.getMessage(), cause); // TODO BizException
		
		if (e instanceof BizException) {
			BizException be = (BizException) e;
			ErrorMsg errorMsg = be.getErrorMsg();
			String message = null;
			if (errorMsg != null) {
				message = this.M(errorMsg.getCode(), errorMsg.getParams(), e.getMessage());
			} else {
				message = e.getMessage();
			}
			
			this.alertErrorMsg(message);
		} else {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
		if (StringUtil.isNotBlank(viewName)) {
			return viewName;
		}
		
		RequestMapping requestMapping = SpringMvcUtil.getMethodRequestMapping(this.getRequest(), this);
		return requestMapping.inputView();
	}
	public String setException(Exception e) {
		return this.setException(e, null);
	}
	
	public void alertMsg(String message) {
		this.getWebEvents().alert(message);
	}
	public void alertErrorMsg(String message) {
		this.getWebEvents().alert(WebEvents.WEB_EVENTS_ALERT_ERROR, message);
	}
	public void alertWarnMsg(String message) {
		this.getWebEvents().alert(WebEvents.WEB_EVENTS_ALERT_WARN, message);
	}
	public void alert(String code, String defaultMessage) {
		this.alert(code, null, defaultMessage);
	}
	public void alert(String code, Object[] args) {
		this.alert(code, args, "");
	}
	public void alert(String code, Object[] args, String defaultMessage) {
		String message = this.M(code, args, defaultMessage);
//		String alertType = StringUtil.regexGroup("\\.(\\w+)\\.", code, 1);
//		if (StringUtil.isBlank(alertType)) {
//			alertType = WebEvents.WEB_EVENTS_ALERT_INFO;
//		}
//		
//		this.getWebEvents().alert(alertType, message);
		this.getWebEvents().alert(message);
	}
	public void alert(String code) {
		this.alert(code, null, "");
	}
	public void setForm(String formId, WebFormVO webFormVO) {
		this.getWebEvents().setForm(formId, webFormVO);
	}
	
	public ModelAndView validateError(HttpServletRequest request, HttpServletResponse response, BindException e) {
		RequestMapping requestMapping = SpringMvcUtil.getMethodRequestMapping(request, this);
		WebEnv env = (WebEnv) EapContext.getEnv();
		
		String formId = JavaScriptUtils.javaScriptEscape(this.getParameter(env.getFormIdField()));
//		String viewName = bc.getParameter(env.getInputViewNameField());
//		String viewName = (String) this.getSession().getAttribute(env.getInputViewNameField() + formId);
		String viewName = requestMapping.inputView();
		
		if (StringUtils.isNotBlank(formId)) {
			this.setForm(formId, WebEventsHelper.toWebFormVO(request, e));
		}
		
		return new ModelAndView(StringUtils.defaultIfBlank(viewName, null), e.getModel());
	}
	
//	public void download(HttpServletResponse response, String resId, Object resContext) throws Exception {
//		InputStream is = FsResourceManager.read(resId, resContext);
//		String fileName = FsResourceManager.getResFileName(resId, resContext);
//		
//		this.download(response, is, fileName);
//	}
	public void download(HttpServletResponse response, byte[] data, String fileName) throws Exception {
		this.download(response, new ByteArrayInputStream(data), fileName);
	}
	public void download(HttpServletResponse response, InputStream is, String fileName) throws Exception {
		WebEnv env = (WebEnv) EapContext.getEnv();
		if (StringUtils.isNotBlank(fileName)) {
			fileName = URLEncoder.encode(fileName, env.getEncoding());
		}
		
		response.reset();
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		response.setHeader("Pragma", "private");
		response.setHeader("Cache-Control", "private, must-revalidate");
		
		ServletOutputStream sos = null;
		try {
			sos = response.getOutputStream();
			
			int i = -1, dataLen = 0;
			byte[] buf = new byte[env.getProperty("app.download.bufferSize", int.class, DEFAULT_DOWNLOAD_BUFFER_SIZE)];
			while ((i = is.read(buf)) != -1) {
				dataLen += i;
				sos.write(buf, 0, i);
				sos.flush();
			}
			
//			response.setContentLength(dataLen);
		} finally {
			if (sos != null) {
				sos.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}
	
//	public String upload(String resId, InputStream is, Object resContext) throws Exception {
//		return FsResourceManager.write(resId, is, resContext);
//	}
//	public String upload(String resId, final MultipartFile multipartFile) throws Exception {
//		return upload(resId, multipartFile, new HashMap());
//	}
//	public String upload(String resId, final MultipartFile multipartFile, Object resContext) throws Exception {
//		Map ctxMap = BeanUtil.toMap(resContext);
//		if (ctxMap != null) {
//			Map<String, Object> multipartFileCtxMap = new HashMap<String, Object>() {{
//				put("contentType", multipartFile.getContentType());
//				put("name", multipartFile.getName());
//				put("originalFilename", multipartFile.getOriginalFilename());
//				put("size", multipartFile.getSize());
//				put("fileExtension", FileUtil.getFileNameSuffix(multipartFile.getOriginalFilename()));
//				put("uuid", EDcodeUtil.uuid());
//			}};
//			multipartFileCtxMap.putAll(ctxMap);
//			ctxMap = multipartFileCtxMap;
//		}
//		return FsResourceManager.write(resId, multipartFile.getInputStream(), ctxMap);
//	}
	
	public String encrypt(String plaintext) {
		WebEnv env = (WebEnv) EapContext.getEnv();
		return EDcodeUtil.aesEncodeAsHex(plaintext, env.getProperty("app.singature"));
	}
	
	public String deciphering(String ciphertext) {
		WebEnv env = (WebEnv) EapContext.getEnv();
		return EDcodeUtil.aesDecodeForHexAsString(ciphertext, env.getProperty("app.singature"));
	}
	
	public boolean isEnableRequestParamHtmlEscape() {
		return enableRequestParamHtmlEscape;
	}

	public boolean isEnableRequestParamEmptyAsNull() {
		return enableRequestParamEmptyAsNull;
	}
}