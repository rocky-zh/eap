package eap.comps.datamapping.definition.parser;

import java.util.ArrayList;
import java.util.List;

import eap.comps.datamapping.definition.SheetDefinition;

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
public class PojoToExcelParserDefinition extends ParserDefinition implements ParserVersion {

	public static final String PARSER_TYPE = "PojoToExcel";
	
	private String version = VERSION_EXCEL_2003;
	
	private List<SheetDefinition> sheets = new ArrayList<SheetDefinition>();
	
	public String getParserType() {
		return PARSER_TYPE;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void addSheet(SheetDefinition sheet) {
		this.sheets.add(sheet);
	}
	public List<SheetDefinition> getSheets() {
		return sheets;
	}
	public void setSheets(List<SheetDefinition> sheets) {
		this.sheets = sheets;
	}
}
