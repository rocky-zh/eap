package eap.comps.datamapping.exception;

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
public class ValidateFailException extends RuntimeException {
	
	private Object data;
	private Definition definition;
	private ValidatorDefinition validatorDefinition;

	public ValidateFailException(Object data, Definition definition, ValidatorDefinition validatorDefinition) {
		super(validatorDefinition.getMsg());
		this.data = data;
		this.definition = definition;
		this.validatorDefinition = validatorDefinition;
	}

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Definition getDefinition() {
		return definition;
	}
	public void setDefinition(Definition definition) {
		this.definition = definition;
	}
	public ValidatorDefinition getValidatorDefinition() {
		return validatorDefinition;
	}
	public void setValidatorDefinition(ValidatorDefinition validatorDefinition) {
		this.validatorDefinition = validatorDefinition;
	}
}