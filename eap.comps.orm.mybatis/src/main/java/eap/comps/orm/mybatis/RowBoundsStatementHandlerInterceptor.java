package eap.comps.orm.mybatis;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;

import eap.util.ReflectUtil;

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
public class RowBoundsStatementHandlerInterceptor implements Interceptor {
	
	public Object intercept(Invocation invocation) throws Throwable {
		RoutingStatementHandler statement = (RoutingStatementHandler) invocation.getTarget();
		PreparedStatementHandler handler = (PreparedStatementHandler) ReflectUtil.getFieldValue(statement, "delegate");
		RowBounds rowBounds = (RowBounds) ReflectUtil.getFieldValue(handler, "rowBounds");
		
		if (rowBounds.getLimit() > 0 && rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT) {
			BoundSql boundSql = statement.getBoundSql();
			String sql = boundSql.getSql();

			String dbType = ((Connection)invocation.getArgs()[0]).getMetaData().getDatabaseProductName().toLowerCase().indexOf("mysql") != -1 ? "mysql" : "oracle";
			sql = getLimitString(dbType, sql, rowBounds.getOffset(), rowBounds.getLimit());
			
			ReflectUtil.setFieldValue(boundSql, "sql", sql);
		}
		
		return invocation.proceed();
	}
	
	public String getLimitString(String dbType, String sql, int offset, int limit) {
		StringBuilder pagingSelect = null;
		
		if ("mysql".equalsIgnoreCase(dbType)) {
			pagingSelect = new StringBuilder(sql.length() + 20);
			pagingSelect.append(sql);
			pagingSelect.append(String.format(" limit %d,%d", offset, limit));
		}
		else if ("oracle".equalsIgnoreCase(dbType)) {
			sql = sql.trim();
			boolean isForUpdate = false;
			if (sql.toLowerCase().endsWith(" for update")) {
				sql = sql.substring( 0, sql.length() - 11);
				isForUpdate = true;
			}
			
			pagingSelect = new StringBuilder(sql.length() + 100);
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
			pagingSelect.append(sql);
			pagingSelect.append(String.format(" ) row_ where rownum <= %d) where rownum_ > %d", offset+limit, offset));
			
			if (isForUpdate) {
				pagingSelect.append( " for update" );
			}
		}
		
		return pagingSelect.toString();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
}