package eap.comps.cache.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.ShardedJedisSentinelPool;

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
import eap.util.BeanUtil;
import eap.util.DomUtil;
import eap.util.StringUtil;

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
 * sudo /usr/local/bin/redis-server /etc/redis.conf
 * cat /var/run/redis.pid 
 * </pre>
 */
public class CacheManagerBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		
		String id = element.getAttribute("id");
		String provider = element.getAttribute("provider"); // element.hasAttribute("provider") ? element.getAttribute("provider") : env.getProperty("cache.provider");
		Assert.hasText(provider, "attribute 'provider' must not be empty");
		int order = element.hasAttribute("order") ? Integer.parseInt(element.getAttribute("order")) : 0;
		
		if ("memcache".equalsIgnoreCase(provider)) {
			parseMemcache(element, parserContext, source, id, provider, order);
		} 
		else if ("redis".equalsIgnoreCase(provider)) {
			parseRedis(element, parserContext, source, id, provider, order);
		}
		else if ("local".equalsIgnoreCase(provider)) {
			
		}
		
		return null;
	}

	private void parseMemcache(Element element, ParserContext parserContext, Object source, String id, String provider, int order) {
		Env env = EapContext.getEnv();
		
		String cacheName = env.getProperty(String.format("cache.%s.cacheName", id), "default"); //element.hasAttribute("cacheName") ? element.getAttribute("cacheName") : env.getProperty(String.format("cache.%s.cacheName", provider));
		String address = env.getProperty(String.format("cache.%s.address", id)); // element.hasAttribute("address") ? element.getAttribute("address") : env.getProperty(String.format("cache.%s.address", provider));
		boolean annotationDriven = element.hasAttribute("annotationDriven") ? Boolean.parseBoolean(element.getAttribute("annotationDriven")) : true;
		
		RootBeanDefinition settingsDef = new RootBeanDefinition(Settings.class);
		String settingsId = parserContext.getReaderContext().generateBeanName(settingsDef);
		settingsDef.setSource(source);
		settingsDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		settingsDef.getPropertyValues().add("order", order);
		parserContext.getRegistry().registerBeanDefinition(settingsId, settingsDef);
		parserContext.registerComponent(new BeanComponentDefinition(settingsDef, settingsId));
		
		RootBeanDefinition cacheFactoryDef = new RootBeanDefinition(CacheFactory.class);
		String cacheFactoryId = parserContext.getReaderContext().generateBeanName(cacheFactoryDef);
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
		String ssmCacheId = parserContext.getReaderContext().generateBeanName(ssmCacheDef);
		ssmCacheDef.setSource(source);
		ssmCacheDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		ConstructorArgumentValues ssmCacheConstructorArgumentValues = new ConstructorArgumentValues();
		ssmCacheConstructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(cacheFactoryId));
		ssmCacheConstructorArgumentValues.addIndexedArgumentValue(1, env.getProperty(String.format("cache.%s.expiration", id), "300")); // 5 minutes
		ssmCacheConstructorArgumentValues.addIndexedArgumentValue(2, env.getProperty(String.format("cache.%s.allowClear", id), "false"));
		ssmCacheDef.setConstructorArgumentValues(ssmCacheConstructorArgumentValues);
		parserContext.getRegistry().registerBeanDefinition(ssmCacheId, ssmCacheDef);
		parserContext.registerComponent(new BeanComponentDefinition(ssmCacheDef, ssmCacheId));
		
		RootBeanDefinition cacheManagerDef = new RootBeanDefinition(ExtendedSSMCacheManager.class);
		cacheManagerDef.setSource(source);
		cacheManagerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		cacheManagerDef.getPropertyValues().add("caches", new RuntimeBeanReference(ssmCacheId));
		parserContext.getRegistry().registerBeanDefinition(id, cacheManagerDef);
		parserContext.registerComponent(new BeanComponentDefinition(cacheManagerDef, id));
		
		if (annotationDriven) {
			Document doc = DomUtil.newDocument();
			Element adcElement = doc.createElementNS("", "annotation-driven");
			adcElement.setAttribute("cache-manager", id);
			// cache-manager="cacheManager" key-generator=""  mode="proxy" proxy-target-class="false" order=""
			new AnnotationDrivenCacheBeanDefinitionParser().parse(adcElement, parserContext);
			
			parserContext.getReaderContext().getReader().loadBeanDefinitions("classpath:simplesm-context.xml");
			
			Element aaElement = doc.createElementNS("", "aspectj-autoproxy");
			new AspectJAutoProxyBeanDefinitionParser().parse(aaElement, parserContext);
		}
	}
	
	private void parseRedis(Element element, ParserContext parserContext, Object source, String id, String provider, int order) {
		Env env = EapContext.getEnv();
		
		String modeKey = String.format("cache.%s.mode", id);
		String mode = env.getProperty(modeKey);
		Assert.hasText(mode, "env '" + modeKey + "' must not be empty");
		
		Map<String, Object> poolConfig = env.filterForPrefix(String.format("cache.%s.poolConfig.", id));
		String poolConfigId = id + "_poolConfig";
		if (poolConfig != null && poolConfig.size() > 0) {
			RootBeanDefinition poolConfigDef = new RootBeanDefinition(GenericObjectPoolConfig.class);
			poolConfigDef.setSource(source);
			poolConfigDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			poolConfigDef.getPropertyValues().addPropertyValues(poolConfig);
			parserContext.getRegistry().registerBeanDefinition(poolConfigId, poolConfigDef);
			parserContext.registerComponent(new BeanComponentDefinition(poolConfigDef, poolConfigId));
		}
		
		if ("single".equalsIgnoreCase(mode)) {
			RootBeanDefinition jedisDef = new RootBeanDefinition(Jedis.class);
			jedisDef.setSource(source);
			jedisDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			JedisShardInfo shardInfo = new JedisShardInfo(
				env.getProperty(String.format("cache.%s.host", id)),
				env.getProperty(String.format("cache.%s.port", id), Integer.class, 6379),
				env.getProperty(String.format("cache.%s.timeout", id), Integer.class, 2000),
				env.getProperty(String.format("cache.%s.weight", id), Integer.class, 1)
			);
			shardInfo.setPassword(env.getProperty(String.format("cache.%s.password", id)));
			jedisDef.getConstructorArgumentValues().addIndexedArgumentValue(0, shardInfo);
			jedisDef.setDestroyMethodName("close");
			parserContext.getRegistry().registerBeanDefinition(id, jedisDef);
			parserContext.registerComponent(new BeanComponentDefinition(jedisDef, id));
		} 
		else if ("shared".equalsIgnoreCase(mode)) {
			RootBeanDefinition poolDef = new RootBeanDefinition(ShardedJedisPool.class);
			poolDef.setSource(source);
			poolDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			poolDef.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference(poolConfigId));
			String nodes = env.getProperty(String.format("cache.%s.nodes", id));
			Assert.hasText(nodes, "attribute 'nodes' must not be empty");
			int nodesAsInt = new Integer(nodes);
			List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>(nodesAsInt);
			for (int i = 0; i < nodesAsInt; i++) {
				JedisShardInfo shardInfo = new JedisShardInfo(
					env.getProperty(String.format("cache.%s.nodes.%d.host", id, i)),
					env.getProperty(String.format("cache.%s.nodes.%d.port", id, i), Integer.class, 6379),
					env.getProperty(String.format("cache.%s.nodes.%d.timeout", id, i), Integer.class, 2000),
					env.getProperty(String.format("cache.%s.nodes.%d.weight", id, i), Integer.class, 1)
				);
				shardInfo.setPassword(StringUtil.defaultIfBlank(env.getProperty(String.format("cache.%s.nodes.%d.password", id, i)), null));
				shardInfoList.add(shardInfo);
			}
			poolDef.getConstructorArgumentValues().addIndexedArgumentValue(1, shardInfoList);
			poolDef.setDestroyMethodName("destroy");
			parserContext.getRegistry().registerBeanDefinition(id, poolDef);
			parserContext.registerComponent(new BeanComponentDefinition(poolDef, id));
		}
		else if ("sentinel".equalsIgnoreCase(mode)) {
			RootBeanDefinition poolDef = new RootBeanDefinition(JedisSentinelPool.class);
			poolDef.setSource(source);
			poolDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			ConstructorArgumentValues poolConstructorArgumentValues = new ConstructorArgumentValues();
			poolConstructorArgumentValues.addIndexedArgumentValue(0, env.getProperty(String.format("cache.%s.masters", id)));
			poolConstructorArgumentValues.addIndexedArgumentValue(1, BeanUtil.toSet(StringUtil.split(env.getProperty(String.format("cache.%s.sentinels", id)), ",")));
			poolConstructorArgumentValues.addIndexedArgumentValue(2, new RuntimeBeanReference(poolConfigId));
			poolConstructorArgumentValues.addIndexedArgumentValue(3, env.getProperty(String.format("cache.%s.timeout", id), "2000"));
			poolConstructorArgumentValues.addIndexedArgumentValue(4, StringUtil.defaultIfBlank(env.getProperty(String.format("cache.%s.password", id)), null));
			poolConstructorArgumentValues.addIndexedArgumentValue(5, env.getProperty(String.format("cache.%s.database", id), "0"));
			poolDef.getConstructorArgumentValues().addArgumentValues(poolConstructorArgumentValues);
			poolDef.setDestroyMethodName("destroy");
			parserContext.getRegistry().registerBeanDefinition(id, poolDef);
			parserContext.registerComponent(new BeanComponentDefinition(poolDef, id));
		} 
		else if ("sharedSentinel".equalsIgnoreCase(mode)) {
			RootBeanDefinition poolDef = new RootBeanDefinition(ShardedJedisSentinelPool.class);
			poolDef.setSource(source);
			poolDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			ConstructorArgumentValues poolConstructorArgumentValues = new ConstructorArgumentValues();
			poolConstructorArgumentValues.addIndexedArgumentValue(0, BeanUtil.toList(StringUtil.split(env.getProperty(String.format("cache.%s.masters", id)), ",")));
			poolConstructorArgumentValues.addIndexedArgumentValue(1, BeanUtil.toSet(StringUtil.split(env.getProperty(String.format("cache.%s.sentinels", id)), ",")));
			poolConstructorArgumentValues.addIndexedArgumentValue(2, new RuntimeBeanReference(poolConfigId));
			poolConstructorArgumentValues.addIndexedArgumentValue(3, env.getProperty(String.format("cache.%s.timeout", id), "2000"));
			poolConstructorArgumentValues.addIndexedArgumentValue(4, StringUtil.defaultIfBlank(env.getProperty(String.format("cache.%s.password", id)), null));
			poolConstructorArgumentValues.addIndexedArgumentValue(5, env.getProperty(String.format("cache.%s.database", id), "0"));
			poolDef.getConstructorArgumentValues().addArgumentValues(poolConstructorArgumentValues);
			poolDef.setDestroyMethodName("destroy");
			parserContext.getRegistry().registerBeanDefinition(id, poolDef);
			parserContext.registerComponent(new BeanComponentDefinition(poolDef, id));
		} 
		else if ("cluster".equalsIgnoreCase(mode)) {
			throw new NotImplementedException("redis cluster mode not implemented");
		}
	}
}