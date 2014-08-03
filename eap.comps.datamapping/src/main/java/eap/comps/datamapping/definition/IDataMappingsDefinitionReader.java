package eap.comps.datamapping.definition;

import java.util.List;

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
public interface IDataMappingsDefinitionReader extends IDefinitionReader<DataMappingsDefinition> {
	public DataMappingsDefinition getDataMappings();
	public ConstantDefinition getConstant(String name);
	public List<ConstantDefinition> getConstants();
	public HandlerDefinition getHandler(String name);
	public RendererDefinition getRenderer(String name);
	public ValidatorDefinition getValidator(String name);
	public DataFieldSetDefinition getDataFieldSet(String name);
	public DataMappingDefinition getDataMapping(String name);
	
	public void addDataMappingDefinition(DataMappingDefinition dtd);
}