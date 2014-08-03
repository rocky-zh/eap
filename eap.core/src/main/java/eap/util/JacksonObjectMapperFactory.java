package eap.util;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.FactoryBean;

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
public class JacksonObjectMapperFactory implements FactoryBean<ObjectMapper> {
	
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	
	@Override
	public ObjectMapper getObject() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false); 
		
		SerializationConfig sc = objectMapper.getSerializationConfig();
		sc.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		sc.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
		
		SimpleDateFormat fullDateSdf = new SimpleDateFormat(dateFormat);
		sc.withDateFormat(fullDateSdf);
		sc.setDateFormat(fullDateSdf);
		
		DeserializationConfig desc = objectMapper.getDeserializationConfig();
		desc.withDateFormat(fullDateSdf);
		desc.setDateFormat(fullDateSdf);
		desc.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		return objectMapper;
	}
	
	@Override
	public Class<?> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
}