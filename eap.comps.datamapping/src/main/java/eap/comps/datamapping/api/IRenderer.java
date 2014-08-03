package eap.comps.datamapping.api;

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
public interface IRenderer {
	public Object render(Object data, Definition definition, RendererDefinition rd);
}