package eap.comps.token;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
public class TokenStore {
	
	private Map<String, Token> tokenMap = new ConcurrentHashMap<String, Token>();
	
	private int capacity = 10;
	private Queue<String> validTokenQueue = null;
	
	public TokenStore(int capacity) {
		this.capacity = capacity;
		this.validTokenQueue = new ConcurrentLinkedQueue<String>();
	}
	
	public Token addToken(Token token) {
		if (validTokenQueue.size() >= capacity) {
			for (int i = (capacity - 1); i < validTokenQueue.size(); i++) {
				this.removeToken(validTokenQueue.remove());
			}
		}
		validTokenQueue.add(token.getId());
		
		return tokenMap.put(token.getId(), token);
	}
	
	public Token removeToken(String tokenId) {
		return tokenMap.remove(tokenId);
	}
	
	public boolean hasToken(String tokenId) {
		return tokenMap.containsKey(tokenId);
	}
	
	public Collection<Token> tokens() {
		return tokenMap.values();
	}
	
	public int size() {
		return tokenMap.size();
	}
}