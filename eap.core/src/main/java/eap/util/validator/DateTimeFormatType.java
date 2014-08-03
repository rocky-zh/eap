package eap.util.validator;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 
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
public enum DateTimeFormatType {
	
	FULL("yyyy-MM-dd HH:mm:ss"),
	DATE("yyyy-MM-dd"),
	TIME("HH:mm:ss");
	
	private String pattern;
	
	private DateTimeFormatType(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}