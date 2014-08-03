package eap.util.objectfactory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
public class ObjectFactory {
	
	private static String defaultFactory;
	
	private static Map<String, IObjectFactory> objectFactorys;
	
	public static Object getObject(String objectName) {
		return getObject(objectName, Object.class);
	}

	public static <T> T getObject(String objectName, Class<T> requireType) {
		if (objectFactorys == null) {
			throw new IllegalStateException("objectFactorys not initialized");
		}
		
		objectName = objectName.trim();
		String factoryType = null;
		int idx = objectName.indexOf(" ");
		if (idx > 0) {
			factoryType = objectName.substring(0, idx);
			idx += 1; // offset 1
		} 
		else if (StringUtils.isNotBlank(defaultFactory)) {
			idx = 0;
			factoryType = defaultFactory;
		}
		
		return objectFactorys.get(factoryType).getObject(objectName.substring(idx), requireType);
	}
	
	public void setDefaultFactory(String df) {
		defaultFactory = df;
	}
	
	public void setObjectFactorys(Map<String, IObjectFactory> ofs) {
		objectFactorys = ofs;
	}
}