package eap.web.jstl.tags;

import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.form.TagWriter;

import eap.EapContext;
import eap.util.MessageUtil;
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
public class InputTag extends org.springframework.web.servlet.tags.form.InputTag {
	
	private String placeholder;
	private String placeholderCode;
	
	@Override
	protected void writeDefaultAttributes(TagWriter tagWriter) throws JspException {
		Locale locale = EapContext.getLocale();
		String uri = this.getRequestContext().getContextPath();
		
		if (StringUtil.isNotBlank(placeholderCode)) {
			placeholder = MessageUtil.getMessage(placeholderCode, null, "", locale);
		}
		if (StringUtil.isNotBlank(placeholder)) {
			this.setDynamicAttribute(uri, "placeholder", placeholder);
		}
		
		super.writeDefaultAttributes(tagWriter);
	}
	
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	public String getPlaceholderCode() {
		return placeholderCode;
	}
	public void setPlaceholderCode(String placeholderCode) {
		this.placeholderCode = placeholderCode;
	}
}