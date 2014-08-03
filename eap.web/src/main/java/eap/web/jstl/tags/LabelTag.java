package eap.web.jstl.tags;

import java.text.MessageFormat;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.TagWriter;

import eap.EapContext;
import eap.WebEnv;
import eap.comps.codetable.CodeTable;
import eap.util.MessageUtil;
import eap.util.ReflectUtil;
import eap.util.ValidationUtil;
import eap.util.validator.Required;

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
public class LabelTag extends org.springframework.web.servlet.tags.form.LabelTag {
	
	/** 码表类型 */
	private String codeType;
	/** 码表键 */
	private String codeKey;
	/** 码表值 */
	private Object codeValue;
	
	/** 格式化参数分隔符 */
	public final static String ARGS_SEPARATOR = ",";
	/** 格式化参数 */
	private String args;
	
	private String messageCode;
	private String messageArgs;
	
	private String text;
	
	private Boolean require;
	private Boolean colon;
	
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		int instruct = super.writeTagContent(tagWriter);
		
		Locale locale = EapContext.getLocale();
		
		if (this.getRequire()) {
			BindStatus bindStatus = this.getBindStatus();
			BeanPropertyBindingResult bindingResult = ReflectUtil.getFieldValue(bindStatus, "bindingResult", BeanPropertyBindingResult.class);
			Object target = bindingResult.getTarget();
			String property = bindStatus.getExpression();
			boolean require = ValidationUtil.hasConstraintsForProperty(target.getClass(), property, new Class<?>[] {NotEmpty.class, NotBlank.class, NotNull.class, Required.class});
			if (require) {
				tagWriter.appendValue(MessageUtil.getMessage("ui.label.required.html", null, "<span class=\"required star\">*</span>", locale));
			}
		}
		
		if (StringUtils.isNotBlank(codeType)) {
			String codeName = null;
			if (StringUtils.isNotBlank(codeKey)) {
				codeName = CodeTable.getName(codeType, codeKey);
			} 
			else if (codeValue != null) {
				codeName = CodeTable.getNameByValue(codeType, codeValue.toString());
			} 
			else {
				Object value = this.getBindStatus().getValue();
				codeName = CodeTable.getNameByValue(codeType, (value != null ? value.toString() : null));
			}
			
			if (StringUtils.isNotBlank(codeName) && args != null && args.length() > 0) {
				codeName = MessageFormat.format(codeName, StringUtils.split(args, ARGS_SEPARATOR));
			}
			
			tagWriter.appendValue(this.getDisplayString(codeName));
		} 
		else if (StringUtils.isNotBlank(messageCode)) {
			String[] messageArgsArr = null;
			if (StringUtils.isNotBlank(messageArgs) && messageArgs != null && messageArgs.length() > 0) {
				messageArgsArr = StringUtils.split(messageArgs, ARGS_SEPARATOR);
			}
			String message = MessageUtil.getMessage(messageCode, messageArgsArr, "", locale);
			
			tagWriter.appendValue(this.getDisplayString(message));
		} 
		else if (StringUtils.isNotBlank(text)) {
			tagWriter.appendValue(this.getDisplayString(text));
		}
		
		return instruct;
	}
	
	@Override
	public int doEndTag() throws JspException {
		if (this.getColon()) {
			TagWriter tagWriter = ReflectUtil.getFieldValue(this, "tagWriter", TagWriter.class);
			tagWriter.appendValue(MessageUtil.getMessage("ui.label.colon", null, "：", EapContext.getLocale()));
		}
		
		return super.doEndTag();
	}

	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getCodeKey() {
		return codeKey;
	}
	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}

	public Object getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(Object codeValue) {
		this.codeValue = codeValue;
	}

	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessageArgs() {
		return messageArgs;
	}
	public void setMessageArgs(String messageArgs) {
		this.messageArgs = messageArgs;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public Boolean getRequire() {
		if (require != null) {
			return require;
		} else {
			WebEnv env = (WebEnv) EapContext.getEnv();
			return Boolean.valueOf(env.getProperty("app.web.tag.label.require", "true"));
		}
	}
	public void setRequire(Boolean require) {
		this.require = require;
	}

	public Boolean getColon() {
		if (colon != null) {
			return colon;
		} else {
			WebEnv env = (WebEnv) EapContext.getEnv();
			return Boolean.valueOf(env.getProperty("app.web.tag.label.colon", "true"));
		}
	}
	public void setColon(Boolean colon) {
		this.colon = colon;
	}
}