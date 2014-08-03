package eap.comps.datamapping.renderer;

import eap.comps.datamapping.api.IRenderer;
import eap.comps.datamapping.definition.Definition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.util.ReflectUtil;
import eap.util.StringUtil;
import eap.util.objectfactory.ObjectFactory;

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
public class InvokeRenderer implements IRenderer {
	
	public static final String STYLE_CLASS_NAME = "className";
	public static final String STYLE_METHOD = "method";
	
	@Override
	public Object render(Object data, Definition definition, RendererDefinition rd) {
		if (data == null) return null;
		
		String className = rd.getStyleValue(STYLE_CLASS_NAME);
		String method = rd.getStyleValue(STYLE_METHOD);
		if (StringUtil.isNotBlank(className) && StringUtil.isNotBlank(method)) {
			Object setterObj = ObjectFactory.getObject(className);
			return ReflectUtil.invokeMethod(setterObj, method, new Object[] {data});
		} else if (StringUtil.isNotBlank(method)) {
			return ReflectUtil.invokeMethod(data, method, null);
		} else {
			return data;
		}
	}
}