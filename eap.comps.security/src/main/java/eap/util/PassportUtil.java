package eap.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

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
public class PassportUtil {
	
	public static void sendRedirectToPassport(HttpServletRequest request, HttpServletResponse response, 
		String appId, String username, String targetUrl) throws IOException 
	{
		Env env = EapContext.getEnv();
		if ("true".equalsIgnoreCase(env.getProperty("security.sso.enable"))) {
			request.getSession().setAttribute("_SECURITY_SSO_AUTO", "true");
			request.getSession().setAttribute("_SECURITY_SSO_CALLBACK_URL", targetUrl);
		}
		
		response.sendRedirect(genPassportUrl(appId, username, targetUrl));
	}
	private static String genPassportUrl(String appId, String username, String targetUrl) {
		Env env = EapContext.getEnv();
		
		PassportToken pt = new PassportToken();
		pt.setAi(appId);
		pt.setU(username);
		pt.setT(System.currentTimeMillis() + "");
		pt.setTu(StringUtil.defaultIfBlank(targetUrl, ""));
		pt.setCs(EDcodeUtil.md5(pt.getAi() + pt.getU() + pt.getT() + pt.getTu()));
		
		String url = UrlUtil.appendParams(env.getProperty("security.passport.url", "/passport"),
			new String[][] {
				{"appId", appId},
				{"token", genToken(pt)}
			}
		);
		
		return url;
	}
	private static String genToken(PassportToken pt) {
		String ptJson = JsonUtil.toJson(pt);
		return EDcodeUtil.desEncodeAsHex(ptJson, EapContext.getEnv().getProperty("security.passport.singature"));
	}
	
	
	public static PassportToken checkToken(HttpServletRequest request) throws IllegalArgumentException {
		return checkToken(request.getParameter("appId"), request.getParameter("token"));
	}
	public static PassportToken checkToken(String appId, String token) throws IllegalArgumentException {
		Env env = EapContext.getEnv();
		
		Assert.hasText(appId, "passport.1 invalid");
		Assert.hasText(token, "passport.2 invalid");
		
		String singature = env.getProperty(String.format("security.passport.%s.singature", appId));
		if (StringUtil.isBlank(singature)) {
			singature = env.getProperty("security.passport.singature");
		}
		String tokenPlaintext = null;
		try {
			tokenPlaintext = EDcodeUtil.aesDecodeForHexAsString(token, singature);
		} catch (Exception e) {
			throw new IllegalArgumentException("token invalid");
		}
		
		PassportToken pt = null;
		try {
			pt = JsonUtil.parseJson(tokenPlaintext, PassportToken.class);
		} catch (Exception e) {
			throw new IllegalArgumentException("token invalid");
		}
			
		Assert.hasText(pt.getAi(), "token.1 invalid");
		Assert.hasText(pt.getU(), "token.2 invalid");
		Assert.hasText(pt.getT(), "token.3 invalid");
		Assert.isTrue(pt.getCs().equals(EDcodeUtil.md5(pt.getAi() + pt.getU() + pt.getT() + pt.getTu())), "token.4 invalid");
		
		long timestamp = new Long(pt.getT());
		if ((timestamp + env.getProperty("security.passport.tokenMaxAliveTime", Integer.class)) <= System.currentTimeMillis()) {
			throw new IllegalArgumentException("token expired");
		}
		
		// TODO 销毁当前token，不能再次使用
		
		return pt;
	}
	
	public static class PassportToken {
		
		private String ai; // appId
		private String u; // username
		private String t; // timestamp
		private String tu; // targetUrl
		private String cs; // checksum
		
		public String getAi() {
			return ai;
		}
		public void setAi(String ai) {
			this.ai = ai;
		}
		public String getU() {
			return u;
		}
		public void setU(String u) {
			this.u = u;
		}
		public String getT() {
			return t;
		}
		public void setT(String t) {
			this.t = t;
		}
		public String getTu() {
			return tu;
		}
		public void setTu(String tu) {
			this.tu = tu;
		}
		public String getCs() {
			return cs;
		}
		public void setCs(String cs) {
			this.cs = cs;
		}
	}
}