package eap.comps.datamapping.factory.object;

import java.util.Map;

import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.parser.JsonToPojoParserDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.util.JsonUtil;
import eap.util.StringUtil;

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
public class JsonToPojoMapping extends AbstractDataMapping<String, Object> {

	@Override
	protected Object mappingImpl(String dtName, String source, Map<String, Object> params, DataMappingDefinition dmd, ParserDefinition pd) {
		if (StringUtil.isBlank(source)) {
			return null;
		}
		
		JsonToPojoParserDefinition jtppd = (JsonToPojoParserDefinition) pd;
		Class mappingClass = null;
		try {
			mappingClass = Class.forName(jtppd.getMappingClass());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		Object jsonObj = JsonUtil.parseJson(source, mappingClass);
		
		return jsonObj;
	}
}