package eap.comps.datamapping.definition;

import java.io.Serializable;

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
public class CellFieldDefinition extends DataFieldDefinition {
	private Integer row;
	private Integer column;

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}

	public CellIndex getCellIndex() {
		return new CellIndex(this);
	}
	
	public static class CellIndex implements Serializable {
		private Integer row;
		private Integer column;
		
		public CellIndex() {
		}
		
		public CellIndex(Integer row, Integer column) {
			this.row = row;
			this.column = column;
		}
		
		public CellIndex(CellFieldDefinition cfd) {
			this.row = cfd.getRow();
			this.column = cfd.getColumn();
		}

		public Integer getRow() {
			return row;
		}

		public void setRow(Integer row) {
			this.row = row;
		}

		public Integer getColumn() {
			return column;
		}

		public void setColumn(Integer column) {
			this.column = column;
		}
	}
}