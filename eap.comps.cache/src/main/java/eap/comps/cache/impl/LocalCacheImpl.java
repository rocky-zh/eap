package eap.comps.cache.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class LocalCacheImpl extends AbstractCache { // TEST
	
	private Map<String, Object> cache = Collections.synchronizedMap(new HashMap<String, Object>());
	
	
	

}