package eap.comps.datamapping.definition.support;

import java.util.HashMap;
import java.util.Map;

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
public class DefaultDefinitionRegistry<T> implements IDefinitionRegistry<T> {
	private Map<String, T> definitions = new HashMap<String, T>();
	
	public void register(String name, T definition) {
		definitions.put(name, definition);
	}
	
	public boolean containsName(String name) {
		return definitions.containsKey(name);
	}
	
	public void clear() {
		definitions.clear();
	}
	
	public T remove(String name) {
		return definitions.remove(name);
	}

	public T get(String name) {
		return definitions.get(name);
	}
}