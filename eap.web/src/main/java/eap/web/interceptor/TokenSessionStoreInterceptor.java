package eap.web.interceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import eap.EapContext;
import eap.Env;
import eap.comps.datastore.DataStore;
import eap.comps.token.TokenExpiredException;
import eap.comps.token.TokenManager;
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
public class TokenSessionStoreInterceptor extends HandlerInterceptorAdapter {
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	private String[] excludeUrlPatterns;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String method = request.getMethod();
		boolean isPost = "POST".equals(method);
		
		if (isPost && !request.getDispatcherType().equals(DispatcherType.FORWARD)) { // TODO
			if (excludeUrlPatterns != null && excludeUrlPatterns.length > 0) {
				for (String pattern : excludeUrlPatterns) {
					if (pathMatcher.match(pattern, request.getServletPath())) {
						return true;
					}
				}
			}
			
			Env env = EapContext.getEnv();
			String tokenStoreId = env.getProperty("app.web.form.tokenStoreId", "__formTokenStore");
			String tokenField = env.getProperty("app.web.form.tokenField", "__ft");
			
			String token = null;
			if (request instanceof MultipartHttpServletRequest) {
				token = ((MultipartHttpServletRequest) request).getParameter(tokenField);
			} else {
				token = request.getParameter(tokenField);
			}
			
			if (StringUtil.isNotBlank(token) && TokenManager.destoryToken(tokenStoreId, token, DataStore.getDataScope(DataStore.SCOPE_SESSION))) { // request.getSession()
				return true;
			}
			
			throw new TokenExpiredException("e.TokenExpiredException", "请求过期"); // 请求过期
		}
		
		return true;
	}
	
	public String[] getExcludeUrlPatterns() {
		return excludeUrlPatterns;
	}
	public void setExcludeUrlPatterns(String[] excludeUrlPatterns) {
		this.excludeUrlPatterns = excludeUrlPatterns;
	}
	public void setExcludeUrlPatterns(String excludeUrlPatternsStr) {
		if (excludeUrlPatternsStr != null && excludeUrlPatternsStr.length() > 0) {
			this.excludeUrlPatterns = StringUtil.split(excludeUrlPatternsStr, ",");
		}
	}
	
	public static void main(String[] args) {
		PathMatcher pm = new AntPathMatcher();
		System.out.println(pm.match("/demo/*", "/demo/index"));
	}
}