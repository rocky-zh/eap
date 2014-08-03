package eap.util;

import java.util.Map;
import java.util.Properties;

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
public class PropertiesUtil {
	public static Properties buildProperties(String propertiesString) {
		if (StringUtils.isBlank(propertiesString)) {
			return new Properties();
		}
		
		Properties properties = new Properties();
		String[] propertyArr = propertiesString.split(";");
		for (String propertyString : propertyArr) {
			if (StringUtils.isBlank(propertyString)) continue;
			
			int idx = propertyString.indexOf("=");
			if (idx > 0) {
				properties.put(propertyString.substring(0, idx), propertyString.substring(idx + 1));
			}
		}
		
		return properties;
	}
	
	public static Properties filterForPrefix(Properties srcProps, String prefix) {
		if (srcProps == null) return null;
		if (StringUtils.isBlank(prefix)) {
			return new Properties(srcProps);
		}
		
		Properties descProps = new Properties();
		for (Object keyObj : srcProps.keySet()) {
			String key = (String) keyObj;
			int prefixLen = prefix.length();
			if (key.startsWith(prefix)) {
				descProps.put(key.substring(prefixLen), srcProps.getProperty(key));
			}
		}
		
		return descProps;
	}
	
	public static String getPropertyAndRemove(Properties props, String name) {
		return (String) props.remove(name);
	}
	
	public static Properties filterForPrefix(String propertiesString, String prefix) {
		return filterForPrefix(buildProperties(propertiesString), prefix);
	}
	
	public static String toString(Properties props) {
		StringBuilder buf = new StringBuilder();
		for (Object keyObj : props.keySet()) {
			buf.append(keyObj.toString())
					.append("=")
					.append(props.getProperty(keyObj.toString()))
					.append(";");
		}
		
		return buf.toString();
	}
	
	public static Properties from(Map<?, ?> map) {
		if (map == null) {
			return null;
		}
		
		Properties props = new Properties();
		for (Map.Entry<?,?> entry : map.entrySet()) {
			props.put(entry.getKey(), entry.getValue());
		}
		return props;
	}
}