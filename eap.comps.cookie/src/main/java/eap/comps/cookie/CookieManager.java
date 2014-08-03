package eap.comps.cookie;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * @see org.springframework.web.util.CookieGenerator
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class CookieManager {
	
	private static Map<Integer, ICookieSecurityStrategy> securityStrategys = Collections.synchronizedMap(new HashMap<Integer, ICookieSecurityStrategy>());
	
	private static Map<String, CookieGenerator> cookieGenerators = Collections.synchronizedMap(new HashMap<String, CookieGenerator>());
	
	public void init() {
		for (Entry<String, CookieGenerator> entry : cookieGenerators.entrySet()) {
			CookieGenerator cookieGenerator = entry.getValue();
			ICookieSecurityStrategy securityStrategy = getSecurityStrategy(cookieGenerator.getSecurityLevel());
			if (securityStrategy != null) {
				securityStrategy.preHandle(cookieGenerator);
			}
		}
	}
	
//	public static Cookie[] getCookies(HttpServletRequest request) {
//		return request.getCookies();
//	}
	
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(cookieName)) {
					CookieGenerator cookieGenerator = getCookieGenerator(cookieName);
					ICookieSecurityStrategy securityStrategy = getSecurityStrategy(cookieGenerator.getSecurityLevel());
					if (securityStrategy != null) {
						Cookie plainCookie = (Cookie) cookie.clone();
						plainCookie.setValue(securityStrategy.decodeCookieValue(request, cookieGenerator, cookie.getValue()));
						
						return plainCookie;
					}
					
					return cookie;
				}
			}
		}
		
		return null;
	}
	
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
		CookieGenerator cookieGenerator = getCookieGenerator(cookieName);
		if (request.getSession().getServletContext().getMajorVersion() < 3) { // servlet < 3.0
			cookieGenerator.setCookieHttpOnly(false);
		}
		ICookieSecurityStrategy securityStrategy = getSecurityStrategy(cookieGenerator.getSecurityLevel());
		if (securityStrategy != null) {
			cookieValue = securityStrategy.decodeCookieValue(request, cookieGenerator, cookieValue);
		}
		
//		cookieGenerator.setCookiePath(request.getSession().getServletContext().getContextPath() + "/")
		cookieGenerator.addCookie(response, cookieValue);
	}
	
	public static void removeCookie(HttpServletResponse response, String cookieName) {
		CookieGenerator cookieGenerator = getCookieGenerator(cookieName);
		cookieGenerator.removeCookie(response);
	}
	
	public static ICookieSecurityStrategy getSecurityStrategy(int level) {
		return securityStrategys.get(level);
	}
	
	public static CookieGenerator getCookieGenerator(String cookieName) {
		CookieGenerator cookieGenerator = cookieGenerators.get(cookieName);
		if (cookieGenerator == null) {
			throw new IllegalArgumentException(String.format("unconfigured cookieGenerator[%s] in CookieManager", cookieName));
		}
		
		return cookieGenerator;
	}
	
	public void setSecurityStrategyList(List<ICookieSecurityStrategy> cssList) {
		for (ICookieSecurityStrategy css : cssList) {
			securityStrategys.put(css.getLevel(), css);
		}
	}
	
	public void setCookieGeneratorList(List<CookieGenerator> cgList) {
		for (CookieGenerator cg : cgList) {
			cookieGenerators.put(cg.getCookieName(), cg);
		}
	}
}