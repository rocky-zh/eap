package eap.comps.orm.mybatis;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import eap.EapContext;
import eap.Env;
import eap.util.Paginator;
import eap.util.PagingDataList;

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
public class SqlExecutorImpl extends SqlSessionTemplate implements ISqlExecutor {
	
	public SqlExecutorImpl(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}
	
	public PagingDataList selectList(String statement, Paginator paginator) {
		return this.selectList(statement, null, paginator);
	}
	
	public PagingDataList selectList(String statement, Object parameter, Paginator paginator) {
		Env env = EapContext.getEnv();
		
		Long totalCount = (Long) selectOne(statement + env.getProperty("mybaits.paging.suffix", "_count"), parameter);
		if (totalCount == null) {
			totalCount = 0L;
		}
		paginator.setTotalCount(totalCount);
		
		List dataList = null;
		if (totalCount > 0) {
			dataList = this.selectList(statement, parameter, new RowBounds(paginator.getOffset(), paginator.getLimit()));
		} else {
			dataList = Collections.EMPTY_LIST;
		}
		
		return new PagingDataList(paginator.getTotalCount(), dataList, paginator);
	}
}