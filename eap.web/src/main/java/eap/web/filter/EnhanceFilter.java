package eap.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

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
public abstract class EnhanceFilter extends OncePerRequestFilter {
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	private String[] excludeUrlPatterns;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException 
	{
		if (excludeUrlPatterns != null && excludeUrlPatterns.length > 0) {
			for (String pattern : excludeUrlPatterns) {
				if (pathMatcher.match(pattern, request.getServletPath())) {
					filterChain.doFilter(request, response);
					return;
				}
			}
		}
		
		doFilterCleaned(request, response, filterChain);
	}
	
	protected abstract void doFilterCleaned(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;
	
	public void setExcludeUrlPatterns(String[] excludeUrlPatterns) {
		this.excludeUrlPatterns = excludeUrlPatterns;
	}
	public void setExcludeUrlPatterns(String excludeUrlPatternsStr) {
		if (excludeUrlPatternsStr != null && excludeUrlPatternsStr.length() > 0) {
			this.excludeUrlPatterns = StringUtil.split(excludeUrlPatternsStr, ",");
		}
	}
}
