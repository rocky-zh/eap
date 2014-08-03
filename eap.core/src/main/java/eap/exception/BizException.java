package eap.exception;

import java.util.Map;

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
public class BizException extends Exception {
	
	protected Map<String, Object> model;
	
	protected ErrorMsg errorMsg;
	
	public BizException() {
		super();
		this.errorMsg = new ErrorMsg();
	}
	
	public BizException(String message) {
		super(message);
		this.errorMsg = new ErrorMsg();
	}
	public BizException(String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg();
	}
	
	public BizException(String msgCode, String[] msgParams, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode, msgParams);
	}
	
	public BizException(String msgCode, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode);
	}
	
	public BizException(String msgCode, String message) {
		super(message);
		this.errorMsg = new ErrorMsg(msgCode);
	}
	
	public BizException(String msgCode, String msgParam, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode, msgParam);
	}
	
	public Map<String, Object> getModel() {
		return model;
	}

	public void setModel(Map<String, Object> model) {
		this.model = model;
	}

	public ErrorMsg getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(ErrorMsg errorMsg) {
		this.errorMsg = errorMsg;
	}
}