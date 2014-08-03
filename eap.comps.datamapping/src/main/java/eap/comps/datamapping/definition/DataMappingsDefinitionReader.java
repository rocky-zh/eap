package eap.comps.datamapping.definition;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import eap.comps.datamapping.definition.support.DefaultDefinitionRegistry;
import eap.comps.datamapping.definition.support.IDefinitionRegistry;
import eap.comps.datamapping.factory.object.template.ITemplateEngine;
import eap.comps.datamapping.factory.object.template.digester.DigesterTemplateEngine;

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
public class DataMappingsDefinitionReader implements IDataMappingsDefinitionReader {
	
	private static final Logger logger = LoggerFactory.getLogger(DataMappingsDefinitionReader.class);
	
	public static final String CONFIG_RULE_FILE = "eap/comps/datamapping/definition/rule/DataMappingsRule.xml";
	
	private ITemplateEngine tplEngine;
	
	private DataMappingsDefinition dataMappingsDefinition;
	private IDefinitionRegistry<ConstantDefinition> cdRegistry;
	private IDefinitionRegistry<HandlerDefinition> hdRegistry;
	private IDefinitionRegistry<RendererDefinition> rdRegistry;
	private IDefinitionRegistry<ValidatorDefinition> vdRegistry;
	private IDefinitionRegistry<DataFieldSetDefinition> dfsdRegistry;
	private IDefinitionRegistry<DataMappingDefinition> dtdRegistry;
	
	public DataMappingsDefinitionReader() {
		tplEngine = new DigesterTemplateEngine();
		cdRegistry = new DefaultDefinitionRegistry<ConstantDefinition>();
		hdRegistry = new DefaultDefinitionRegistry<HandlerDefinition>();
		rdRegistry = new DefaultDefinitionRegistry<RendererDefinition>();
		vdRegistry = new DefaultDefinitionRegistry<ValidatorDefinition>();
		dfsdRegistry = new DefaultDefinitionRegistry<DataFieldSetDefinition>();
		dtdRegistry = new DefaultDefinitionRegistry<DataMappingDefinition>();
	}
	
	public void read(String configPath) {
		this.clearRegistry();
		dataMappingsDefinition = this.load(configPath);
		if (dataMappingsDefinition != null) {
			this.importResouces(new ArrayList<ImportDefinition>(dataMappingsDefinition.getImports()), dataMappingsDefinition);
			this.prepare(dataMappingsDefinition);
		}
	}
	public ClassLoader getClassLoader() {
		return null; //TODO 
	}

	public DataMappingsDefinition getDefinition() {
		return this.getDataMappings();
	}
	public DataMappingsDefinition getDataMappings() {
		return dataMappingsDefinition;
	}
	public ConstantDefinition getConstant(String name) {
		return cdRegistry.get(name);
	}
	public List<ConstantDefinition> getConstants() {
		return dataMappingsDefinition.getConstants();
	}
	public HandlerDefinition getHandler(String name) {
		return hdRegistry.get(name);
	}
	public RendererDefinition getRenderer(String name) {
		return rdRegistry.get(name);
	}
	public ValidatorDefinition getValidator(String name) {
		return vdRegistry.get(name);
	}
	public DataFieldSetDefinition getDataFieldSet(String name) {
		return dfsdRegistry.get(name);
	}
	public DataMappingDefinition getDataMapping(String name) {
		return dtdRegistry.get(name);
	}
	
	public void addDataMappingDefinition(DataMappingDefinition dmd) {
		synchronized (dtdRegistry) {
			if (dtdRegistry.containsName(dmd.getName())) {
				List<DataMappingDefinition> dtdList = dataMappingsDefinition.getDataMappings();
				for (int i = 0; i < dtdList.size(); i++) {
					if (dtdList.get(i).getName().equals(dmd.getName())) {
						dtdList.set(i, dmd);
						break;
					}
				}
			} else {
				dataMappingsDefinition.addDataMapping(dmd);
			}
			
			dtdRegistry.register(dmd.getName(), dmd);
		}
	}
	
	private DataMappingsDefinition load(String configPath) {
		Resource config = new ClassPathResource(configPath, this.getClassLoader());
		InputStream inputStream = null;
		try {
			inputStream = config.getInputStream();
		} catch (Exception e) {
			logger.error("load datamapping file [" + configPath + "] error", e);
			return null;
//			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		Properties settings = new Properties();
		
		
		return (DataMappingsDefinition) tplEngine.process(CONFIG_RULE_FILE, inputStream, settings);
	}
	private void importResouces(List<ImportDefinition> ids, DataMappingsDefinition parentDmsd) {
		if (ids == null || ids.size() == 0) {
			return;
		}
		
		for (ImportDefinition id : ids) {
			String resource = id.getResource();
			DataMappingsDefinition dtsd = this.load(resource);
			if (dtsd != null) {
				this.importResouces(new ArrayList<ImportDefinition>(dtsd.getImports()), dtsd);
				parentDmsd.merge(dtsd);
			}
		}
	}
	
	private void prepare(DataMappingsDefinition dmsd) {
		for (ConstantDefinition cd : dmsd.getConstants()) {
			cdRegistry.register(cd.getName(), cd);
		}
		for (HandlerDefinition hd : dmsd.getHandlers()) {
			hdRegistry.register(hd.getName(), hd);
		}
		for (RendererDefinition rd : dmsd.getRenderers()) {
			rdRegistry.register(rd.getName(), rd);
		}
		for (ValidatorDefinition vd : dmsd.getValidators()) {
			vdRegistry.register(vd.getName(), vd);
		}
		for (DataFieldSetDefinition sfSetd : dmsd.getDataFieldSets()) {
			dfsdRegistry.register(sfSetd.getName(), sfSetd);
		}
		for (DataMappingDefinition dtd : dmsd.getDataMappings()) {
			dtdRegistry.register(dtd.getName(), dtd);
		}
	}
	
	private void clearRegistry() {
		if (cdRegistry != null) cdRegistry.clear();
		if (hdRegistry != null) hdRegistry.clear();
		if (rdRegistry != null) rdRegistry.clear();
		if (vdRegistry != null) vdRegistry.clear();
		if (dfsdRegistry != null) dfsdRegistry.clear();
		if (dtdRegistry != null) dtdRegistry.clear();
	}
}