package eap.comps.datamapping.definition.parser;

import eap.comps.datamapping.definition.DataFieldDefinition;

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
public class TextToPojoParserDefinition extends RepeatDataFieldParserDefinition<DataFieldDefinition> {
	
	public static final String PARSER_TYPE = "TextToPojo";
	
	public static final String LENGTH_BY_CHAR = "char";
	public static final String LENGTH_BY_BYTE = "byte";
	
	private String lengthBy = LENGTH_BY_CHAR;
	private String lengthByByteCharset; // = System.getProperty("file.encoding");

	public String getParserType() {
		return PARSER_TYPE;
	}

	public String getLengthBy() {
		return lengthBy;
	}

	public void setLengthBy(String lengthBy) {
		this.lengthBy = lengthBy;
	}

	public String getLengthByByteCharset() {
		return lengthByByteCharset;
	}

	public void setLengthByByteCharset(String lengthByByteCharset) {
		this.lengthByByteCharset = lengthByByteCharset;
	}
}