package eap.comps.datamapping.api;

import java.util.Map;

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
public interface IDataMapping<S, D> {
//	public D mapping(String name, S source);
	public D mapping(String name, S source, Map<String, Object> params);
	
	public void setDataMappingFactory(IDataMappingFactory dataMappingFactory);
}