package eap.comps.datastore.config;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.Cache;
import org.w3c.dom.Element;

import eap.EapContext;
import eap.Env;
import eap.comps.datastore.DataStore;
import eap.comps.datastore.scope.MemScopeImpl;
import eap.comps.datastore.scope.RequestScopeImpl;
import eap.comps.datastore.scope.ServletContextScopeImpl;
import eap.comps.datastore.scope.SessionScopeImpl;

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
public class InitBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	@Override
	protected Class<?> getBeanClass(Element element) {
		return DataStore.class;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		element.setAttribute("id", "eap_comps_dataStore");
		
		String memCacheManager = element.getAttribute("memCacheManager");
		String memCacheName = element.hasAttribute("memCacheName") ? element.getAttribute("memCacheName") : "default";
		
		List dataScopeList = new ManagedList(4);
		dataScopeList.add(new RequestScopeImpl());
		dataScopeList.add(new SessionScopeImpl());
		dataScopeList.add(new ServletContextScopeImpl());
		
		if (memCacheManager != null && memCacheManager.length() > 0) {
			RootBeanDefinition cacheDef = new RootBeanDefinition(Cache.class);
			String cacheId = parserContext.getReaderContext().generateBeanName(cacheDef);
			cacheDef.setSource(source);
			cacheDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			cacheDef.setFactoryBeanName(memCacheManager);
			cacheDef.setFactoryMethodName("getCache");
			ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
			constructorArgumentValues.addIndexedArgumentValue(0, memCacheName);
			cacheDef.setConstructorArgumentValues(constructorArgumentValues);
			parserContext.getRegistry().registerBeanDefinition(cacheId, cacheDef);
			parserContext.registerComponent(new BeanComponentDefinition(cacheDef, cacheId));
			
			RootBeanDefinition memScopeDef = new RootBeanDefinition(MemScopeImpl.class);
			String memScopeId = parserContext.getReaderContext().generateBeanName(memScopeDef);
			memScopeDef.setSource(source);
			memScopeDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			memScopeDef.getPropertyValues().add("cache", new RuntimeBeanReference(cacheId));
			parserContext.getRegistry().registerBeanDefinition(memScopeId, memScopeDef);
			parserContext.registerComponent(new BeanComponentDefinition(memScopeDef, memScopeId));
			
			dataScopeList.add(new RuntimeBeanReference(memScopeId));
		}
		
		builder.addPropertyValue("dataScopeList", dataScopeList);
	}
}