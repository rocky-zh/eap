package eap.comps.datamapping.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eap.comps.codetable.CodeTable;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

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
public class CodeTableFtlDirective implements TemplateDirectiveModel {
	
	public static final String NODE_ATTR_MODE = "mode";
	public static final String NODE_ATTR_MODE_GET_VALUE = "getValue";
	public static final String NODE_ATTR_MODE_GET_NAME = "getName";
	public static final String NODE_ATTR_MODE_GET_NAME_BY_VALUE = "getNameByValue";
	public static final String NODE_ATTR_MODE_GET_NAME_EN_BY_VALUE = "getNameEnByValue";
	public static final String NODE_ATTR_MODE_GET_VALUE_BY_NAME = "getValueByName";
	
	public static final String NODE_ATTR_CODE_TYPE = "codeType";
	
	public static final String NODE_ATTR_DATA = "data";

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) 
		throws TemplateException, IOException 
	{
		TemplateModel modeTM = (TemplateModel) params.get(NODE_ATTR_MODE);
		String mode = modeTM != null ? modeTM.toString() : null;
		TemplateModel codeTypeTM = (TemplateModel) params.get(NODE_ATTR_CODE_TYPE);
		String codeType = codeTypeTM != null ? codeTypeTM.toString() : null;
		TemplateModel dataTM = (TemplateModel) params.get(NODE_ATTR_DATA);
		String data = dataTM != null ? dataTM.toString() : null;
		
		String result = null;
		if (StringUtils.isNotEmpty(data)) {
			if (NODE_ATTR_MODE_GET_VALUE.equalsIgnoreCase(mode)) {
				result = CodeTable.getValue(codeType, data);
			} else if(NODE_ATTR_MODE_GET_NAME.equalsIgnoreCase(mode)) { 
				result = CodeTable.getName(codeType, data);
			} else if (NODE_ATTR_MODE_GET_VALUE_BY_NAME.equalsIgnoreCase(mode)) {
				result = CodeTable.getValueByName(codeType, data);
			} else { // NODE_ATTR_MODE_GET_NAME_BY_VALUE
				result = CodeTable.getNameByValue(codeType, data);
			}
		} else {
			result = "";
		}
		
		Writer out = env.getOut();
		
		out.write(result);
	}
}