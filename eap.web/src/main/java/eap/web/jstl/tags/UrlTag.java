package eap.web.jstl.tags;

import org.apache.commons.lang.StringUtils;

import eap.EapContext;
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
public class UrlTag extends org.springframework.web.servlet.tags.UrlTag {
	
	private String domain;
	
	private Boolean area;
	
	@Override
	public void setValue(String value) {
		WebEnv env = (WebEnv) EapContext.getEnv();
		String d = this.getDomain();
		String baseUrl = env.getDomainUrl(d);
		if (StringUtil.isBlank(baseUrl) || baseUrl.equals(WebEnv.webContextPath)) {
			baseUrl = "";
		}
		
//		if (this.getArea()) {
//			String areaPinyin = ContextHolderHelper.get().currAreaDetailsVO().getAreaPinyin();
//			if (StringUtils.isNotBlank(areaPinyin)) {
//				baseUrl += ("/" + areaPinyin);
//			}
//		}
		
		if (value.startsWith(WebEnv.webContextPath + "/")) {
			value = value.substring(WebEnv.webContextPath.length());
		}
		
		String url = baseUrl + StringUtils.defaultString(value);
		
//		if (!env.isProMode()) { // env.isDevMode()
//			String postfix = UrlUtil.getFilePostfixName(url);
//			if (StringUtils.isNotBlank(postfix)) {
//				String appendPostfixEnableRegex = env.getProperty("app.http.file.appendPostfix.enable");
//				if (StringUtils.isNotBlank(appendPostfixEnableRegex) && d.matches(appendPostfixEnableRegex)) {
//					String filterTypeRegex = env.getProperty(String.format("app.http.file.appendPostfix.filter.%s", d));
//					if (StringUtils.isBlank(filterTypeRegex)) {
//						filterTypeRegex = env.getProperty("app.http.file.appendPostfix.filter");
//					}
//					if (StringUtils.isNotBlank(filterTypeRegex) && postfix.matches(filterTypeRegex)) {
//						url = StringUtils.replaceOnce(url, "." + postfix, "." + (env.getProperty(String.format("app.http.file.appendPostfix.%s", postfix)) + "." + postfix));
//					}
//				}
//			}
//		}
		
		super.setValue(url);
	}
	
	public String getDomain() {
		if (domain != null) {
			return domain;
		} else {
			WebEnv env = (WebEnv) EapContext.getEnv();
			return env.getDomain();
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Boolean getArea() {
		return area != null ? area : false;
	}

	public void setArea(Boolean area) {
		this.area = area;
	}
}