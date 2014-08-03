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
public class CellFieldSetDefinition extends DataFieldDefinition implements DataFieldSetDefinition {
	
	private String ref;
	
	private List<DataFieldDefinition> cells = new ArrayList<DataFieldDefinition>();
	
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
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
	public List<DataFieldDefinition> getItems() {
		return cells;
	}
}