package eap.comps.datamapping.definition.parser;

import java.util.ArrayList;
import java.util.List;

import eap.comps.datamapping.definition.HandlerDefinition;
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
public class JsonToPojoParserDefinition extends ParserDefinition {
	
	public static final String PARSER_TYPE = "JsonToPojo";
	public String getParserType() {
		return PARSER_TYPE;
	}
	
	private String mappingClass;
	
	private List<RendererDefinition> renderers = new ArrayList<RendererDefinition>();
	private List<HandlerDefinition> handlers = new ArrayList<HandlerDefinition>();
	
	public String getMappingClass() {
		return mappingClass;
	}
	public void setMappingClass(String mappingClass) {
		this.mappingClass = mappingClass;
	}
	public List<RendererDefinition> getRenderers() {
		return renderers;
	}
	public void setRenderers(List<RendererDefinition> renderers) {
		this.renderers = renderers;
	}
	public List<HandlerDefinition> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<HandlerDefinition> handlers) {
		this.handlers = handlers;
	}
}