package eap.comps.orm.jpa;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import eap.util.Paginator;
import eap.util.PagingDataList;
import eap.util.SqlUtil;

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
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseDAO { // BaseDAO<T, ID extends Serializable>
	
	@PersistenceContext(unitName="eap_comps_orm_jpa_entityManager")
	protected EntityManager entityManager;
	
	@Resource(name="eap_comps_orm_jpa_entityManager_jdbcTempalte")
	protected JdbcTemplate jdbcTemplate;
	
	public PagingDataList findAll(String countSql, String recordSql, Object[] params, Paginator paginator, Class<?> resultClass) {
		Long count = jdbcTemplate.queryForLong(countSql, params);
		paginator.setTotalCount(count);
		
		String dbType = null;
		try {
			dbType = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName().toLowerCase().indexOf("mysql") != -1 ? "mysql" : "oracle";
		} catch (SQLException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		recordSql = SqlUtil.getLimitString(dbType, recordSql, paginator.getOffset(), paginator.getLimit());
		List resultList = jdbcTemplate.query(recordSql, params, new BeanPropertyRowMapper(resultClass));
		
		return new PagingDataList(resultList.size(), resultList, paginator);
	}
	
	public <R> List<R> findAll(String sql, Object[] params, Class<R> resultClass) {
		return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper(resultClass));
	}
	
	public <R> R findOne(String sql, Object[] params, Class<R> resultClass) {
		return (R) jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper(resultClass)); 
	}
	
	public int executeSql(String sql, Object[] params) {
		return jdbcTemplate.update(sql, params);
	}
	
	// -----------------------
	// Spring JPA
	// -----------------------
	
//	protected JpaRepository<T, ID> jpaRepository;
	
//	private Class entityClass;
	
//	@PostConstruct
//	public void init() {
//		entityClass =(Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//		jpaRepository = new JpaRepository<T, ID>(entityClass, entityManager);
//	}
	
//	public void setParameters(Query query, Object paramsObj, Paginator paginator) {
//		if (paramsObj != null) {
//			if (paramsObj instanceof List) {
//				List<Object> params = (List<Object>) paramsObj;
//				if (params.size() > 0) {
//					for (int i = 1; i <= params.size(); i++) {
//						query.setParameter(i, params.get(i - 1));
//					}
//				}
//			} 
//			else if (paramsObj instanceof Map) {
//				Map<String, Object> params = (Map<String, Object>) paramsObj;
//				if (params.size() > 0) {
//					for (Map.Entry<String, Object> entry : params.entrySet()) {
//						query.setParameter(entry.getKey(), entry.getValue());
//					}
//				}
//			} 
//			else if (paramsObj instanceof Object[]) {
//				Object[] params = (Object[]) paramsObj;
//				if (params.length > 0) {
//					for (int i = 1; i <= params.length; i++) {
//						query.setParameter(i, params[i - 1]);
//					}
//				}
//			}
//			
//			if (paginator != null) {
//				query.setFirstResult(paginator.getOffset());
//				query.setMaxResults(paginator.getLimit());
//			}
//		}
//		
//	}
//	public void setParameters(Query query, Object[] paramsObj) {
//		setParameters(query, paramsObj, null);
//	}
//	
//	public PagingDataList findAll(String countSql, String recordSql, Object paramsObj, Paginator paginator, Class<?> resultClass) {
//		Query countQuery = entityManager.createNativeQuery(countSql, Long.class);
//		setParameters(countQuery, paramsObj, paginator);
//		
//		paginator.setTotalCount((Long) countQuery.getSingleResult());
//		
//		Query recordQuery = entityManager.createNativeQuery(recordSql, resultClass);
//		setParameters(recordQuery, paramsObj, paginator);
//		
//		List resultList = recordQuery.getResultList();
//		
//		return new PagingDataList(resultList.size(), resultList, paginator);
//	}
//	
//	public <R> List<R> findAll(String sql, Object paramsObj, Class<R> resultClass) {
//		Query query = entityManager.createNativeQuery(sql, resultClass); // resultClass must be Entity Class
//		setParameters(query, paramsObj, null);
//		
//		return query.getResultList();
//	}
//	
//	public <R> R findOne(String sql, Object paramsObj, Class<R> resultClass) {
//		Query query = entityManager.createNativeQuery(sql, resultClass);
//		setParameters(query, paramsObj, null);
//		
//		return (R) query.getSingleResult();
//	}
//	
//	public int executeSql(String sql, Object paramsObj) {
//		Query query = entityManager.createNativeQuery(sql);
//		setParameters(query, paramsObj, null);
//		
//		return query.executeUpdate();
//	}
	
	// ----------------------
	// Hibernate JPA
	// ----------------------
	
