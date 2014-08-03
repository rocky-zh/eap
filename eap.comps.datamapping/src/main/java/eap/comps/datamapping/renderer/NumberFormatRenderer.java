package eap.comps.datamapping.renderer;

import java.text.DecimalFormat;

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
public class NumberFormatRenderer implements IRenderer {
	
	public static final String STYLE_PATTERN = "pattern";

	public Object render(Object o, Definition definition, RendererDefinition rd) {
		if (o == null) {
			return null;
		}
		
		Double value = null;
		try {
			value = new Double(o.toString());
		} catch (Exception e) {
		}
		
		if (value == null) {
			return o;
		}
		
		String pattern = rd.getStyleValue(STYLE_PATTERN);
		return new DecimalFormat(pattern).format(value);
	}
}