package eap.comps.datamapping.definition.parser;

import eap.comps.datamapping.definition.DataFieldDefinition;
import eap.comps.datamapping.definition.RepeatDefinition;

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
public abstract class RepeatDataFieldParserDefinition<T extends DataFieldDefinition> extends ParserDefinition {
	protected RepeatDefinition<T> repeat;
	
	public RepeatDefinition<T> getRepeat() {
		return repeat;
	}
	public void setRepeat(RepeatDefinition<T> repeat) {
		this.repeat = repeat;
	}
}