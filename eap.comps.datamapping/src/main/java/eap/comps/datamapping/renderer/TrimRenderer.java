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
public class TrimRenderer implements IRenderer {

	public Object render(Object dataObj, Definition definition, RendererDefinition rd) {
		if (dataObj == null) {
			return null;
		}
		
		return (dataObj instanceof String ? ((String) dataObj).trim() : dataObj);
	}
}