package eap.comps.orm.jpa;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import eap.EapContext;
import eap.base.BaseBO;
import eap.base.UserDetailsVO;
import eap.util.DateUtil;

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
public class BaseBOHandlerInterceptor extends EmptyInterceptor {
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof BaseBO) {
			UserDetailsVO currUserDetailsVO = EapContext.getUserDetailsVO();
			for (int i = 0; i < propertyNames.length; i++) {
				String propertyName = propertyNames[i];
				if ("createdTime".equals(propertyName)) {
					if (state[i] == null) {
						state[i] = DateUtil.currDate();
					}
				} else if ("updateTime".equals(propertyName)) {
					if (state[i] == null) {
						state[i] = DateUtil.currDate();
					}
				} else {
					if (currUserDetailsVO != null) {
						if (state[i] == null && "createdBy".equals(propertyName)) {
							state[i] = currUserDetailsVO.getUserNum();
						}
						if (state[i] == null && "deptCode".equals(propertyName)) {
							state[i] = currUserDetailsVO.getDeptCode();
						}
						if (state[i] == null && "orgCode".equals(propertyName)) {
							state[i] = currUserDetailsVO.getOrgCode();
						}
					}
				}
			}
			
			EapContext.publish("#eap.comps.orm.jpa.BaseBOHandlerInterceptor.onSave.AFTER", entity.getClass());
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		if (entity instanceof BaseBO) {
			for (int i = 0; i < propertyNames.length; i++) {
				String propertyName = propertyNames[i];
				if ("createdTime".equals(propertyName)) {
					if (currentState[i] == null) {
						currentState[i] = DateUtil.currDate();
					}
				} else if ("updateTime".equals(propertyName)) {
					if (currentState[i] == null) {
						currentState[i] = DateUtil.currDate();
					}
				}
			}
			
			EapContext.publish("#eap.comps.orm.jpa.BaseBOHandlerInterceptor.onFlushDirty.AFTER", entity.getClass());
			
			return true;
		}
		
		return false;
	}
}