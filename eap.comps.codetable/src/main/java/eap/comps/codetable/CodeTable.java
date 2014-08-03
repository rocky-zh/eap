package eap.comps.codetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import eap.EapContext;
import eap.Env;
import eap.comps.codetable.collector.SqlCollector;

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
public class CodeTable {
	private static final Logger logger = LoggerFactory.getLogger(CodeTable.class);

	/** 代码组别分隔符 */
	public static final String CODE_GROUP_SEPARATOR = " |,;";
	
	public static final String SELECT_OPTION_ITEM_LABLE = "codeName";
	public static final String SELECT_OPTION_ITEM_VALUE = "codeValue";
	/** ComboItem空选项值 */
	public static final String COMBO_ITEM_EMPTY_HEADER_VALUE = "";
	/** ComboItem空选项(text="请选择") */
	public static final String[] COMBO_ITEM_CHOICE_HEADER = {COMBO_ITEM_EMPTY_HEADER_VALUE, "请选择"};
	

	/** 代码集合缓存 */
	private static Map<String, Object> cache = new ConcurrentHashMap<String,Object>();
//	private static String cacheKeyPrefix = "$ct_";
	
	private static boolean autoCollector = true;
	private static JdbcTemplate jdbcTemplate;

	private static String defaultUseCollector;
	private static Map<String, ICodeTableCollector> collectors;
	
	private static MultiValueMap<String, String> refreshSceneMap = CollectionUtils.toMultiValueMap(new HashMap<String, List<String>>());
	
//	private static String sqlCollectorPropertiesPath;

	public void afterPropertiesSet() throws Exception {
		if (autoCollector) {
			refreshSceneMap.clear();
			
			Env env = EapContext.getEnv();
			String autoCollectors = env.getProperty("codeTable.autoCollectors", "");
			String[] autoCollectorArray = StringUtils.split(autoCollectors, ",");
			if (autoCollectorArray.length > 0) {
				for (String autoCollector : autoCollectorArray) {
					if (collectors == null) {
						collectors = new HashMap<String, ICodeTableCollector>();
					}
					
					if (!collectors.containsKey(autoCollector)) {
						SqlCollector collector = new SqlCollector();
						collector.setJdbcTemplate(jdbcTemplate);
						collector.setCollectSql(env.getProperty(String.format("codeTable.%s.collectSql", autoCollector)));
						collector.setSingleCollectSql(env.getProperty(String.format("codeTable.%s.singleCollectSql", autoCollector)));
						
						String refreshScenes = env.getProperty(String.format("codeTable.%s.refresh", autoCollector), "");
						String[] refreshSceneArray = StringUtils.split(refreshScenes, ",");
						if (refreshSceneArray.length > 0) {
							for (String refreshScene : refreshSceneArray) {
								refreshSceneMap.add(refreshScene, autoCollector);
							}
						}
						
						collectors.put(autoCollector, collector);
					}
				}
			}
		}
		
		loadAll();
	}
	public static synchronized void refresh() {
//		synchronized (cache) {
			loadAll();
//		}
	}
	public static synchronized void refresh(String codeType) {
		if (StringUtils.isBlank(codeType)) {
			return;
		}
//		synchronized (cache) {
			String collector = null;
			if (!collectors.containsKey(codeType) && StringUtils.isNotBlank(defaultUseCollector)) {
				collector = defaultUseCollector;
			} else {
				collector = codeType;
			}
			
			if (StringUtils.isNotBlank(collector)) {
				List<CodeTableVO> codeTableVOList = collectors.get(collector).collect(codeType);
				putToCache(codeTableVOList);
			}
//		}
	}
	public static void clear(String codeType) {
		if (StringUtils.isBlank(codeType)) {
			return;
		}
		
		cache.remove(getCacheKey(codeType));
	}
	public static void clearByScene(String scene) {
		List<String> refreshCodeList = refreshSceneMap.get(scene);
		if (refreshCodeList != null && refreshCodeList.size() > 0) {
			for (String refreshCode : refreshCodeList) {
				clear(refreshCode);
			}
		}
	}

	/**
	 * 根据代码类型获取代码列表
	 * 所有Code操作方法入口类
	 * @param codeType 代码类型
	 * @return 代码列表
	 */
	public static List<CodeTableVO> getCodes(String codeType) {
		List<CodeTableVO> codeVOList = (List<CodeTableVO>) cache.get(getCacheKey(codeType));
		if (codeVOList == null) {
			refresh(codeType);
			codeVOList = (List<CodeTableVO>) cache.get(getCacheKey(codeType));
		}

		return codeVOList != null ? codeVOList : Collections.EMPTY_LIST;
	}

	/**
	 * 根据代码类型和代码键获取代码
	 * @param codeType 代码类型
	 * @param key 代码键
	 * @return 代码
	 */
	public static CodeTableVO getCode(String codeType, String key) {
		List<CodeTableVO> codeVOList = getCodes(codeType);
		if (codeVOList != null && codeVOList.size() > 0) {
			for (CodeTableVO codeVO : codeVOList) {
				if (codeVO.getCodeKey().equals(key)) {
					return codeVO;
				}
			}
		}

		return null;
	}

