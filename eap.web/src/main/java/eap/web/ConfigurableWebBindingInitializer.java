package eap.web;

import java.beans.PropertyEditor;
import java.util.Map;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

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
public class ConfigurableWebBindingInitializer extends org.springframework.web.bind.support.ConfigurableWebBindingInitializer {
	
	private Map<Class<?>, PropertyEditor> propertyEditors;
	
	@Override
	public void initBinder(WebDataBinder binder, WebRequest request) {
		super.initBinder(binder, request);
		
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		
		if (propertyEditors != null && propertyEditors.size() > 0) {
			for (Map.Entry<Class<?>, PropertyEditor> peEntry : propertyEditors.entrySet()) {
				binder.registerCustomEditor(peEntry.getKey(), peEntry.getValue());
			}
		}
	}
	
	public void setPropertyEditors(Map<Class<?>, PropertyEditor> propertyEditors) {
		this.propertyEditors = propertyEditors;
	}
}