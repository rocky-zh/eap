package eap.comps.datamapping.definition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eap.comps.datamapping.util.ObjectUtil;
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
public class RendererDefinition extends ClassMetadataDefinition {
	
	public static final String PHASE_BEFORE = "before";
	public static final String PHASE_AFTER = "after";
	
	private String style;
	private Map<String, String> styleMap = new ConcurrentHashMap<String, String>();
	
	private String phase = PHASE_AFTER;
	
	public String getStyleValue(String name) {
		if (StringUtil.isBlank(name)) {
			return null;
		}
		
		return styleMap.get(name.toLowerCase());
	}
	public <T> T getStyleValue(String name, Class<T> requiredType) {
		String value = styleMap.get(name.toLowerCase());
		if (value == null) {
			throw new IllegalArgumentException("not found style: " + name);
		}
		
		return ObjectUtil.to(value, requiredType);
	}
	
	private volatile boolean mergedStyle = false;
	public void mergeStyle(Map<String, String> srcStyle, boolean overwrite) {
		if (!mergedStyle) {
			for (String srcKey : srcStyle.keySet()) {
				if (styleMap.containsKey(srcKey) && !overwrite) {
					continue;
				}
				styleMap.put(srcKey, srcStyle.get(srcKey));
			}
			mergedStyle = true;
		}
	}

	public void setStyle(String style) {
		this.style = style;
		this.styleMap = splitStyle(style);
		mergedStyle = false;
	}
	public String getStyle() {
		return style;
	}
	public Map<String, String> getStyleMap() {
		return styleMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> splitStyle(String styleStr) {
		if (StringUtil.isBlank(styleStr)) {
			return Collections.EMPTY_MAP;
		}
		
		Map<String,String> styleMap = new HashMap<String, String>();
		String[] stylePairGroup = styleStr.split(";");
		for (String stylePair : stylePairGroup) {
			if (StringUtil.isNotBlank(stylePair)) {
				String[] stylePairArr = StringUtil.split(stylePair, ":", 2);
				if (stylePairArr != null) {
					if (stylePairArr.length == 2) {
						styleMap.put(stylePairArr[0].trim().toLowerCase(), stylePairArr[1]);
					} else if (stylePairArr.length == 1) {
						styleMap.put(stylePairArr[0].trim().toLowerCase(), "");
					}
				}
			}
		}
		
		return styleMap;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
}