package eap.util.objectfactory.impl;

import eap.util.objectfactory.IObjectFactory;

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
public class NewObjectFactoryImpl implements IObjectFactory {

	@Override
	public Object getObject(String objectName) {
		try {
			return Class.forName(objectName).newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	@Override
	public <T> T getObject(String objectName, Class<T> requireType) {
		return (T) this.getObject(objectName);
	}
}