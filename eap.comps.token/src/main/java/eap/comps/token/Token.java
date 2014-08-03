package eap.comps.token;

import java.util.Date;

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
public class Token {
	
	private String id;
	
	private Date createdTime;
	
	public Token() {
	}
	
	public Token(String id) {
		this.id = id;
		this.createdTime = new Date();
	}

	public Token(String id, Date createdTime) {
		this.id = id;
		this.createdTime = createdTime;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
}