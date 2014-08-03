package eap.comps.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;

import com.google.code.ssm.api.CacheName;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

public class ClazzA {
	
	private Cache cache;
	
//	@Cacheable(value="default", key="'M1' + #key")
	@ReadThroughSingleCache(namespace="ns1", expiration = 600)
//	@CacheName("default")
	public String m1(@ParameterValueKeyProvider String key) {
		System.out.println("======= 1 " + key);
		return "key 1 " + System.currentTimeMillis();
	}
	
	@Cacheable(value="default", key="'M2' + #key")
	public String m2(String key) {
		System.out.println("======= 2 " + key);
		return "key 2 " + System.currentTimeMillis();
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
