package eap.comps.cache.impl;

import java.util.Arrays;
import java.util.Map;

import net.rubyeye.xmemcached.Counter;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import eap.comps.cache.AbstractCache;

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
public class MemcacheImpl extends AbstractCache {
	
	private MemcachedClient memcachedClient;
	
	public Map<String, Object> get(String[] keys) {
		try {
			return memcachedClient.get(Arrays.asList(keys));
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public Object get(String key) {
		try {
			return memcachedClient.get(key);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public boolean set(String key, Object value) {
		try {
			return memcachedClient.set(key, Integer.MAX_VALUE, value); // TODO
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public boolean set(String key, Object value, int exp) {
		try {
			return memcachedClient.set(key, exp, value);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void setWithNoReply(String key, Object value, int exp) {
		try {
			memcachedClient.setWithNoReply(key, exp, value);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public Object getAndTouch(String key, int newExp) {
		try {
			return memcachedClient.getAndTouch(key, newExp);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public boolean add(String key, Object value, int exp) {
		try {
			return memcachedClient.add(key, exp, value);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void addWithNoReply(String key, Object value, int exp) {
		try {
			memcachedClient.addWithNoReply(key, exp, value);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public long decr(String key, long delta) {
		try {
			return memcachedClient.decr(key, delta, 0L);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public long decr(String key, long delta, long initValue) {
		try {
			return memcachedClient.decr(key, delta, initValue);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public long decr(String key, long delta, long initValue, int exp) {
		try {
			return memcachedClient.decr(key, delta, initValue, exp);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void decrWithNoReply(String key, long delta) {
		try {
			memcachedClient.decrWithNoReply(key, delta);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public boolean delete(String key) {
		try {
			return memcachedClient.delete(key);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void deleteWithNoReply(String key) {
		try {
			memcachedClient.deleteWithNoReply(key);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void flushAll() {
		try {
			memcachedClient.flushAll();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void flushAllWithNoReply() {
		try {
			memcachedClient.flushAllWithNoReply();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public long incr(String key, long delta) {
		try {
			return memcachedClient.incr(key, delta);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public long incr(String key, long delta, long initValue) {
		try {
			return memcachedClient.incr(key, delta, initValue);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public long incr(String key, long delta, long initValue, int exp) {
		try {
			return memcachedClient.incr(key, delta, initValue, memcachedClient.getOpTimeout(), exp);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public long incrWithNoReply(String key, long delta) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean replace(String key, Object value, long exp) {
		// TODO Auto-generated method stub
		return false;
	}

	public void replaceWithNoReply(String key, Object value, long exp) {
		// TODO Auto-generated method stub
		
	}

	public boolean touch(String key, long exp) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		
		System.setProperty("xmemcached.jmx.enable", "true");
		
//		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:12000 localhost:12001"),new int[]{1,3});
//		   MemcachedClient memcachedClient=builder.build();
		
		MemcachedClient client=new XMemcachedClient("127.0.0.1",11211);
		
//		client.set("a", 10000, "abc");
//		client.set(key, exp, value, timeout)
//		client.setWithNoReply(key, exp, value)
//		client.get("a");
//		client.get(key, timeout)
//		client.getAndTouch(key, newExp)
//		client.getAndTouch(key, newExp, opTimeout)
//		client.getCounter(key)
//		client.getCounter(key, initialValue)
//		client.add(key, exp, value)
//		client.add(key, exp, value, timeout)
//		client.addWithNoReply(key, exp, value)
//		client.append(key, value)
//		client.append(key, value, timeout)
//		client.appendWithNoReply(key, value)
//		client.decr(key, delta)
//		client.decr(key, delta, initValue)
//		client.decr(key, delta, initValue, timeout)
//		client.decr(key, delta, initValue, timeout, exp)
//		client.decrWithNoReply(key, delta)
//		client.delete(key)
//		client.delete(key, opTimeout)
//		client.deleteWithNoReply(key)
//		client.flushAll()
//		client.flushAll(timeout)
//		client.flushAllWithNoReply()
//		client.incr(key, delta)
//		client.incr(key, delta, initValue)
//		client.incr(key, delta, initValue, timeout)
//		client.incr(key, delta, initValue, timeout, exp)
//		client.incrWithNoReply(key, delta)
//		client.prepend(key, value)
//		client.prepend(key, value, timeout)
//		client.prependWithNoReply(key, value)
//		client.replace(key, exp, value)
//		client.replace(key, exp, value, timeout)
//		client.replaceWithNoReply(key, exp, value)
//		client.touch(key, exp)
//		client.touch(key, exp, opTimeout)
		
		Counter c = client.getCounter("bbb", 1);
		System.out.println(c.addAndGet(2));
		System.out.println(c.incrementAndGet());
//		
//		System.out.println(client.getCounter("bbb").get());
		
//		client.

//		//同步存储value到memcached，缓存超时为1小时，3600秒。
//		CodeTableVO vo = new CodeTableVO();
//		client.set("a1",3600, vo);
//		//从memcached获取key对应的value
//		Object someObject=client.get("a1");
//		System.out.println(someObject);
//
//		//从memcached获取key对应的value,操作超时2秒
//		someObject=client.get("a1",2000);
//		//更新缓存的超时时间为10秒。
////		boolean success=client.touch("a1",10);
//
//		//删除value
//		client.delete("a1");
	}
}