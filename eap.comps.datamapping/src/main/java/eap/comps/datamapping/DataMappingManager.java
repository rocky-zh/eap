package eap.comps.datamapping;

import java.util.Map;

import eap.comps.datamapping.api.IDataMapping;
import eap.comps.datamapping.factory.IDataMappingFactory;

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
public class DataMappingManager {
	
	private static IDataMappingFactory dataMappingFactory;
	
	public static void refresh() {
		dataMappingFactory.refresh();
	}

	public static Object mapping(String name, Object source) {
		return mapping(name, source, null);
	}
	
	@SuppressWarnings("unchecked")
	public static Object mapping(String name, Object source, Map<String, Object> params) {
		if (dataMappingFactory == null) {
			throw new IllegalStateException("dataMappingFactory not initialized");
		}
		
		IDataMapping dataMapping = dataMappingFactory.getDataMapping(name);
		return dataMapping.mapping(name, source, params);
	}
	
	public static boolean containsDataMapping(String name) {
		return dataMappingFactory.containsDataMapping(name);
	}

	public void setDataMappingFactory(IDataMappingFactory dmf) {
		dataMappingFactory = dmf;
	}
}
