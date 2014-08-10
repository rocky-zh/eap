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
import org.w3c.dom.Element;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.ShardedJedisSentinelPool;
import eap.EapContext;
import eap.Env;
import eap.util.BeanUtil;
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
public class RedisBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		String id = element.getAttribute("id");
		int order = element.hasAttribute("order") ? Integer.parseInt(element.getAttribute("order")) : 0;
		
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
			shardInfo.setPassword(StringUtil.defaultIfBlank(env.getProperty(String.format("cache.%s.password", id)), null));
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
					env.getProperty(String.format("cache.%s.nodes.%d.timeout", id, i), Integer.class, 
						env.getProperty(String.format("cache.%s.timeout", id), Integer.class, 2000)
					),
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
		
		return null;
	}
}