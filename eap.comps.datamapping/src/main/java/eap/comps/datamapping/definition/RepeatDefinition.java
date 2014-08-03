package eap.comps.datamapping.definition;

import java.util.ArrayList;
import java.util.List;

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
public class RepeatDefinition<T> extends Definition {
	
	private String newLineChar = System.getProperty("line.separator");
	private String mappingClass;
	private boolean singleResult = false;
	
	protected List<T> fields = new ArrayList<T>();
	private ValidatorDefinition validator;
	protected List<HandlerDefinition> handlers = new ArrayList<HandlerDefinition>();
	
	public String getNewLineChar() {
		return newLineChar;
	}
	public void setNewLineChar(String newLineChar) {
		this.newLineChar = newLineChar;
	}
	public String getMappingClass() {
		if (mappingClass.startsWith("[L") && mappingClass.indexOf(":") == -1) {
			mappingClass += (":" + fields.size());
		}
		
		return mappingClass;
	}
	public void setMappingClass(String mappingClass) {
		this.mappingClass = mappingClass;
	}
	public boolean isSingleResult() {
		return singleResult;
	}
	public void setSingleResult(boolean singleResult) {
		this.singleResult = singleResult;
	}
	public void addField(T field) {
		fields.add(field);
	}
	public List<T> getFields() {
		return fields;
	}
	public void setFields(List<T> fields) {
		this.fields = fields;
	}
	public ValidatorDefinition getValidator() {
		return validator;
	}
	public void setValidator(ValidatorDefinition validator) {
		this.validator = validator;
	}
	public void addHandler(HandlerDefinition handler) {
		handlers.add(handler);
	}
	public List<HandlerDefinition> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<HandlerDefinition> handlers) {
		this.handlers = handlers;
	}
}