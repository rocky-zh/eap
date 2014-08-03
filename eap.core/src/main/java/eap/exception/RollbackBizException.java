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
public class RollbackBizException extends BizException {
	
	public RollbackBizException() {
		super();
	}
	
	public RollbackBizException(BizException e) {
		super(e.getMessage(), e);
		this.errorMsg = e.errorMsg;
		this.setModel(e.getModel());
	}
	
	public RollbackBizException(String message) {
		super(message);
	}
	public RollbackBizException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public RollbackBizException(String msgCode, String[] msgParams, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode, msgParams);
	}
	
	public RollbackBizException(String msgCode, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode);
	}
	
	public RollbackBizException(String msgCode, String message) {
		super(message);
		this.errorMsg = new ErrorMsg(msgCode);
	}
	
	public RollbackBizException(String msgCode, String msgParam, String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = new ErrorMsg(msgCode, msgParam);
	}
	public RollbackBizException(String msgCode, String msgParam, String message) {
		super(message);
		this.errorMsg = new ErrorMsg(msgCode, msgParam);
	}
	
	public static void throwRollbackableBizException(Exception e) throws RollbackBizException {
		if (e instanceof RollbackBizException) {
			throw (RollbackBizException) e;
		} 
		else if (e instanceof BizException) {
			throw new RollbackBizException((BizException) e);
		} 
		else {
			throw new RollbackBizException(e.getMessage(), e);
		}
	}
}