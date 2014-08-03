package eap.comps.orm.mybatis;

import java.util.List;
import java.util.Map;

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
public interface ISqlExecutor { // extends SqlSession 
	public <T> T selectOne(String statement);
	public <T> T selectOne(String statement, Object parameter);
	public <E> List<E> selectList(String statement);
	public <E> List<E> selectList(String statement, Object parameter);
	public <K, V> Map<K, V> selectMap(String statement, String mapKey);
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);
	public int insert(String statement);
	public int insert(String statement, Object parameter);
	public int update(String statement);
	public int update(String statement, Object parameter);
	public int delete(String statement);
	public int delete(String statement, Object parameter);
	
	public PagingDataList selectList(String statement, Paginator paginator);
	public PagingDataList selectList(String statement, Object parameter, Paginator paginator);
}