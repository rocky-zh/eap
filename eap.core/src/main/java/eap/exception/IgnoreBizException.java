package eap.exception;

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
public class IgnoreBizException extends BizException {
	
	public IgnoreBizException() {
		super();
	}
	
	public IgnoreBizException(String message) {
		super(message);
	}
	public IgnoreBizException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public IgnoreBizException(String msgCode, String[] msgParams, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode, msgParams);
	}
	
	public IgnoreBizException(String msgCode, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode);
	}
	
	public IgnoreBizException(String msgCode, String message) {
		super(message);
		this.errorMsg = new ErrorMsg(msgCode);
	}
	
	public IgnoreBizException(String msgCode, String msgParam, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode, msgParam);
	}
}
