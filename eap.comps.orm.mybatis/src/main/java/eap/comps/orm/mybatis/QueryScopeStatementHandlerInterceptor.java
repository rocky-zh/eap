package eap.comps.orm.mybatis;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.jdbc.core.JdbcTemplate;

import eap.EapContext;
import eap.Env;
import eap.base.UserDetailsVO;
import eap.util.ReflectUtil;
import eap.util.StringUtil;

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
@Intercepts({@Signature(type=StatementHandler.class, method="prepare", args={Connection.class})})
public class QueryScopeStatementHandlerInterceptor implements Interceptor {
	
	public static final String PLACEHOLDER_QUERY_SCOPE = "@QueryScope@";
	
	private Env env = EapContext.getEnv();
	
	private JdbcTemplate jdbcTemplate;
	
	public Object intercept(Invocation invocation) throws Throwable {
		RoutingStatementHandler statement = (RoutingStatementHandler) (Proxy.isProxyClass(invocation.getTarget().getClass()) 
				? ReflectUtil.getFieldValue(ReflectUtil.getFieldValue(invocation.getTarget(), "h"), "target") 
					: invocation.getTarget());
		PreparedStatementHandler handler = (PreparedStatementHandler) ReflectUtil.getFieldValue(statement, "delegate");
		MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(handler, "mappedStatement");
		
		String statementId = mappedStatement.getId();
		UserDetailsVO currUserDetailsVO = EapContext.getUserDetailsVO();
		if (currUserDetailsVO != null) {
			BoundSql boundSql = statement.getBoundSql();
			String sql = boundSql.getSql();
			
			String qsPlaceholder = env.getProperty("mybaits.queryScope.placeholder", PLACEHOLDER_QUERY_SCOPE);
			if (sql.indexOf(qsPlaceholder) > 0) {
				List<String> roleCdList = currUserDetailsVO.getRoleCdList();
				String queryScope = this.findMaxQueryScope(roleCdList, statementId.endsWith(env.getProperty("mybaits.paging.suffix")) ? statementId.substring(0, statementId.length() - env.getProperty("mybaits.paging.suffix").length()) : statementId);
				String filterWhere = null;
				if (StringUtil.isNotBlank(queryScope)) {
					if ("00".equals(queryScope)) {
						filterWhere = this.filter00(currUserDetailsVO);
					}
					else if ("10".equals(queryScope)) {
						filterWhere = this.filter10(currUserDetailsVO);
					}
					else if ("20".equals(queryScope)) {
						filterWhere = this.filter20(currUserDetailsVO);
					}
					else if ("21".equals(queryScope)) {
						filterWhere = this.filter21(currUserDetailsVO);
					}
					else if ("30".equals(queryScope)) {
						filterWhere = this.filter30(currUserDetailsVO);
					}
					else if ("31".equals(queryScope)) {
						filterWhere = this.filter31(currUserDetailsVO);
					}
				}
				if (StringUtil.isBlank(filterWhere)) {
					filterWhere = this.reject(currUserDetailsVO);
				}
				sql = StringUtil.replaceOnce(sql, qsPlaceholder, filterWhere);
				
				ReflectUtil.setFieldValue(boundSql, "sql", sql);
			}
		}
		
		return invocation.proceed();
	}
	private String findMaxQueryScope(List<String> roleCdList , String statementId) { //  TODO IN CACHE
		StringBuilder sql = new StringBuilder();
		sql.append(env.getProperty("mybaits.queryScope.byRolesQuery.prefix"));
		
		Object[] params = new Object[roleCdList.size() + 1];
		for (int i = 0; i < roleCdList.size(); i++) {
			sql.append("?,");
			params[i] = roleCdList.get(i);
		}
		sql.deleteCharAt(sql.length() - 1);
		
		sql.append(env.getProperty("mybaits.queryScope.byRolesQuery.subfix"));
		params[params.length - 1] = statementId;
		
		List<String> scopeList = jdbcTemplate.queryForList(sql.toString(), params, String.class);
		Collections.sort(scopeList); // lower -> upper
		
		return (scopeList != null && scopeList.size() > 0) ? scopeList.get(scopeList.size() - 1) : null;
	}
	private String filter00(UserDetailsVO currUserDetailsVO) { // 所有可见
		return "1 = 1";
	}
	private String filter10(UserDetailsVO currUserDetailsVO) { // 自己可见
		return StringUtil.isNotBlank(currUserDetailsVO.getUserNum()) 
				? (env.getProperty("mybaits.queryScope.field.createdBy", "qst.created_by") + " = '"+ currUserDetailsVO.getUserNum() +"'") 
					: null;
//		return "SELECT row_.* FROM (" + sql + ") row_ WHERE row_.createdBy = '"+ currUserDetailsVO.getUserNum() +"'";
	}
	private String filter20(UserDetailsVO currUserDetailsVO) { // 本部门可见
		return StringUtil.isNotBlank(currUserDetailsVO.getDeptCode()) 
				? (env.getProperty("mybaits.queryScope.field.deptCode", "qst.dept_code") + " = '" + currUserDetailsVO.getDeptCode() + "'") 
					: null;
//		return "SELECT row_.* FROM (" + sql + ") row_ WHERE row_.deptCode = '" + currUserDetailsVO.getDeptCd() + "'";
	}
	private String filter21(UserDetailsVO currUserDetailsVO) { // 下级部门可见
		return StringUtil.isNotBlank(currUserDetailsVO.getDeptCode()) 
				? (env.getProperty("mybaits.queryScope.field.deptCode", "qst.dept_code") + " LIKE '"+ currUserDetailsVO.getDeptCode() +"%'") 
					: null;
//		return "SELECT row_.* FROM (" + sql + ") row_ WHERE row_.deptCode LIKE '"+ currUserDetailsVO.getDeptCd() +"%'";
	}
	private String filter30(UserDetailsVO currUserDetailsVO) { // 本机构可见
		return StringUtil.isNotBlank(currUserDetailsVO.getOrgCode()) 
				? (env.getProperty("mybaits.queryScope.field.orgCode", "qst.org_code")+ " = '" + currUserDetailsVO.getOrgCode() + "'") 
					: null;
//		return "SELECT row_.* FROM (" + sql + ") row_ WHERE row_.orgCode = '" + currUserDetailsVO.getOrgCd() + "'";
	}
	private String filter31(UserDetailsVO currUserDetailsVO) { // 下级机构可见
		return StringUtil.isNotBlank(currUserDetailsVO.getOrgCode()) 
				? (env.getProperty("mybaits.queryScope.field.orgCode", "qst.org_code")+ " LIKE '" + currUserDetailsVO.getOrgCode() + "%'")
					: null;
//		return "SELECT row_.* FROM (" + sql + ") row_ WHERE row_.orgCode LIKE '" + currUserDetailsVO.getOrgCd() + "%'";
	}
	private String reject(UserDetailsVO currUserDetailsVO) {
		return "1 = 2";
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
}