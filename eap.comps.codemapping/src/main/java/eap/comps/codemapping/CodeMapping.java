package eap.comps.codemapping;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import eap.EapContext;
import eap.util.StringUtil;

/**
 * <p> Title: 码表映射</p>
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
public class CodeMapping {
	
	private static final Logger logger = LoggerFactory.getLogger(CodeMapping.class);
	
	private static JdbcTemplate jdbcTemplate;
	private static MultiValueMap<String, CodeMappingVO> cache = CollectionUtils.toMultiValueMap(new ConcurrentHashMap());

	public void afterPropertiesSet() throws Exception {
		refresh();
	}

	public static synchronized void refresh() {
		String collectSql = EapContext.getEnv().getProperty("codeMapping.collectSql");
		if (StringUtil.isNotBlank(collectSql)) {
			List<CodeMappingVO> codeMappingVOList = jdbcTemplate.query(collectSql, new BeanPropertyRowMapper(CodeMappingVO.class));
			if ((codeMappingVOList != null) && (codeMappingVOList.size() > 0))
				for (CodeMappingVO codeMappingVO : codeMappingVOList)
					cache.add(codeMappingVO.getMappingType(), codeMappingVO);
		}
	}

	public static List<CodeMappingVO> getCodeMappings(String mappingType) {
		return (List<CodeMappingVO>) cache.get(mappingType);
	}

	public static CodeMappingVO bySrcKey(String mappingType, String srcKey) {
		if (StringUtil.isBlank(srcKey)) {
			return null;
		}

		List<CodeMappingVO> codeMappingVOList = getCodeMappings(mappingType);
		if ((codeMappingVOList != null) && (codeMappingVOList.size() > 0)) {
			for (CodeMappingVO codeMappingVO : codeMappingVOList) {
				if (codeMappingVO.getSrcKey().equals(srcKey)) {
					return codeMappingVO;
				}
			}
		}

		return null;
	}

	public static CodeMappingVO byDescKey(String mappingType, String descKey) {
		if (StringUtil.isBlank(descKey)) {
			return null;
		}

		List<CodeMappingVO> codeMappingVOList = getCodeMappings(mappingType);
		if ((codeMappingVOList != null) && (codeMappingVOList.size() > 0)) {
			for (CodeMappingVO codeMappingVO : codeMappingVOList) {
				if (codeMappingVO.getDescKey().equals(descKey)) {
					return codeMappingVO;
				}
			}
		}

		return null;
	}

	public static String getSrcValue(String mappingType, String descValue) {
		if (StringUtil.isBlank(descValue)) {
			return null;
		}

		List<CodeMappingVO> codeMappingVOList = getCodeMappings(mappingType);
		if ((codeMappingVOList != null) && (codeMappingVOList.size() > 0)) {
			for (CodeMappingVO codeMappingVO : codeMappingVOList) {
				if (descValue.equals(codeMappingVO.getDescValue())) {
					return codeMappingVO.getSrcValue();
				}
			}
		}

		return null;
	}

	public static String getDescValue(String mappingType, String srcValue) {
		if (StringUtil.isBlank(srcValue)) {
			return null;
		}

		List<CodeMappingVO> codeMappingVOList = getCodeMappings(mappingType);
		if ((codeMappingVOList != null) && (codeMappingVOList.size() > 0)) {
			for (CodeMappingVO codeMappingVO : codeMappingVOList) {
				if (srcValue.equals(codeMappingVO.getSrcValue())) {
					return codeMappingVO.getDescValue();
				}
			}
		}

		return null;
	}

	public void setJdbcTemplate(JdbcTemplate jt) {
		jdbcTemplate = jt;
	}
}
