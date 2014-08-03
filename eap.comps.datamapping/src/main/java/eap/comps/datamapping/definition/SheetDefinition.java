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
public class SheetDefinition extends Definition {
	private String mappingClass;
	
	private String sheetName;
	
	private List<DataFieldDefinition> cells = new ArrayList<DataFieldDefinition>();
	
	private ValidatorDefinition validator;
	private List<RendererDefinition> renderers = new ArrayList<RendererDefinition>();
	private List<HandlerDefinition> handlers = new ArrayList<HandlerDefinition>();
	
	public String getMappingClass() {
		return mappingClass;
	}
	public void setMappingClass(String mappingClass) {
		this.mappingClass = mappingClass;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public void addCell(DataFieldDefinition cell) {
		this.cells.add(cell);
	}
	public List<DataFieldDefinition> getCells() {
		return cells;
	}
	public void setCells(List<DataFieldDefinition> cells) {
		this.cells = cells;
	}
	public ValidatorDefinition getValidator() {
		return validator;
	}
	public void setValidator(ValidatorDefinition validator) {
		this.validator = validator;
	}
	public void addRenderer(RendererDefinition renderer) {
		this.renderers.add(renderer);
	}
	public List<RendererDefinition> getRenderers() {
		return renderers;
	}
	public void setRenderers(List<RendererDefinition> renderers) {
		this.renderers = renderers;
	}
	public void addHandler(HandlerDefinition handler) {
		this.handlers.add(handler);
	}
	public List<HandlerDefinition> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<HandlerDefinition> handlers) {
		this.handlers = handlers;
	}
}