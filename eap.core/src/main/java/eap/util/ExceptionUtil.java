package eap.util;

import eap.exception.BizException;
import eap.exception.IgnoreBizException;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本	   修改人		 修改时间		 修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class ExceptionUtil {
	
	public static Throwable getRootCause(Exception e) {
		if (e == null) {
			return null;
		}
		
		Throwable cause = e;
		while (!(cause instanceof BizException) && cause.getCause() != null && cause.getCause() != cause) { // InvocationTargetException
			cause = cause.getCause();
		}
		
		return cause;
	}
	
	public static void throwIgnoreBizException(BizException e) throws IgnoreBizException{
		if (e instanceof IgnoreBizException) {
			throw (IgnoreBizException) e;
		}
		
		IgnoreBizException ignoreBizException = new IgnoreBizException(e.getMessage(), e);
		ignoreBizException.setErrorMsg(e.getErrorMsg());
		ignoreBizException.setModel(e.getModel());
		
		throw ignoreBizException;
	}
	
	public static void main(String[] args) {
		try {
			try {
				try {
					m3();
				} catch (Exception e) {
					throw new BizException("测试1", e);
//					throw new IllegalStateException("测试1", e);
				}
			} catch (Exception e) {
				throw new IllegalStateException("测试2", e);
			}
		} catch (Exception e) {
			getRootCause(e).printStackTrace();
//			e.printStackTrace();
			
			
		}
	}
	
	public static void m1() {
		throw new IllegalArgumentException("测试");
	}
	public static void m2() {
		m1();
	}
	public static void m3() {
		m2();
	}
	
}