	/**
	 * 根据代码类型和代码值获取代码
	 * @param codeType 代码类型
	 * @param value 代码值
	 * @return 代码
	 */
	public static CodeTableVO getCodeByValue(String codeType, String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}

		List<CodeTableVO> codeVOList = getCodes(codeType);
		if (codeVOList != null && codeVOList.size() > 0) {
			for (CodeTableVO codeVO : codeVOList) {
//				if (codeVO.getCodeValue().equals(value)) {
				if (value.equals(codeVO.getCodeValue())) {
					return codeVO;
				}
			}
		}

		return null;
	}

	/**
	 * 根据代码类型和代码值获取组
	 * @param codeType 代码类型
	 * @param value 代码值
	 * @return 代码
	 */
	public static String getGroupByValue(String codeType, String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}

		List<CodeTableVO> codeVOList = getCodes(codeType);
		if (codeVOList != null && codeVOList.size() > 0) {
			for (CodeTableVO codeVO : codeVOList) {
				if (codeVO.getCodeValue().equals(value)) {
					return codeVO.getCodeGroup();
				}
			}
		}

		return null;
	}
	/**
	 * 根据代码类型获取代码列表, 并转化为Map类型,Map-key为代码键,Map-value为代码值
	 * @param codeType 代码类型
	 * @param groups 组别
	 * @return 代码列表转换后的Map
	 */
	public static Map<String, String> getCodesAsMap(String codeType, String... groups) {
		List<CodeTableVO> codeList = getCodes(codeType, groups);
		if (codeList != null && codeList.size() > 0) {
			Map<String, String> codeMap = new HashMap<String, String>();
			for (CodeTableVO code : codeList) {
				codeMap.put(code.getCodeKey(), code.getCodeValue());
			}

			return codeMap;
		}

		return null;
	}

	/**
	 * 根据代码类型和代码组别列表获取代码列表
	 * @param codeType 代码类型
	 * @param groups 代码组别列表
	 * @return 代码列表
	 */
	public static List<CodeTableVO> getCodes(String codeType, String... groups) {
		if (groups == null || groups.length == 0 || groups[0] == null) {
			return getCodes(codeType);
		}

		List<CodeTableVO> codeVOList = getCodes(codeType);
		if (codeVOList != null && codeVOList.size() > 0) {
			List<CodeTableVO> result = new ArrayList<CodeTableVO>();
			for (CodeTableVO codeVO : codeVOList) {
				String[] codeGroups = StringUtils.split(codeVO.getCodeGroup(), CODE_GROUP_SEPARATOR);
				if (codeGroups == null || codeGroups.length == 0) {
					continue;
				}

				for (String group : groups) {
					int index = ArrayUtils.indexOf(codeGroups, group);
					if (index > -1) {
						result.add(codeVO);
					}
				}
			}

			return result;
		}

		return Collections.EMPTY_LIST;
	}

	/**
	 * 根据代码类型和代码键获取代码值
	 * @param codeType 代码类型
	 * @param key 代码键
	 * @return 代码值
	 */
	public static String getValue(String codeType, String key) {
		CodeTableVO codeVO = getCode(codeType, key);
		if (codeVO != null) {
			return codeVO.getCodeValue();
		}

		return null;
	}

	/**
	 * 根据代码类型和代码名称获取代码值
	 * @param codeType 代码类型
	 * @param name 代码名称
	 * @return 代码值
	 */
	public static String getValueByName(String codeType, String name) {
		List<CodeTableVO> codeVOList = getCodes(codeType);
		if (codeVOList != null && codeVOList.size() > 0) {
			for (CodeTableVO code : codeVOList) {
				if (StringUtils.equals(name, code.getCodeName())) {
					return code.getCodeValue();
				}
			}
		}

		return null;
	}

	/***
	 * 根据代码类型获取代码列表, 获取列表列表首个代码值
	 * @param codeType 代码类型
	 * @return 首个代码值
	 */
	public static String getValueOfFirstCodes(String codeType) {
		List<CodeTableVO> codes = getCodes(codeType);
		if (codes != null && codes.size() > 0) {
			return codes.get(0).getCodeValue();
		}

		return null;
	}

	/**
	 * 根据代码类型和代码键获取代码名称
	 * @param codeType 代码类型
	 * @param key 代码键
	 * @return 代码名称
	 */
	public static String getName(String codeType, String key) {
		CodeTableVO codeVO = getCode(codeType, key);
		if (codeVO != null) {
			return codeVO.getCodeName();
		}
		return null;
	}
	
	public static List<String> getNames(String codeType){
		List<CodeTableVO> voList = getCodes(codeType);
		List<String> codeNames = new ArrayList<String>(voList.size());
		for(CodeTableVO vo : voList){
			codeNames.add(vo.getCodeName());
		}
		return codeNames;
	}
	
	public static String getName(String codeType, Object key) {
		if (key != null) {
			return getName(codeType, key.toString());
		}
		
		return null;
	}

	/**
	 * 根据代码类型和代码值获取代码名称
	 * @param codeType 代码类型
	 * @param value 代码值
	 * @return 代码名称
	 */
	public static String getNameByValue(String codeType, String value) {
		CodeTableVO codeVO = getCodeByValue(codeType, value);
		if (codeVO != null) {
			return codeVO.getCodeName();
		}

		return null;
	}

	/**
	 * 根据代码类型或代码键获取代码值, 是否等于目标值,如果等于返回true, 否之false
	 * @param codeType 代码类型
	 * @param key 代码键
	 * @param descValue 目标值
	 * @return true 或 false
	 */
	public static boolean eqValue(String codeType, String key, Object descValue) {
		String srcValue = getValue(codeType, key);
		if (StringUtils.isBlank(srcValue)) {
			return false;
		}
		if (descValue == null) {
			return false;
		}

		return srcValue.equals(descValue.toString());
	}
	public static boolean neValue(String codeType, String key, Object descValue) {
		return !eqValue(codeType, key, descValue);
	}
	
	public static List<CodeTableVO> getComboItems(String codeType, String[] header, String... groups) {
		if (header == null || header.length != 2) {
			return getCodes(codeType, groups);
		}
		
		List<CodeTableVO> items = new ArrayList<CodeTableVO>(); // codes.size + 1
		items.add(new CodeTableVO(codeType, header[0], header[1]));
		items.addAll(getCodes(codeType, groups));
		
		return items;
	}
	public static List<CodeTableVO> getComboItemsIncludeChoiceHeader(String codeType, String... groups) {
		return getComboItems(codeType, COMBO_ITEM_CHOICE_HEADER, groups);
	}

	private static void loadAll() {
		cache.clear();
		
		if (collectors != null && collectors.size() > 0) {
			Collection<ICodeTableCollector> ctcList = collectors.values();
			for (ICodeTableCollector ctc : ctcList) {
				putToCache(ctc.collect());
			}
		}
		
		logger.info("codetable all loaded");
	}
	private static void putToCache(List<CodeTableVO> codeTableVOList) {
		if (codeTableVOList == null || codeTableVOList.size() == 0) {
			return;
		}
		
		Map<String, List<CodeTableVO>> container = new HashMap<String, List<CodeTableVO>>();
		for (CodeTableVO codeTableVO : codeTableVOList) {
			String codeType = codeTableVO.getCodeType();
			List<CodeTableVO> values = container.get(codeType);
			if (values == null) {
				values = new ArrayList<CodeTableVO>();
			}
			values.add(codeTableVO);
			
			container.put(codeType, values);
		}
		
		for (Map.Entry<String, List<CodeTableVO>> entry : container.entrySet()) {
			cache.put(getCacheKey(entry.getKey()), entry.getValue());
		}
	}
	private static String getCacheKey(String codeType) {
//		return cacheKeyPrefix + codeType;
		return codeType;
	}

