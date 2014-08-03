package eap.base;

import java.io.Serializable;
import java.util.List;

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
public class UserDetailsVO implements Serializable {
	
	/** 用户名/登录账户 */
	private String userName;
	/** 用户编码 */
	private String userNum;
	/** 账号类型; 1-邮箱； 2-手机; 3-用户名 */
	private String accountType;
	/** 账号状态 */
	private String status;
	
	/** IP地址 */
	private String ip;
	/** 区域代码 */
	private String areaCode;
	/** 部门代码 */
	private String deptCode;
	/** 机构代码 */
	private String orgCode;
	
	/** 角色代码列表 */
	private List<String> roleCdList; // sorted
	
	public boolean isAccountLocked() {
		return "2".equals(status);
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNum() {
		return userNum;
	}
	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public List<String> getRoleCdList() {
		return roleCdList;
	}
	public void setRoleCdList(List<String> roleCdList) {
		this.roleCdList = roleCdList;
	}
}