package eap.comps.cache.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.Settings;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.CacheConfiguration;
import com.google.code.ssm.providers.xmemcached.MemcacheClientFactoryImpl;
import com.google.code.ssm.spring.ExtendedSSMCacheManager;
import com.google.code.ssm.spring.SSMCache;

import eap.EapContext;
import eap.Env;
import eap.config.AnnotationDrivenCacheBeanDefinitionParser;
import eap.config.AspectJAutoProxyBeanDefinitionParser;
import eap.util.DomUtil;

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
public class MemcacheBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		String id = element.getAttribute("id");
		String cacheManagerId = element.getAttribute("cacheManagerId");
		int order = element.hasAttribute("order") ? Integer.parseInt(element.getAttribute("order")) : 0;
		
		String cacheName = env.getProperty(String.format("cache.%s.cacheName", id), "default"); //element.hasAttribute("cacheName") ? element.getAttribute("cacheName") : env.getProperty(String.format("cache.%s.cacheName", provider));
		String address = env.getProperty(String.format("cache.%s.address", id)); // element.hasAttribute("address") ? element.getAttribute("address") : env.getProperty(String.format("cache.%s.address", provider));
		boolean annotationDriven = env.getProperty(String.format("cache.%s.annotationDriven", id), Boolean.class, false); // element.hasAttribute("annotationDriven") ? Boolean.parseBoolean(element.getAttribute("annotationDriven")) : true;
		
		RootBeanDefinition settingsDef = new RootBeanDefinition(Settings.class);
		String settingsId = parserContext.getReaderContext().generateBeanName(settingsDef);
		settingsDef.setSource(source);
		settingsDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		settingsDef.getPropertyValues().add("order", order);
		parserContext.getRegistry().registerBeanDefinition(settingsId, settingsDef);
		parserContext.registerComponent(new BeanComponentDefinition(settingsDef, settingsId));
		
		RootBeanDefinition cacheFactoryDef = new RootBeanDefinition(CacheFactory.class);
		String cacheFactoryId = id;
		cacheFactoryDef.setSource(source);
		cacheFactoryDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		cacheFactoryDef.getPropertyValues().add("cacheName", cacheName);
		cacheFactoryDef.getPropertyValues().add("cacheClientFactory", new MemcacheClientFactoryImpl());
		cacheFactoryDef.getPropertyValues().add("addressProvider", new DefaultAddressProvider(address));
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.setConsistentHashing(env.getProperty(String.format("cache.%s.consistentHashing", id), Boolean.class, true));
		cacheConfiguration.setOperationTimeout(env.getProperty(String.format("cache.%s.operationTimeout", id), Integer.class, null));
		cacheConfiguration.setUseBinaryProtocol(env.getProperty(String.format("cache.%s.useBinaryProtocol", id), Boolean.class, false));
		cacheFactoryDef.getPropertyValues().add("configuration", cacheConfiguration);
		parserContext.getRegistry().registerBeanDefinition(cacheFactoryId, cacheFactoryDef);
		parserContext.registerComponent(new BeanComponentDefinition(cacheFactoryDef, cacheFactoryId));
		
		RootBeanDefinition ssmCacheDef = new RootBeanDefinition(SSMCache.class);
		String ssmCacheId = id + "_ssmCache";
		ssmCacheDef.setSource(source);
		ssmCacheDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		ConstructorArgumentValues ssmCacheConstructorArgumentValues = new ConstructorArgumentValues();
		ssmCacheConstructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(cacheFactoryId));
		ssmCacheConstructorArgumentValues.addIndexedArgumentValue(1, env.getProperty(String.format("cache.%s.expiration", id), "300")); // 5 minutes
		ssmCacheConstructorArgumentValues.addIndexedArgumentValue(2, env.getProperty(String.format("cache.%s.allowClear", id), "false"));
		ssmCacheDef.setConstructorArgumentValues(ssmCacheConstructorArgumentValues);
		parserContext.getRegistry().registerBeanDefinition(ssmCacheId, ssmCacheDef);
		parserContext.registerComponent(new BeanComponentDefinition(ssmCacheDef, ssmCacheId));
		
		if (cacheManagerId != null && cacheManagerId.length() > 0) {
			RootBeanDefinition cacheManagerDef = new RootBeanDefinition(ExtendedSSMCacheManager.class);
			cacheManagerDef.setSource(source);
			cacheManagerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			cacheManagerDef.getPropertyValues().add("caches", new RuntimeBeanReference(ssmCacheId));
			parserContext.getRegistry().registerBeanDefinition(cacheManagerId, cacheManagerDef);
			parserContext.registerComponent(new BeanComponentDefinition(cacheManagerDef, cacheManagerId));
			
			if (annotationDriven) {
				Document doc = DomUtil.newDocument();
				Element adcElement = doc.createElementNS("", "annotation-driven");
				adcElement.setAttribute("cache-manager", cacheManagerId);
				// cache-manager="cacheManager" key-generator=""  mode="proxy" proxy-target-class="false" order=""
				new AnnotationDrivenCacheBeanDefinitionParser().parse(adcElement, parserContext);
				
				parserContext.getReaderContext().getReader().loadBeanDefinitions("classpath:simplesm-context.xml");
				
				Element aaElement = doc.createElementNS("", "aspectj-autoproxy");
				new AspectJAutoProxyBeanDefinitionParser().parse(aaElement, parserContext);
			}
		}
		
		return null;
	}
}