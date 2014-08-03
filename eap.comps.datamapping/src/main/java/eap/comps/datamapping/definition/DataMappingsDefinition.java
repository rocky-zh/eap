package eap.comps.datamapping.definition;

import java.util.ArrayList;
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
public class DataMappingsDefinition extends Definition {
	
	private String objectFactory = ClassMetadataDefinition.OBJECTFACTORY_NEW;
	private List<ImportDefinition> imports = new ArrayList<ImportDefinition>();
	private List<ConstantDefinition> constants = new ArrayList<ConstantDefinition>();
	private List<HandlerDefinition> handlers = new ArrayList<HandlerDefinition>();
	private List<RendererDefinition> renderers = new ArrayList<RendererDefinition>();
	private List<ValidatorDefinition> validators = new ArrayList<ValidatorDefinition>();
	private List<DataFieldSetDefinition> dataFieldSets = new ArrayList<DataFieldSetDefinition>();
	
	private List<DataMappingDefinition> dataMappings = new ArrayList<DataMappingDefinition>();
	
	public void merge(DataMappingsDefinition dtsd) {
		if (dtsd == null) {
			return;
		}
		imports.addAll(dtsd.getImports());
		constants.addAll(dtsd.getConstants());
		handlers.addAll(dtsd.getHandlers());
		renderers.addAll(dtsd.getRenderers());
		validators.addAll(dtsd.getValidators());
		dataFieldSets.addAll(dtsd.getDataFieldSets());
		dataMappings.addAll(dtsd.getDataMappings());
	}
	
	public String getObjectFactory() {
		return objectFactory;
	}
	public void setObjectFactory(String objectFactory) {
		this.objectFactory = objectFactory;
	}
	
	public void addImport(ImportDefinition id) {
		this.imports.add(id);
	}
	public List<ImportDefinition> getImports() {
		return imports;
	}
	public void setImports(List<ImportDefinition> imports) {
		this.imports = imports;
	}
	
	public void addConstant(ConstantDefinition cd) {
		constants.add(cd);
	}
	public List<ConstantDefinition> getConstants() {
		return constants;
	}
	public void setConstants(List<ConstantDefinition> constants) {
		this.constants = constants;
	}
	
	public void addHandler(HandlerDefinition handler) {
		handlers.add(handler);
	}
	public List<HandlerDefinition> getHandlers() {
		return handlers;
	}
	
	public void addRenderer(RendererDefinition renderer) {
		renderers.add(renderer);
	}
	public List<RendererDefinition> getRenderers() {
		return renderers;
	}
	
	public void addValidator(ValidatorDefinition validator) {
		validators.add(validator);
	}
	public List<ValidatorDefinition> getValidators() {
		return validators;
	}
	
	public void addDataFieldSet(DataFieldSetDefinition dataFieldSet) {
		dataFieldSets.add(dataFieldSet);
	}
	public List<DataFieldSetDefinition> getDataFieldSets() {
		return dataFieldSets;
	}
	public void setDataFieldSets(List<DataFieldSetDefinition> dataFieldSets) {
		this.dataFieldSets = dataFieldSets;
	}

	public void addDataMapping(DataMappingDefinition dataMapping) {
		dataMappings.add(dataMapping);
	}
	public List<DataMappingDefinition> getDataMappings() {
		return dataMappings;
	}
}