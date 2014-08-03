package eap.comps.cookie;

import javax.servlet.http.HttpServletRequest;

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
public interface ICookieSecurityStrategy {
	
	public int getLevel();

	public void preHandle(CookieGenerator cookieGenerator);
	
	public String encodeCookieValue(HttpServletRequest request, CookieGenerator cookieGenerator, String cookieValue);
	
	public String decodeCookieValue(HttpServletRequest request, CookieGenerator cookieGenerator, String cookieValue);
}