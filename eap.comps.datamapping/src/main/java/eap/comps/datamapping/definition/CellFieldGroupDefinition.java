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
public class CellFieldGroupDefinition extends CellFieldDefinition {
	private String mappingClass;
	private String itemMappingClass;
	
	private Integer count;
	
	private List<CellFieldDefinition> cells = new ArrayList<CellFieldDefinition>();

	public String getMappingClass() {
		return mappingClass;
	}
	public void setMappingClass(String mappingClass) {
		this.mappingClass = mappingClass;
	}
	public String getItemMappingClass() {
		return itemMappingClass;
	}
	public void setItemMappingClass(String itemMappingClass) {
		this.itemMappingClass = itemMappingClass;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public void addCell(CellFieldDefinition cell) {
		this.cells.add(cell);
	}
	public List<CellFieldDefinition> getCells() {
		return cells;
	}
	public void setCells(List<CellFieldDefinition> cells) {
		this.cells = cells;
	}
}