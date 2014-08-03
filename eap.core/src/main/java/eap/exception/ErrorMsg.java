package eap.exception;

import org.apache.commons.lang.StringUtils;

import eap.util.MessageUtil;


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
public class ErrorMsg {
	
	private String code;
	private String[] params;
	
	public ErrorMsg() {
		super();
	}
	
	public ErrorMsg(String code) {
		super();
		this.code = code;
	}
	
	public ErrorMsg(String code, String param) {
		this.code = code;
		this.params = new String[] { param };
	}

	public ErrorMsg(String code, String[] params) {
		super();
		this.code = code;
		this.params = params;
	}
	
	public String getMessage() {
		String message = null;
		if (StringUtils.isNotBlank(code)) {
			message = MessageUtil.getMessage(code, params, "");
		}
		return message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}
}
