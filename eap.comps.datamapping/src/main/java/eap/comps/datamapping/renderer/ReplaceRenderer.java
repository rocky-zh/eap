package eap.comps.datamapping.renderer;

import eap.comps.datamapping.api.IRenderer;
import eap.comps.datamapping.definition.Definition;
import eap.comps.datamapping.definition.RendererDefinition;

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
public class ReplaceRenderer implements IRenderer {
	
	public static final String STYLE_MODE = "mode";
	public static final String STYLE_MODE_FIRST = "first";
	public static final String STYLE_MODE_ALL = "all";
	
	public static final String STYLE_REGEX = "regex";
	public static final String STYLE_REPLACEMENT = "replacement";

	public Object render(Object dataObj, Definition definition, RendererDefinition rd) {
		if (dataObj == null) return null;
		
		String mode = rd.getStyleValue(STYLE_MODE);
		String regex = rd.getStyleValue(STYLE_REGEX);
		String replacement = rd.getStyleValue(STYLE_REPLACEMENT);
		
		String data = dataObj.toString();
		if (STYLE_MODE_FIRST.equals(mode)) {
			data = data.replaceFirst(regex, replacement);
		} else {
			data = data.replaceAll(regex, replacement);
		}
		
		return data;
	}
}