package eap.comps.codetable;

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
public class CodeTableVO implements Serializable {
	
	/** 码表类型 */
	private String codeType;
	
	/** 码表键 */
	private String codeKey;
	
	/** 码表值 */
	private String codeValue;
	
	/** 码表名称 */
	private String codeName;
	
//	private String codeDesc;
	
	/** 码表组别; 多个组别以逗号分割 */
	private String codeGroup;
	
//	private Integer ordinal;
//	private String canModifyInd;
//	private String status;
	
	public CodeTableVO() {
	}
	
	public CodeTableVO(String codeType, String codeValue, String codeName) {
		this.codeType = codeType;
		this.codeValue = codeValue;
		this.codeName = codeName;
	}

	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
	public String getCodeKey() {
		return codeKey;
	}
	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getCodeGroup() {
		return codeGroup;
	}
	public void setCodeGroup(String codeGroup) {
		this.codeGroup = codeGroup;
	}
}