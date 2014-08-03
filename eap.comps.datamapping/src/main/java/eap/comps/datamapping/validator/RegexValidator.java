package eap.comps.datamapping.validator;

import eap.comps.datamapping.api.IValidator;
import eap.comps.datamapping.definition.Definition;
import eap.comps.datamapping.definition.ValidatorDefinition;

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
public class RegexValidator implements IValidator {

	public boolean validate(Object data, Definition definition, ValidatorDefinition vd) {
		return true;
	}
}