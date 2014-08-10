package eap.comps.cache.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

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
public class NamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("memcache", new MemcacheBeanDefinitionParser());
		registerBeanDefinitionParser("redis", new RedisBeanDefinitionParser());
	}
}
