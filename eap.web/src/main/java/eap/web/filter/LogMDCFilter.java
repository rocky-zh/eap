package eap.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

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
public class LogMDCFilter extends EnhanceFilter {
	
	public static final String MDC_KEY_IP = "ip";
	
	@Override
	protected void doFilterCleaned(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException 
	{
		try {
			if (MDC.get(MDC_KEY_IP) == null) { // current thread already set ip
				MDC.put(MDC_KEY_IP, HttpUtil.getRemoteAddr(request));
			}
			
//			MDC.put("serverIp", System.getProperty("app.ip", InetAddress.getLocalHost().getHostAddress()));
//			MDC.put("http_method", request.getMethod().toLowerCase());
//			MDC.put("http_request_uri", request.getRequestURI());
//			MDC.put("http_user_agent", StringUtils.defaultIfBlank(request.getHeader("user-agent"), ""));
//			MDC.put("http_referer", StringUtils.defaultIfBlank(request.getHeader("referer"), ""));
		
//			Logger.getLogger(WebFilter.class).info(request.getRequestURL() + " " + request.getQueryString() + " " + request.getHeader("user-agent") + " " + request.getHeader("referer"));
			
			super.doFilter(request, response, filterChain);
		} finally {
			MDC.clear();
		}
	}
}
