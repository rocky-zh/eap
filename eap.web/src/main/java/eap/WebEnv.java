package eap;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

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
public class WebEnv extends Env {
	
	public final static String VALUES_SEPARATOR = ",";
	
	public static final String SESSION_USER_DETAILS_KEY = "__user_details";
	
	public static final String REQUEST_LAST_HANDLER_BASE_PACKAGE_KEY = "__last_handler_object_base_package";
	
	public static String webContextPath;
	
//	public String getViewPrefix() {
//		return this.getProperty("app.web.view.prefix", "/WEB-INF/views/");
//	}
//	public String getViewSuffix() {
//		return this.getProperty("app.web.view.suffix", ".jsp");
//	}
	public String getFormIdPrefix() {
		return this.getProperty("app.web.form.id.prefix", "__form_");
	}
	public String getFormIdField() {
		return this.getProperty("app.web.form.id.field", "__form_id");
	}
	public boolean isFormTokenEnable() {
		return Boolean.parseBoolean(this.getProperty("app.web.form.token.enable", "true"));
	}
	public Integer getFormTokenMaxCount() {
		return this.getProperty("app.web.form.token.maxCount", Integer.class, 16);
	}
	public Integer getFormTokenLength() {
		return this.getProperty("app.web.form.token.length", Integer.class, 8);
	}
	public String getFormTokenField() {
		return this.getProperty("app.web.form.token.field", "__form_token");
	}
	public String getFormTokenStoreKey() {
		return this.getProperty("app.web.form.token.store.key", "__token_store");
	}
	
	public String getDomain() {
		return this.getProperty("app.web.domain", "");
	}
	public String getDomainUrl() {
		return getDomainUrl(getDomain());
	}
	public String getDomainUrl(String domain) {
		if (StringUtils.isBlank(domain)) {
			return webContextPath;
		}
		
		return this.getProperty(String.format("app.web.domain.%s", domain), webContextPath); 
	}
	public Map<String, String> getDomainUrls() {
		Map<String, String> urlMap = new HashMap<String, String>();
		urlMap.put("", getDomainUrl());
		
		String[] domains = StringUtils.split(this.getProperty("app.web.domain.all", String.class, ""), VALUES_SEPARATOR);
		for (String domain : domains) {
			urlMap.put(domain, this.getDomainUrl(domain));
		}
		
		return urlMap;
	}
	
	public String getWebRootPath() {
		return this.getProperty("app.web.rootPath", ""); // System.getProperty("user.dir")
	}
	public String getDomainPath(String domain) {
		Assert.hasText(domain, "'domain' must not be empty");
		
		return this.getProperty(String.format("app.web.domain.%s.path", domain), this.getWebRootPath()); 
	}
}
