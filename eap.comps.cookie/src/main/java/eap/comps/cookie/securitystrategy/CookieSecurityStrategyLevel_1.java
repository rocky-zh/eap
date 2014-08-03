package eap.comps.cookie.securitystrategy;

import javax.servlet.http.HttpServletRequest;

import eap.comps.cookie.CookieGenerator;
import eap.comps.cookie.ICookieSecurityStrategy;

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
public class CookieSecurityStrategyLevel_1 implements ICookieSecurityStrategy {

	@Override
	public int getLevel() {
		return 1;
	}
	
	@Override
	public void preHandle(CookieGenerator cookieGenerator) {

	}
	
	@Override
	public String encodeCookieValue(HttpServletRequest request, CookieGenerator cookieGenerator, String cookieValue) {
		return cookieValue;
	}
	
	@Override
	public String decodeCookieValue(HttpServletRequest request, CookieGenerator cookieGenerator, String cookieValue) {
		return cookieValue;
	}
}