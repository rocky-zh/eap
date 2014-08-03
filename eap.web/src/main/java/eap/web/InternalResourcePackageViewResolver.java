package eap.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import eap.WebEnv;
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
public class InternalResourcePackageViewResolver extends org.springframework.web.servlet.view.InternalResourceViewResolver implements HandlerInterceptor {
	
	private String viewPath = "/WEB-INF/classes/%s/view/%s.jsp";
	
	@Override
	protected Object getCacheKey(String viewName, Locale locale) {
		HttpServletRequest request = getRequest();
		String basePackage = (String) request.getAttribute(WebEnv.REQUEST_LAST_HANDLER_BASE_PACKAGE_KEY);
		
		return super.getCacheKey(basePackage + "@" + viewName, locale);
	}
	
	@Override
	protected View createView(String viewName, Locale locale) throws Exception { // viewName 必须唯一
		String newViewName = viewName;
		
		HttpServletRequest request = getRequest();
		String basePackage = (String) request.getAttribute(WebEnv.REQUEST_LAST_HANDLER_BASE_PACKAGE_KEY);
		if (StringUtil.isNotBlank(viewName) && !viewName.startsWith("/") && !viewName.startsWith(FORWARD_URL_PREFIX) && !viewName.startsWith(REDIRECT_URL_PREFIX))  {
			newViewName = String.format(viewPath, StringUtil.replaceChars(basePackage, ".", "/"), viewName);
		}
		
		View view = super.createView(newViewName, locale);
//		if (view != null) {
//			if (view instanceof JstlView) {
//				JstlView jstlView = (JstlView) view;
//				String url = jstlView.getUrl();
//				if (StringUtil.isNotBlank(url) && !url.startsWith("/")) { // TODO 不以"/"开头的 viewName，默认添加包路径
//					
//					String viewPrefix = null;
//					if (StringUtil.isNotBlank(basePackage)) {
//						viewPrefix = "/WEB-INF/classes/" + newViewName;
//						jstlView.setUrl(viewPrefix + url);
//					}
//				}
//			}
//		}
		
		return view;
	}

	// START: HandlerInterceptor
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String packageStr = handler.getClass().getPackage().getName();
		if (StringUtils.isNotBlank(packageStr) && packageStr.toLowerCase().endsWith(".clr")) {
			packageStr = packageStr.substring(0, packageStr.length() - ".clr".length());
		}
		request.setAttribute(WebEnv.REQUEST_LAST_HANDLER_BASE_PACKAGE_KEY, StringUtil.defaultIfBlank(packageStr, ""));
		
		return true;
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		request.removeAttribute(WebEnv.REQUEST_LAST_HANDLER_BASE_PACKAGE_KEY);
	}
	// END: HandlerInterceptor
	
	private HttpServletRequest getRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
		return (requestAttributes != null ? requestAttributes.getRequest() : null);
	}
	
	public String getViewPath() {
		return viewPath;
	}
	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}
}