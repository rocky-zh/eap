package eap.exception;

import java.util.Collection;

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
public class BizAssert {
	
	public static void isNull(Object obj, String msgCode, String[] msgParams, String message) throws BizException {
		if (obj == null) {
			throw new BizException(msgCode, msgParams, message, null);
		}
	}
	public static void isNull(Object obj, String msgCode, String msgParam, String message) throws BizException {
		if (obj == null) {
			throw new BizException(msgCode, new String[] {msgParam}, message, null);
		}
	}
	public static void isNull(Object obj, String msgCode, String message) throws BizException {
		if (obj == null) {
			throw new BizException(msgCode, message, null);
		}
	}
	
	public static void isEmptyParam(Object param, String paramName, String message) throws BizException {
		if (param == null || param.toString().length() == 0) {
			throw new BizException("e.paramMustNotBeEmpty", paramName, message, null);
		}
	}
	
	public static void isEmpty(Object obj, String msgCode, String[] msgParams, String message) throws BizException {
		if (obj == null || obj.toString().length() == 0) {
			throw new BizException(msgCode, msgParams, message, null);
		}
	}
	public static void isEmpty(Object obj, String msgCode, String msgParam, String message) throws BizException {
		if (obj == null || obj.toString().length() == 0) {
			throw new BizException(msgCode, new String[] {msgParam}, message, null);
		}
	}
	public static void isEmpty(Object obj, String msgCode, String message) throws BizException {
		if (obj == null || obj.toString().length() == 0) {
			throw new BizException(msgCode, message, null);
		}
	}
	
	public static void isTrue(boolean expression, String msgCode, String[] msgParams, String message) throws BizException {
		if (expression) {
			throw new BizException(msgCode, msgParams, message, null);
		}
	}
	public static void isTrue(boolean expression, String msgCode, String msgParam, String message) throws BizException {
		if (expression) {
			throw new BizException(msgCode, new String[] {msgParam}, message, null);
		}
	}
	public static void isTrue(boolean expression, String msgCode, String message) throws BizException {
		if (expression) {
			throw new BizException(msgCode, message, null);
		}
	}
	public static void isTrue(boolean expression, String message) throws BizException {
		if (expression) {
			throw new BizException(message);
		}
	}
	public static void isFalse(boolean expression, String message) throws BizException {
		if (!expression) {
			throw new BizException(message);
		}
	}
	
	
	public static void isNullR(Object obj, String msgCode, String[] msgParams, String message) throws RollbackBizException {
		if (obj == null) {
			throw new RollbackBizException(msgCode, msgParams, message, null);
		}
	}
	public static void isNullR(Object obj, String msgCode, String msgParam, String message) throws RollbackBizException {
		if (obj == null) {
			throw new RollbackBizException(msgCode, new String[] {msgParam}, message, null);
		}
	}
	public static void isNullR(Object obj, String msgCode, String message) throws RollbackBizException {
		if (obj == null) {
			throw new RollbackBizException(msgCode, message);
		}
	}
	
	public static void isEmptyRParam(Object param, String paramName, String message) throws RollbackBizException {
		if (param == null || param.toString().length() == 0) {
			throw new RollbackBizException("e.paramMustNotBeEmpty", paramName, message, null);
		}
	}
	
	public static void isEmptyR(Object obj, String msgCode, String[] msgParams, String message) throws RollbackBizException {
		if (obj == null || obj.toString().length() == 0) {
			throw new RollbackBizException(msgCode, msgParams, message, null);
		}
	}
	public static void isEmptyR(Object obj, String msgCode, String msgParam, String message) throws RollbackBizException {
		if (obj == null || obj.toString().length() == 0) {
			throw new RollbackBizException(msgCode, new String[] {msgParam}, message, null);
		}
	}
	public static void isEmptyR(Object obj, String msgCode, String message) throws RollbackBizException {
		if (obj == null || obj.toString().length() == 0) {
			throw new RollbackBizException(msgCode, message);
		}
	}
	
	public static void isTrueR(boolean expression, String msgCode, String[] msgParams, String message) throws RollbackBizException {
		if (expression) {
			throw new RollbackBizException(msgCode, msgParams, message, null);
		}
	}
	public static void isTrueR(boolean expression, String msgCode, String msgParam, String message) throws RollbackBizException {
		if (expression) {
			throw new RollbackBizException(msgCode, new String[] {msgParam}, message, null);
		}
	}
	public static void isTrueR(boolean expression, String msgCode, String message) throws RollbackBizException {
		if (expression) {
			throw new RollbackBizException(msgCode, message);
		}
	}
	
	public static void hasLength(Collection<?> collection, String msgCode, String message) throws BizException{
		hasLength(collection, msgCode, null, message);
	}
	public static void hasLength(Collection<?> collection, String msgCode, String[] msgParams, String message) throws BizException{
		if(collection != null && collection.size() > 0){
			return;
		}
		throw new BizException(msgCode, msgParams, message, null);
	}
	
	
	public static void hasLengthR(Collection<?> collection, String msgCode, String message) throws RollbackBizException{
		hasLengthR(collection, msgCode, null, message);
	}
	public static void hasLengthR(Collection<?> collection, String msgCode, String[] msgParams, String message) throws RollbackBizException{
		if(collection != null && collection.size() > 0){
			return;
		}
		throw new RollbackBizException(msgCode, msgParams, message, null);
	}
}