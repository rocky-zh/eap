package eap.comps.codetable.collector;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import eap.comps.codetable.CodeTableVO;
import eap.comps.codetable.ICodeTableCollector;

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
public class SqlCollector implements ICodeTableCollector {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected JdbcTemplate jdbcTemplate;
	
	protected String collectSql;
	protected String singleCollectSql;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<CodeTableVO> collect() {
		try {
			return jdbcTemplate.query(collectSql, new BeanPropertyRowMapper(CodeTableVO.class));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
//			throw new IllegalArgumentException(e.getMessage(), e);
			return Collections.EMPTY_LIST;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<CodeTableVO> collect(String codeType) {
		if (StringUtils.isBlank(singleCollectSql)) {
			return this.collect();
		}
		
		try {
			return jdbcTemplate.query(singleCollectSql, new String[] { codeType }, new BeanPropertyRowMapper(CodeTableVO.class));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
//			throw new IllegalArgumentException(e.getMessage(), e);
			return Collections.EMPTY_LIST;
		}
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public void setCollectSql(String collectSql) {
		this.collectSql = collectSql;
	}
	public void setSingleCollectSql(String singleCollectSql) {
		this.singleCollectSql = singleCollectSql;
	}
}