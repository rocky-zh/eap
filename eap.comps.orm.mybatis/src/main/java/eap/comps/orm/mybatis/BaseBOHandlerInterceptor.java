package eap.comps.orm.mybatis;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import eap.EapContext;
import eap.Env;
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
@Intercepts({@Signature(type=Executor.class, method="update", args={MappedStatement.class, Object.class})})
public class BaseBOHandlerInterceptor implements Interceptor {
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement) args[0];
		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		
		if (args[1] instanceof BaseBO) {
			BaseBO baseBO = (BaseBO) args[1];
			UserDetailsVO currUserDetailsVO = EapContext.getUserDetailsVO();
			
			if (SqlCommandType.INSERT.equals(sqlCommandType)) {
				Env env = EapContext.getEnv();
				if ("auto".equalsIgnoreCase(env.getProperty("mybatis.idGen", "auto"))) {
					baseBO.setId(EapContext.getEnv().nextId());
				}
				if (currUserDetailsVO != null) {
					if (baseBO.getCreatedBy() == null) {
						baseBO.setCreatedBy(currUserDetailsVO.getUserNum());
					}
					if (baseBO.getDeptCode() == null) {
						baseBO.setDeptCode(currUserDetailsVO.getDeptCode());
					}
					if (baseBO.getOrgCode() == null) {
						baseBO.setOrgCode(currUserDetailsVO.getOrgCode());
					}
				}
				if (baseBO.getCreatedTime() == null) {
					baseBO.setCreatedTime(DateUtil.currDate());
				}
				if (baseBO.getUpdateTime() == null) {
					baseBO.setUpdateTime(DateUtil.currDate());
				}
			} 
			else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
				if (currUserDetailsVO != null) {
					if (baseBO.getUpdateBy() == null) {
						baseBO.setUpdateBy(currUserDetailsVO.getUserNum());
					}
				}
				if (baseBO.getUpdateTime() == null) {
					baseBO.setUpdateTime(DateUtil.currDate());
				}
			}
		}
		
		Object result = invocation.proceed();
		
		EapContext.publish("#eap.comps.orm.mybatis.BaseBOHandlerInterceptor.intercept.AFTER", mappedStatement.getId());
		
		return result;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}

}
