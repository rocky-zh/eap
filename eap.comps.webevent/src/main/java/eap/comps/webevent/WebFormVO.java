package eap.comps.webevent;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;

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
public class WebFormVO extends WebBaseVO {
	
	private boolean success = true;

	private Map<String, String> errors;

	private Object data;
	
//	private Map<String, String> attrs;
	
//	private String validator;
	private String inputStyles;
	
	public WebFormVO() {
	}

	public WebFormVO(Object data) {
		super();
		this.data = data;
	}

	public WebFormVO addField(String name, Object value) {
		if (data == null) {
			data = new TreeMap<String, String>();
		}

		((Map<String, String>) data).put(name, value != null ? this.htmlEscape(value.toString()): null); // htmlEscape

		return this;
	}

	public WebFormVO addError(String fieldName, String errorMsg) {
		if (errors == null) {
			success = false;
			errors = new TreeMap<String, String>();
		}
		errors.put(fieldName, this.htmlEscape(errorMsg)); // htmlEscape

		return this;
	}
	
	public boolean hasError() {
		return errors != null && errors.size() > 0;
	}
	
	private String htmlEscape(String text) {
//		return HtmlUtils.htmlEscape(text);
		return StringEscapeUtils.escapeHtml(text);
	}
	
//	public WebFormVO addAttr(String name, Object attr) {
//		if (attrs == null) {
//			attrs = new TreeMap<String, String>();
//		}
//		attrs.put(name, attr != null ? attr.toString(): null);
//		
//		return this;
//	}

	public static WebFormVO success(Object dataObj) {
		return new WebFormVO(dataObj);
	}
	public static WebFormVO success() {
		return new WebFormVO();
	}
	
	public boolean isEmpty(Object value, String field, String errorMsg) {
		if (value == null || value.toString().length() == 0) {
			this.addError(field, errorMsg);
			return true;
		}
		
		return false;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getInputStyles() {
		return inputStyles;
	}

	public void setInputStyles(String inputStyles) {
		this.inputStyles = inputStyles;
	}
}