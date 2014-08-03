package eap.comps.datamapping.renderer;

import java.util.Date;

import eap.comps.datamapping.api.IRenderer;
import eap.comps.datamapping.definition.Definition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.util.DateUtil;

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
public class DateFormatRenderer implements IRenderer {
	
	public static final String STYLE_PATTERN = "pattern";
	public static final String STYLE_OLD_PATTERN = "oldPattern";

	public Object render(Object data, Definition definition, RendererDefinition rd) {
		if (data == null) {
			return null;
		}
		
		String pattern = rd.getStyleValue(STYLE_PATTERN);
		
		if (data instanceof Date) {
			return DateUtil.format((Date) data, pattern);
		} else if (data instanceof String) {
			String oldPattern = rd.getStyleValue(STYLE_OLD_PATTERN);
			return DateUtil.format((String) data, oldPattern, pattern);
		}
		
		return data;
	}
}