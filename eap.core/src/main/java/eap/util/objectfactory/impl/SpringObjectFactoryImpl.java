package eap.util.objectfactory.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

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
public class SpringObjectFactoryImpl implements IObjectFactory, BeanFactoryAware {
	
	private BeanFactory beanFactory;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	@Override
	public Object getObject(String objectName) {
		return beanFactory.getBean(objectName);
	}

	@Override
	public <T> T getObject(String objectName, Class<T> requireType) {
		return beanFactory.getBean(objectName, requireType);
	}
}