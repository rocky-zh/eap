package eap.comps.datamapping.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import eap.comps.datamapping.api.IDataMapping;
import eap.comps.datamapping.api.IHandler;
import eap.comps.datamapping.api.IRenderer;
import eap.comps.datamapping.api.IValidator;
import eap.comps.datamapping.definition.CellFieldGroupDefinition;
import eap.comps.datamapping.definition.CellFieldSetDefinition;
import eap.comps.datamapping.definition.ClassMetadataDefinition;
import eap.comps.datamapping.definition.ConstantDefinition;
import eap.comps.datamapping.definition.DataFieldDefinition;
import eap.comps.datamapping.definition.DataFieldSetDefinition;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.DataMappingsDefinitionReader;
import eap.comps.datamapping.definition.HandlerDefinition;
import eap.comps.datamapping.definition.IDataMappingsDefinitionReader;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.TextFieldGroupDefinition;
import eap.comps.datamapping.definition.TextFieldSetDefinition;
import eap.comps.datamapping.definition.ValidatorDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.util.ObjectUtil;
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
public class DataMappingFactory implements IDataMappingFactory {
	
	public static final String CONFIG_FILE = "_DataMappings.xml";
	
	public static final String CONSTANT_ENV_PARSERDEFINITION_HANDLER_REGISTORY = "Env.ParserDefinitionHandlerRegistry";
	
	private IObjectFactory objectFactory;
	
	private Map<String, Object> singletonObjects;
	private IDataMappingsDefinitionReader dmsdReader;
	private Map<String, IDataMapping<?,?>> dataMappings;
	
	public void init() {
		this.refresh();
	}
	
	public String getObjectFactoryName() {
		return DataMappingFactory.class.getSimpleName();
	}
	
	public DataMappingDefinition getDataMappingDefinition(String name) {
		return dmsdReader.getDataMapping(name);
	}
	public void addDynamicDataMappingDefinition(DataMappingDefinition dtd) {
		dmsdReader.addDataMappingDefinition(dtd);
	}

	public IDataMapping<?, ?> getDataMapping(String name) {
		DataMappingDefinition dtd = dmsdReader.getDataMapping(name);
		Assert.notNull(dtd, "DataMappingDefinition '" + name + "' not found");
		ParserDefinition pd = dtd.getParser();
		
		return dataMappings.get(pd.getParserType());
	}
	
	public IDataMapping<?, ?> getDataMappingByParserType(String parserType) {
		return dataMappings.get(parserType);
	}
	
	public boolean containsDataMapping(String name) {
		return dmsdReader.getDataMapping(name) != null ? true : false;
	}
	
	public String getConstantValue(String name) {
		if (dmsdReader.getConstant(name) != null) {
			return dmsdReader.getConstant(name).getValue();
		}
		
		return null;
	}
	public String getConstantValue(String name, String defaultValue) {
		String value = this.getConstantValue(name);
		if (value == null) return defaultValue;
		
		return value;
	}
	public <T> T getConstantValue(String name, Class<T> requiredType) {
		return ObjectUtil.to(this.getConstantValue(name), requiredType);
	}
	public <T> T getConstantValue(String name, Class<T> requiredType, T defaultValue) {
		T value = this.getConstantValue(name, requiredType);
		if (value == null) return defaultValue;
		
		return value;
	}
	public Properties getConstantsOfPrefix(String prefix) {
		List<ConstantDefinition> constants = dmsdReader.getConstants();
		if (constants != null && constants.size() > 0) {
			boolean hasPreFix = StringUtils.isNotBlank(prefix);
			Properties props = new Properties();
			for (ConstantDefinition constant : constants) {
				if (hasPreFix) {
					if (constant.getName().length() < prefix.length()) {
						continue;
					}
					if (constant.getName().startsWith(prefix)) {
						String key = constant.getName().substring(prefix.length());
						props.put(key, constant.getValue());
					}
				} else {
					props.put(constant.getName(), constant.getValue());
				}
			}
			
			return props;
		}
		
		return null;
	}
	
	public IHandler getHandler(String name) {
		HandlerDefinition hd = dmsdReader.getHandler(name);
		return this.getHandler(hd);
	}
	public IHandler getHandler(HandlerDefinition hd) {
		if (hd == null) {
			return null;
		}
		
		HandlerDefinition refHd = null;
		String ref = hd.getRef();
		if (StringUtils.isNotBlank(ref)) {
			refHd = dmsdReader.getHandler(ref);
			Assert.notNull(refHd, "not found Handler '" + ref + "'");
		} else {
			refHd = hd;
		}
		
		IHandler handler = this.getObject(refHd, IHandler.class);
		
		return handler;
	}

	public IRenderer getRenderer(String name) {
		RendererDefinition rd = dmsdReader.getRenderer(name);
		return this.getRenderer(rd);
	}
	public IRenderer getRenderer(RendererDefinition rd) {
		if (rd == null) {
			return null;
		}
		
		RendererDefinition refRd = null;
		String ref = rd.getRef();
		if (StringUtils.isNotBlank(ref)) {
			refRd = dmsdReader.getRenderer(ref);
			Assert.notNull(refRd, "not found Renderer '" + ref + "'");
			rd.mergeStyle(refRd.getStyleMap(), false); // merge style for parent style
		} else {
			refRd = rd;
		}
		
		IRenderer renderer = this.getObject(refRd, IRenderer.class);
		
		return renderer;
	}
	
