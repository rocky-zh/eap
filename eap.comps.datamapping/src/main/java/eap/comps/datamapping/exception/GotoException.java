package eap.comps.datamapping.exception;

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
public class GotoException extends RuntimeException {
	
	private String gotoIndex;
	
	public GotoException() {
	}
	
	public GotoException(String gotoIndex) {
		this.gotoIndex = gotoIndex;
	}
	
	public String getGotoIndex() {
		return gotoIndex;
	}

	public void setGotoIndex(String gotoIndex) {
		this.gotoIndex = gotoIndex;
	}
}