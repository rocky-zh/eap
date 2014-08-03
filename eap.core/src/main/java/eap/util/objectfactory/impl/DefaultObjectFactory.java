package eap.util.objectfactory.impl;

import eap.util.objectfactory.IObjectFactory;
import eap.util.objectfactory.ObjectFactory;

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
public class DefaultObjectFactory implements IObjectFactory {

	public Object getObject(String objectName) {
		return getObject(objectName, Object.class);
	}

	public <T> T getObject(String objectName, Class<T> requireType) {
		return ObjectFactory.getObject(objectName, requireType);
	}

}