	public IValidator getValidator(String name) {
		ValidatorDefinition vd = dmsdReader.getValidator(name); 
		return this.getValidator(vd);
	}
	public IValidator getValidator(ValidatorDefinition vd) {
		if (vd == null) {
			return null;
		}
		
		ValidatorDefinition refVd = null;
		String ref = vd.getRef();
		if (StringUtils.isNotBlank(ref)) {
			refVd = dmsdReader.getValidator(ref);
			Assert.notNull(refVd, "not found Validator '" + ref + "'");
		} else {
			refVd = vd;
		}
		
		IValidator validator = this.getObject(refVd, IValidator.class);
		
		return validator;
	}
	
	public List<DataFieldDefinition> getDataFieldSet(String name) {
		DataFieldSetDefinition dfsd = dmsdReader.getDataFieldSet(name);
		return this.getDataFieldSet(dfsd);
	}
	
	public List<DataFieldDefinition> getDataFieldSet(DataFieldSetDefinition fsd) {
		if (fsd == null) {
			return null;
		}
		
		DataFieldSetDefinition rfsd = null;
		String ref = fsd.getRef();
		Map<String, DataFieldDefinition> overwriteDfdMap = null;
		if (StringUtils.isNotBlank(ref)) {
			rfsd = dmsdReader.getDataFieldSet(ref);
			Assert.notNull(rfsd, "not found DataFieldSet '" + ref + "'");
			
			if (fsd.getItems().size() > 0) {
				overwriteDfdMap = new HashMap<String, DataFieldDefinition>();
				for (DataFieldDefinition owDfd : fsd.getItems()) {
					if (owDfd instanceof TextFieldSetDefinition 
							|| owDfd instanceof TextFieldGroupDefinition 
							|| owDfd instanceof CellFieldSetDefinition 
							|| owDfd instanceof CellFieldGroupDefinition) {
						continue;
					} else if (owDfd instanceof DataFieldDefinition) {
						if (StringUtils.isNotBlank(owDfd.getName())) {
							overwriteDfdMap.put(owDfd.getName(), owDfd);
						}
					}
				}
			}
		} else {
			rfsd = fsd;
		}
		
		List<DataFieldDefinition> dfds = new ArrayList<DataFieldDefinition>();
		for (DataFieldDefinition rdfd : rfsd.getItems()) {
			if (rdfd instanceof DataFieldSetDefinition) {
				List<DataFieldDefinition> rdfds = this.getDataFieldSet((DataFieldSetDefinition) rdfd);
				if (rdfds != null && rdfds.size() > 0) {
					dfds.addAll(rdfds); // TODO not test....
				}
			} else if (rdfd instanceof DataFieldDefinition) {
				if (overwriteDfdMap != null && overwriteDfdMap.containsKey(rdfd.getName())) {
					dfds.add(overwriteDfdMap.get(rdfd.getName()));
				} else {
					dfds.add(rdfd);
				}
			}
		}
		
		return dfds;
	}
	
	public synchronized void refresh() {
		singletonObjects = new ConcurrentHashMap<String, Object>();
		dmsdReader = null;
		dataMappings = null;
		
		dmsdReader = new DataMappingsDefinitionReader();
		dmsdReader.read(CONFIG_FILE);
		
		this.initEnv();
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getObject(ClassMetadataDefinition classMetadate, Class<T> clazz) {
		if (classMetadate == null) return null;
		
		String objectFactoryName = classMetadate.getObjectFactory();
		if (StringUtils.isBlank(objectFactoryName)) {
			objectFactoryName = dmsdReader.getDataMappings().getObjectFactory();
		}
		
		T object = null;
		boolean isGetInCache = ClassMetadataDefinition.SCOPE_SINGLETON.equalsIgnoreCase(classMetadate.getScope()) 
								&& ClassMetadataDefinition.OBJECTFACTORY_NEW.equalsIgnoreCase(objectFactoryName);
		if (isGetInCache) {
			object = (T) singletonObjects.get(ObjectUtil.identityToString(classMetadate));
			if (object != null) {
				return object;
			}
		}
		
		object = (T) objectFactory.getObject(objectFactoryName + " " + classMetadate.getClassName());
		
		if (isGetInCache) {
			singletonObjects.put(ObjectUtil.identityToString(classMetadate), object);
		}
		
		return object;
	}
	
	private void initEnv() {
		Assert.notNull(objectFactory, "'objectFactory' must not be empty");
		
		String splitRegex =  " |,|;|\\|";
		dataMappings = new HashMap<String, IDataMapping<?,?>>();
		String pdhRegistry = this.getConstantValue(CONSTANT_ENV_PARSERDEFINITION_HANDLER_REGISTORY);
		Assert.hasText(pdhRegistry, "constant '"+ CONSTANT_ENV_PARSERDEFINITION_HANDLER_REGISTORY + "' must not be empty");
		String[] pdhNames = pdhRegistry.split(splitRegex);
		if (pdhNames != null && pdhNames.length > 0) {
			for (String pdhName : pdhNames) {
				String pdhPrefix = CONSTANT_ENV_PARSERDEFINITION_HANDLER_REGISTORY + "." + pdhName + ".";
				String pdhObjectFactory = this.getConstantValue(pdhPrefix + "objectFactory", ClassMetadataDefinition.OBJECTFACTORY_NEW);
				String pdhClassName = this.getConstantValue(pdhPrefix + "className");
				Assert.hasText(pdhClassName, "constant '"+ pdhPrefix + "className" +"' must not be empty");
				
				IDataMapping<?, ?> dt = (IDataMapping<?, ?>) objectFactory.getObject(pdhObjectFactory + " " + pdhClassName);
				dt.setDataMappingFactory(this);
				dataMappings.put(pdhName, dt);
			}
		}
	}

	public IObjectFactory getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(IObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
}