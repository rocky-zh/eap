package eap.comps.datamapping.renderer;

import eap.comps.codetable.CodeTable;
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
public class CodeRenderer implements IRenderer {
	
	public static final String STYLE_MODE = "mode";
	public static final String STYLE_MODE_GET_VALUE = "getValue";
	public static final String STYLE_MODE_GET_NAME = "getName";
	public static final String STYLE_MODE_GET_NAME_BY_VALUE = "getNameByValue";
	public static final String STYLE_MODE_GET_VALUE_BY_NAME = "getValueByName";
	
	public static final String STYLE_CODE_TYPE = "codeType";

	public Object render(Object data, Definition definition, RendererDefinition rd) {
		String dataStr = (data == null ? null : data.toString());
		
		String mode = rd.getStyleValue(STYLE_MODE);
		String codeType = rd.getStyleValue(STYLE_CODE_TYPE);
		
		String result = null;
		if (STYLE_MODE_GET_VALUE.equalsIgnoreCase(mode)) {
			result = CodeTable.getValue(codeType, dataStr);
		} else if (STYLE_MODE_GET_NAME_BY_VALUE.equalsIgnoreCase(mode)) {
			result = CodeTable.getNameByValue(codeType, dataStr);
		} else if (STYLE_MODE_GET_VALUE_BY_NAME.equalsIgnoreCase(mode)) {
			result = CodeTable.getValueByName(codeType, dataStr);
		} else { // STYLE_MODE_GET_NAME
			result = CodeTable.getName(codeType, dataStr);
		}
		
		return result;
	}
}