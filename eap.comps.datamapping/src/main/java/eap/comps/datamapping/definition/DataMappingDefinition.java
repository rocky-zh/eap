package eap.comps.datamapping.definition;

import java.util.ArrayList;
import java.util.List;

import eap.comps.datamapping.definition.parser.ParserDefinition;

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
public class DataMappingDefinition extends Definition {
	
	private ParserDefinition parser;
	private List<HandlerDefinition> handlers = new ArrayList<HandlerDefinition>();
	
	public void setHandlers(List<HandlerDefinition> handlers) {
		this.handlers = handlers;
	}
	public ParserDefinition getParser() {
		return parser;
	}
	public void setParser(ParserDefinition parser) {
		this.parser = parser;
	}
	public void addHandler(HandlerDefinition handler) {
		handlers.add(handler);
	}
	public List<HandlerDefinition> getHandlers() {
		return handlers;
	}
}