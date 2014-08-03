package eap.comps.datamapping.factory.object;

import java.util.Map;

import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.definition.parser.PojoToJsonParserDefinition;
import eap.util.JsonUtil;

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
public class PojoToJsonMapping extends AbstractDataMapping<Object, String> {

	@Override
	protected String mappingImpl(String dtName, Object source, Map<String, Object> params, DataMappingDefinition dmd, ParserDefinition pd) {
		PojoToJsonParserDefinition ptjpd = (PojoToJsonParserDefinition) pd; 
		
		String json = JsonUtil.toJson(source);
		
		return json;
	}
}