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
public class DataFieldDefinition extends Definition {
	
	private String dataType;
	private String value;
	
	private ValidatorDefinition validator;
	private List<RendererDefinition> renderers = new ArrayList<RendererDefinition>();
	private List<HandlerDefinition> handlers = new ArrayList<HandlerDefinition>();
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ValidatorDefinition getValidator() {
		return validator;
	}
	public void setValidator(ValidatorDefinition validator) {
		this.validator = validator;
	}
	public void addRenderer(RendererDefinition renderer) {
		renderers.add(renderer);
	}
	public List<RendererDefinition> getRenderers() {
		return renderers;
	}
	public void setRenderers(List<RendererDefinition> renderers) {
		this.renderers = renderers;
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