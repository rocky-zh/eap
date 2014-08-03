package eap.util;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class JsonUtil {
	
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	private static ObjectMapper objectMapper = null;
	
	public static String toJson(Object obj) {
		if (obj == null) {
			return null;
		}
		
		try {
			return getObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static <T> T parseJson(String jsonStr, Class<T> valueType) {
		if (StringUtils.isBlank(jsonStr)) {
			return null;
		}
		
		try {
			return getObjectMapper().readValue(jsonStr, valueType);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static <T> T parseJson(String jsonStr, TypeReference<T> typeReference) {
		if (StringUtils.isBlank(jsonStr)) {
			return null;
		}
		
		try {
			return getObjectMapper().readValue(jsonStr, typeReference);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	private static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			try {
				objectMapper = new JacksonObjectMapperFactory().getObject();
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		
		return objectMapper;
	}
	
	public void setObjectMapper(ObjectMapper om) {
		objectMapper = om;
	}
	
	public static void main(String[] args) throws Exception {
		JsonUtil ju = new JsonUtil();
//		ju.setObjectMapper(new JacksonObjectMapperFactory().getObject());
		
		Object r = parseJson("{\"a\":\"1\", \"b\": 2, \"c\": \"c\"}", HashMap.class);
		System.out.println(r);
	}
}