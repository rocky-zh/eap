package eap.comps.token;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.RandomStringUtils;

import eap.EapContext;
import eap.Env;
import eap.comps.datastore.IDataScope;

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
public class TokenManager {
	
	public static String applyToken(String tokenStoreId, IDataScope dataScope) { // datastore
		Env env = EapContext.getEnv();
		int tokenMaxCount = env.getProperty("token.maxCount", Integer.class, 10);
		int tokenLength = env.getProperty("token.tokenLength", Integer.class, 8);
		
		TokenStore tokenStore = null;
		if (dataScope.getLock() != null) {
			synchronized (dataScope.getLock()) {
				tokenStore = (TokenStore) dataScope.get(tokenStoreId);
				if (tokenStore == null) {
					tokenStore = new TokenStore(tokenMaxCount);
					dataScope.set(tokenStoreId, tokenStore);
				}
			}
		} else {
			tokenStore = (TokenStore) dataScope.get(tokenStoreId);
			if (tokenStore == null) {
				tokenStore = new TokenStore(tokenMaxCount);
				dataScope.set(tokenStoreId, tokenStore);
			}
		}
		
		String tokenId = RandomStringUtils.randomAlphanumeric(tokenLength);
		tokenStore.addToken(new Token(tokenId));
		
		return tokenId;
	}
	
	public static boolean destoryToken(String tokenStoreId, String tokenId, IDataScope dataScope) {
		TokenStore tokenStore = null;
		if (dataScope.getLock() != null) {
			synchronized (dataScope.getLock()) {
				tokenStore = (TokenStore) dataScope.get(tokenStoreId);
			}
		} else {
			tokenStore = (TokenStore) dataScope.get(tokenStoreId);
		}
		
		if (tokenStore == null) {
			return false;
		}
		
		Token removedToken = tokenStore.removeToken(tokenId);
		if (removedToken != null) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isValidToken(String tokenStoreId, String token, IDataScope dataScope) {
		TokenStore tokenStore = null;
		if (dataScope.getLock() != null) {
			synchronized (dataScope.getLock()) {
				tokenStore = (TokenStore) dataScope.get(tokenStoreId);
			}
		} else {
			tokenStore = (TokenStore) dataScope.get(tokenStoreId);
		}
		
		if (tokenStore == null) {
			return false;
		}
		
		return tokenStore.hasToken(token);
	}
	
	public static String applyToken0(Map session) {
		int tokenMaxCount = 10;
		int tokenLength = 8;
		
		TokenStore tokenStore = null;
		synchronized (session) {
			tokenStore = (TokenStore) session.get("a");
			if (tokenStore == null) {
				tokenStore = new TokenStore(tokenMaxCount);
				session.put("a", tokenStore);
			}
		}
		
		String tokenId = RandomStringUtils.randomAlphanumeric(tokenLength);
		tokenStore.addToken(new Token(tokenId));
		
		System.out.println(Thread.currentThread().getName() + ":" + tokenStore.tokens().size());
		
		return tokenId;
	}
	
	public static void main(String[] args) {
		final AtomicInteger counter = new AtomicInteger();
		
//		final StopWatch sw = new StopWatch();
//		sw.start();
		
		for (int j = 0; j < 100; j++) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
//				TokenManager tm = new TokenManager();
				final Map m = new ConcurrentHashMap();
				for (int i = 0; i < 12; i++) {
					
					String token = applyToken0(m);
					System.out.println(token);
				}
//				System.out.println(Thread.currentThread().getName() + ":" + ((TokenStore)m.get("__token_store")).size());
				
//				if (counter.incrementAndGet() == 100) {
//					sw.stop();
//					System.out.println(sw.prettyPrint());
//					System.out.println(((TokenStore)m.get("__token_store")).size());
//				}
			}
		});
		t.setDaemon(false);
		t.start();
		}
	}
}