//	public void setCacheKeyPrefix(String ckp) {
//		cacheKeyPrefix = ckp;
//	}
	public void setAutoCollector(boolean ac) {
		autoCollector = ac;
	}
	public void setJdbcTemplate(JdbcTemplate jt) {
		jdbcTemplate = jt;
	}
	public void setDefaultUseCollector(String duc) {
		defaultUseCollector = duc;
	}
	public void setCollectors(Map<String, ICodeTableCollector> cs) {
		collectors = cs;
	}
//	public void setSqlCollectorPropertiesPath(String path) {
//		sqlCollectorPropertiesPath = path;
//		if (StringUtils.isNotBlank(path)) {
//			if (collectors == null) {
//				collectors = new HashMap<String, ICodeTableCollector>();
//				
//			}
//		}
//	}
	
//	public static void main(String[] args) {
//		ApplicationContext appCtx = new ClassPathXmlApplicationContext("classpath:AC.xml");
//		
//		List<CodeTableVO> codes = CodeTable.getCodes("Sex");
//		for (CodeTableVO code : codes) {
//			System.out.println( ToStringBuilder.reflectionToString(code) );
//		}
//		
//		CodeTable.getCodes("Sex", "GroupA", "GroupB");
//		
//		
//		System.out.println( CodeTable.getCode("Sex", "S1") );
//		System.out.println( CodeTable.getCodeByValue("Sex", "1") );
//		System.out.println( CodeTable.getCodesAsMap("Sex") );
//		System.out.println( CodeTable.getCodesAsMap("Sex", "GroupA", "GroupB") );
//		
//		
//		System.out.println( CodeTable.getName("Sex", "S1") );
//		System.out.println( CodeTable.getNameByValue("Sex", "1") );
//		
//		System.out.println( CodeTable.getValue("Sex", "S1") );
//		
//		System.out.println( CodeTable.eqValue("Sex", "S1", "1") );
//		System.out.println( CodeTable.neValue("Sex", "S1", "1") );
//	}
}