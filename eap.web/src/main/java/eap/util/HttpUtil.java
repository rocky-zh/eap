package eap.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import eap.EapContext;
import eap.Env;

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
public class HttpUtil {
	
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		if (StringUtils.isBlank(cookieName)) {
			return null;
		}
		
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}
		
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				return cookie;
			}
		}
		
		return null;
	}
	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		if (cookie != null) {
			return cookie.getValue();
		}
		
		return null;
	}
	public static Map<String, String> getCookieValues(HttpServletRequest request, String... cookieNames) {
		Map<String, String> cookieValues = new HashMap<String, String>();
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (ArrayUtils.indexOf(cookieNames, cookie.getName()) != -1) {
					cookieValues.put(cookie.getName(), cookie.getValue());
				}
			}
		}
		
		return cookieValues;
	}
	
	public static boolean isAjaxRequest(HttpServletRequest request) {
		if ("text/html;type=ajax".equals(request.getHeader("Accept")) 
				|| "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) 
				|| StringUtils.isNotBlank(request.getParameter("AjaxSource"))) {
			return true;
		}
		
		return false;
	}
	
	public static String getRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		} 
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");  
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
		}
		if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		if (StringUtils.isNotBlank(ip)) {
			String[] ipArr = ip.split(" |,");
			if (ipArr != null && ipArr.length > 0) {
				return ipArr[ipArr.length - 1];
			}
		}
		
		return ip;
	}
	
	public static String getParamDecoded(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
//			return StringUtil.isNotBlank(value) ? URLDecoder.decode(value) : null;
			Env env = EapContext.getEnv();
			String doplayContainerEncoding = env.getProperty("app.deployContainer.encoding", "iso8859-1");
			if (!env.getEncoding().equalsIgnoreCase(doplayContainerEncoding)) {
				return StringUtil.isNotBlank(value) ? new String(value.getBytes(doplayContainerEncoding), "UTF-8") : null;
			} else {
				return value;
			}
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static Map<String, Object> getRequsetMap(HttpServletRequest request) {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Enumeration<String> attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			requestMap.put(attrName, request.getAttribute(attrName));
		}
		return requestMap;
	}
	public static Map<String, Object> getSessionMap(HttpServletRequest request) {
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		Enumeration<String> attrNames = request.getSession(true).getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			sessionMap.put(attrName, request.getSession(true).getAttribute(attrName));
		}
		return sessionMap;
	}
	public static Map<String, String[]> getRequestParamMap(HttpServletRequest request) {
		return request.getParameterMap();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String ip = "1.1.1.1, 2.2.2.2";
		if (StringUtils.isNotBlank(ip)) {
			String[] ipArr = ip.split(" |,");
			if (ipArr != null && ipArr.length > 0) {
				System.out.println(ipArr[ipArr.length - 1]);
			}
		}
		
//		String s = "%D6%D0%B9%FA";
//		System.out.println(URLDecoder.decode(s, "GBK"));
//		
//		String s1 = URLDecoder.decode("中国", "UTF-8");
//		System.out.println(URLDecoder.decode(s1));
//		
//		String s2 = "%E9%A2%86%E5%8F%96%E5%AE%89%E5%85%A8%E5%88%B0%E5%AE%B6%E4%BF%9D%E9%9A%9C";
//		System.out.println(new String(s2.getBytes("iso8859-1"), "UTF-8"));
	}
}