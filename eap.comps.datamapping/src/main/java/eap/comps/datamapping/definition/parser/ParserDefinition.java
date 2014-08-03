package eap.comps.datamapping.definition.parser;

import eap.comps.datamapping.definition.Definition;

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
public abstract class ParserDefinition extends Definition implements IParserDefinition {
	public static final String FAILMODE_EXCEPTION = "exception";
	public static final String FAILMODE_RECORD = "record";
	
	protected String failMode = FAILMODE_EXCEPTION;
	
	public String getFailMode() {
		return failMode;
	}
	public void setFailMode(String failMode) {
		this.failMode = failMode;
	}
}