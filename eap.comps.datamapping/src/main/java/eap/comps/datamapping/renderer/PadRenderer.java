package eap.comps.datamapping.renderer;

import org.apache.commons.lang.StringUtils;

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
public class PadRenderer implements IRenderer {
	public static final String STYLE_LENGTH = "length";
	public static final String STYLE_ALIGN = "align";
	public static final String STYLE_ALIGN_LEFT = "left";
	public static final String STYLE_ALIGN_CENTER = "center";
	public static final String STYLE_ALIGN_RIGHT = "right";
	public static final String STYLE_PADCHAR = "padChar";
	
	public Object render(Object data, Definition definition, RendererDefinition rd) {
		String dataStr = data == null ? "" : data.toString();
		
		Integer length = rd.getStyleValue(STYLE_LENGTH, Integer.class);
		if (dataStr.length() < length) {
			String align = rd.getStyleValue(STYLE_ALIGN);
			String padChar = rd.getStyleValue(STYLE_PADCHAR);
			if (STYLE_ALIGN_LEFT.equalsIgnoreCase(align)) {
				dataStr = StringUtils.rightPad(dataStr, length, padChar);
			} else {
				dataStr = StringUtils.leftPad(dataStr, length, padChar);
			}
		}
		
		return dataStr;
	}
}