//	public void setParameters(Query query, Object paramsObj, Paginator paginator) {
//		if (paramsObj != null) {
//			if (paramsObj instanceof List) {
//				List<Object> params = (List<Object>) paramsObj;
//				if (params.size() > 0) {
//					for (int i = 1; i <= params.size(); i++) {
//						query.setParameter(i, params.get(i - 1));
//					}
//				}
//			} 
//			else if (paramsObj instanceof Map) {
//				Map<String, Object> params = (Map<String, Object>) paramsObj;
//				if (params.size() > 0) {
//					for (Map.Entry<String, Object> entry : params.entrySet()) {
//						query.setParameter(entry.getKey(), entry.getValue());
//					}
//				}
//			} 
//			else if (paramsObj instanceof Object[]) {
//				Object[] params = (Object[]) paramsObj;
//				if (params.length > 0) {
//					for (int i = 1; i <= params.length; i++) {
//						query.setParameter(i, params[i - 1]);
//					}
//				}
//			}
//			
//			if (paginator != null) {
//				query.setFirstResult(paginator.getOffset());
//				query.setMaxResults(paginator.getLimit());
//			}
//		}
//		
//	}
//	public void setParameters(Query query, Object[] paramsObj) {
//		setParameters(query, paramsObj, null);
//	}
//	
//	public PagingDataList findAll(String countSql, String recordSql, Object paramsObj, Paginator paginator, Class<?> resultClass) {
//		Query countQuery = createNativeQuery(countSql, Long.class);
//		setParameters(countQuery, paramsObj, paginator);
//		
//		paginator.setTotalCount((Long) countQuery.uniqueResult());
//		
//		Query recordQuery = createNativeQuery(recordSql, resultClass);
//		setParameters(recordQuery, paramsObj, paginator);
//		
//		List resultList = recordQuery.list();
//		
//		return new PagingDataList(resultList.size(), resultList, paginator);
//	}
//	
//	public <R> List<R> findAll(String sql, Object paramsObj, Class<R> resultClass) {
//		Query query = createNativeQuery(sql, resultClass); // resultClass must be Entity Class
//		setParameters(query, paramsObj, null);
//		
//		return query.list();
//	}
//	
//	public <R> R findOne(String sql, Object paramsObj, Class<R> resultClass) {
//		Query query = createNativeQuery(sql, resultClass);
//		setParameters(query, paramsObj, null);
//		
//		return (R) query.uniqueResult();
//	}
//	
//	public int executeSql(String sql, Object paramsObj) {
//		Query query = createNativeQuery(sql);
//		setParameters(query, paramsObj, null);
//		
//		return query.executeUpdate();
//	}
//	
//	private Query createNativeQuery(String sql, Class<?> resultClass) {
//		Session session = (Session) entityManager.getDelegate(); 
//		return session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(resultClass)); // id java.lang.Integer !=> java.lang.Long
//	}
//	private Query createNativeQuery(String sql) {
//		Session session = (Session) entityManager.getDelegate(); 
//		return session.createSQLQuery(sql);
//	}
}