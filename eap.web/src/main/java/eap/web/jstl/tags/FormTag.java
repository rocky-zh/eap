package eap.web.jstl.tags;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.tags.form.TagWriter;

import eap.EapContext;
import eap.WebEnv;
import eap.comps.datastore.DataStore;
import eap.comps.token.TokenManager;
import eap.comps.webevent.WebEvents;
import eap.comps.webevent.WebEventsHelper;
import eap.comps.webevent.WebFormVO;
import eap.util.ValidationUtil;

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
public class FormTag extends org.springframework.web.servlet.tags.form.FormTag {
	
	private AtomicInteger formIdCounter = new AtomicInteger(0);
	
	private Boolean token;
	private String tokenField;
	
	private String formIdField;
	
	private String domain;
	
	private Boolean inputStyle;
	
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		if (StringUtils.isBlank(this.getId())) {
			this.setId(this.getUniqueFormId());
		}
		
		int r = super.writeTagContent(tagWriter);
		
		String formId = this.resolveId();
		this.writeHidden(tagWriter, this.getFormIdField(), formId);
		
		if (this.getToken() && "POST".equalsIgnoreCase(this.getMethod())) {
			WebEnv env = (WebEnv) EapContext.getEnv();
			String tokenId = TokenManager.applyToken(env.getFormTokenStoreKey(), DataStore.getDataScope(DataStore.SCOPE_SESSION)); 
			this.writeHidden(tagWriter, this.getTokenField(), tokenId);
		}
		
		if (this.getInputStyle()) {
//			BindStatus bindStatus = this.getBindStatus();
//			String path = bindStatus.getPath();
			String path = this.getModelAttribute();
			Object target = this.pageContext.getAttribute(path, PageContext.REQUEST_SCOPE);
			if (target != null) {
				Map<String, Map<String, Object>> inputStyles = ValidationUtil.getInputStyles(target.getClass());
				if (inputStyles != null && inputStyles.size() > 0) {
					WebEvents webEvents = WebEventsHelper.getWebEvents((HttpServletRequest) this.pageContext.getRequest());
					
					String inputStylesId = target.getClass().getSimpleName();
					webEvents.setInputStyles(inputStylesId, inputStyles);
					
					WebFormVO webFormVO = webEvents.getForm(formId, true);
					webFormVO.setInputStyles(inputStylesId);
				}
			}
		}
		
		return r;
	}
	private void writeHidden(TagWriter tagWriter, String name, String value) throws JspException {
		tagWriter.appendValue(String.format("<input type=\"hidden\" name=\"%s\" value=\"%s\" />", name, value));
	}
	
	private String getUniqueFormId() {
		int id = formIdCounter.incrementAndGet();
		if (id >= Integer.MAX_VALUE) {
			id = 0;
			formIdCounter.set(id);
		}
		
		WebEnv env = (WebEnv) EapContext.getEnv();
		return env.getFormIdPrefix() + String.valueOf(id);
	}
	
	@Override
	public void setAction(String action) {
		WebEnv env = (WebEnv) EapContext.getEnv();
		if (StringUtils.isNotBlank(action) && !action.startsWith("/" + env.getDomain() + "/")) {
			String baseUrl = env.getDomainUrl(StringUtils.defaultString(domain, env.getDomain()));
			String fullAction = StringUtils.defaultString(baseUrl) + StringUtils.defaultString(action);
			super.setAction(fullAction);
		} else {
			super.setAction(action);
		}
	}
	
	@Override
	protected String getMethod() {
		return StringUtils.defaultIfBlank(super.getMethod(), "POST");
	}
	
	public Boolean getToken() {
		return token != null ? token : true;
	}
	public void setToken(Boolean token) {
		this.token = token;
	}
	
	public String getTokenField() {
		if (tokenField != null) {
			return tokenField;
		} else {
			WebEnv env = (WebEnv) EapContext.getEnv();
			return env.getFormTokenField();
		}
	}
	public void setTokenField(String tokenField) {
		this.tokenField = tokenField;
	}
	
	public String getFormIdField() {
		if (formIdField != null) {
			return formIdField;
		} else {
			WebEnv env = (WebEnv) EapContext.getEnv();
			return env.getFormIdField();
		}
	}
	public void setFormIdField(String formIdField) {
		this.formIdField = formIdField;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public Boolean getInputStyle() {
		if (inputStyle != null) {
			return inputStyle;
		} else {
			WebEnv env = (WebEnv) EapContext.getEnv();
			return env.getProperty("app.web.form.inputStyle.client", Boolean.class, true);
		}
	}
	public void setInputStyle(Boolean inputStyle) {
		this.inputStyle = inputStyle;
	}
}