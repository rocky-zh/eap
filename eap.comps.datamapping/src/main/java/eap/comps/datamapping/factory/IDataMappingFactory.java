package eap.comps.datamapping.factory;

import java.util.List;
import java.util.Properties;

import eap.comps.datamapping.api.IDataMapping;
import eap.comps.datamapping.api.IHandler;
import eap.comps.datamapping.api.IRenderer;
import eap.comps.datamapping.api.IValidator;
import eap.comps.datamapping.definition.DataFieldDefinition;
import eap.comps.datamapping.definition.DataFieldSetDefinition;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.HandlerDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.ValidatorDefinition;
import eap.util.objectfactory.IObjectFactory;

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
public interface IDataMappingFactory {
	
	public void refresh();
	
	public DataMappingDefinition getDataMappingDefinition(String name);
	public void addDynamicDataMappingDefinition(DataMappingDefinition dtd);
	
	public IDataMapping<?, ?> getDataMapping(String name);
	public IDataMapping<?, ?> getDataMappingByParserType(String parserType);
	public boolean containsDataMapping(String name);
	
	public String getConstantValue(String name);
	public String getConstantValue(String name, String defaultValue);
	public <T> T getConstantValue(String name, Class<T> requiredType);
	public <T> T getConstantValue(String name, Class<T> requiredType, T defaultValue);
	public Properties getConstantsOfPrefix(String prefix);
	
	public IHandler getHandler(String name);
	public IHandler getHandler(HandlerDefinition hd);
	
	public IRenderer getRenderer(String name);
	public IRenderer getRenderer(RendererDefinition rd);
	
	public IValidator getValidator(String name);
	public IValidator getValidator(ValidatorDefinition vd);
	
	public List<DataFieldDefinition> getDataFieldSet(String name);
	public List<DataFieldDefinition> getDataFieldSet(DataFieldSetDefinition fsd);
	
	public IObjectFactory getObjectFactory();
}