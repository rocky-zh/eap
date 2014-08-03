package eap.comps.datamapping.definition.parser;

import eap.comps.datamapping.definition.TemplateDefinition;

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
public class XmlToPojoParserDefinition extends ParserDefinition {
	public static final String PARSER_TYPE = "XmlToPojo";
	
	private TemplateDefinition template;
	
	public String getParserType() {
		return PARSER_TYPE;
	}
	
	public TemplateDefinition getTemplate() {
		return template;
	}

	public void setTemplate(TemplateDefinition template) {
		this.template = template;
	}
}