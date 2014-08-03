package eap.comps.codemapping;

import java.io.Serializable;

/**
 * <p> Title: 码表映射VO</p>
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
public class CodeMappingVO implements Serializable {
	/** 映射类型 */
	private String mappingType;
	/** 原键 */
	private String srcKey;
	/** 原值 */
	private String srcValue;
	/** 目标键 */
	private String descKey;
	/** 目标值 */
	private String descValue;

	public String getMappingType() {
		return this.mappingType;
	}
	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}
	public String getSrcKey() {
		return this.srcKey;
	}
	public void setSrcKey(String srcKey) {
		this.srcKey = srcKey;
	}
	public String getSrcValue() {
		return this.srcValue;
	}
	public void setSrcValue(String srcValue) {
		this.srcValue = srcValue;
	}
	public String getDescKey() {
		return this.descKey;
	}
	public void setDescKey(String descKey) {
		this.descKey = descKey;
	}
	public String getDescValue() {
		return this.descValue;
	}
	public void setDescValue(String descValue) {
		this.descValue = descValue;
	}
}