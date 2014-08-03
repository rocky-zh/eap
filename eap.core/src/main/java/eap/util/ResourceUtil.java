package eap.util;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
public class ResourceUtil {
	
	private static PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
	
	public static Resource getResource(String locationPattern) { // TODO JBOSS VFS
		try {
			Resource[] resources = resourceLoader.getResources(locationPattern);
			return (resources != null && resources.length > 0) ? resources[0] : null;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static Resource[] getResources(String locationPattern) {
		try {
			return resourceLoader.getResources(locationPattern);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}