package eap.base;

import java.io.Serializable;
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
//@MappedSuperclass
public class BaseBO implements Serializable {
	
	/** 主键ID */
//	@Id
//	@Column(name="id")
	protected Long id;
	
	/** 创建时间 */
//	@Column(name="created_time")
	protected Date createdTime;
	
	/** 创建人 */
//	@Column(name="created_by")
	protected String createdBy;
	
	/** 更新时间 */
//	@Column(name="update_time")
	protected Date updateTime; // lastModifiedTime
	/** 更新人 */
//	@Column(name="update_by")
	protected String updateBy; // lastModifiedBy
	
	/** 部门代码 */
//	@Column(name="dept_code")
	protected String deptCode;
	/** 机构代码 */
//	@Column(name="org_code")
	protected String orgCode;
	
	/** 数据状态 */
//	@Column(name="data_status")
	protected String dataStatus;
	
//	@PrePersist
//	public void prePersist() {
//		if (id == null) {
//			id = EapContext.getEnv().nextId();
//		}
//	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
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
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
}