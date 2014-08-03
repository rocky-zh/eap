package eap.comps.orm.jpa;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import eap.EapContext;
import eap.base.BaseBO;

@MappedSuperclass
//@EntityListeners({BaseBOHandlerInterceptor.class})
//@GenericGenerators({})
public class JpaBaseBO extends BaseBO {
	
	@PrePersist
	public void prePersist() {
		if (id == null) {
			id = EapContext.getEnv().nextId();
		}
	}
	
	@Id
	@Column(name="id")
	public Long getId() {
		return super.getId();
	}
//	@Column(name="created_time")
//	public Date getCreatedTime() {
//		return super.getCreatedTime();
//	}
//	@Column(name="created_by")
//	public String getCreatedBy() {
//		return super.getCreatedBy();
//	}
//	@Column(name="update_time")
//	public Date getUpdateTime() {
//		return super.getUpdateTime();
//	}
//	@Column(name="update_by")
//	public String getUpdateBy() {
//		return super.getUpdateBy();
//	}
//	@Column(name="dept_code")
//	public String getDeptCode() {
//		return super.getDeptCode();
//	}
//	@Column(name="org_code")
//	public String getOrgCode() {
//		return super.getOrgCode();
//	}
//	@Column(name="data_status")
//	public String getDataStatus() {
//		return super.getDataStatus();
//	}
}
