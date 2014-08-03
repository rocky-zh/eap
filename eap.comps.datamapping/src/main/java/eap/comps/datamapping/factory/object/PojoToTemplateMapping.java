package eap.comps.datamapping.factory.object;

import java.util.Map;
import java.util.Properties;

import org.springframework.util.Assert;

import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.TemplateDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.definition.parser.PojoToTemplateParserDefinition;
import eap.comps.datamapping.exception.ValidateFailExceptions;
import eap.comps.datamapping.factory.IDataMappingFactory;
import eap.comps.datamapping.factory.object.template.ITemplateEngine;

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
public class PojoToTemplateMapping extends AbstractDataMapping<Object, String> {
	public static final String CONSTANT_TPL_PREFIX = "PojoToTemplate.Template.";
	
	public static final String CONSTANT_TPL_ENGINE_OBJECTFACTORY = CONSTANT_TPL_PREFIX + "engine.objectFactory";
	public static final String CONSTANT_TPL_ENGINE_CLASSNAME = CONSTANT_TPL_PREFIX + "engine.className";
	
	private ITemplateEngine tplEngine;
	
	protected String mappingImpl(String name, Object source, Map<String, Object> params,
			DataMappingDefinition dmd, ParserDefinition pd) 
	{
		this.initTplEngine();
		
		PojoToTemplateParserDefinition pttpd = (PojoToTemplateParserDefinition) pd;
		String failMode = pttpd.getFailMode();
		TemplateDefinition td = pttpd.getTemplate();
		
		source = this.render(source, td, td.getRenderers(), RendererDefinition.PHASE_BEFORE);
		
		String tpl = td.getFile();
		Properties tplSettings = td.getProps(); // TODO encoding
		tplSettings.putAll(params);
		String procResult = (String) tplEngine.process(tpl, source, tplSettings);
		
		ValidateFailExceptions validateFailExceptions = new ValidateFailExceptions();
		boolean validatePassed = this.validate(procResult, td, td.getValidator(), failMode, validateFailExceptions);
		String result = null;
		if (validatePassed) {
			result = (String) this.render(procResult, td, td.getRenderers(), RendererDefinition.PHASE_AFTER);
			this.handle(result, source, dmd, dmd.getHandlers(), null);
		} else {
			this.handle(null, null, dmd, dmd.getHandlers(), validateFailExceptions);
		}
		
		return result;
	}
	
	@Override
	public void setDataMappingFactory(IDataMappingFactory dataMappingFactory) {
		super.setDataMappingFactory(dataMappingFactory);
		this.initTplEngine();
	}
	
	private void initTplEngine() {
		if (tplEngine == null) {
			String objectFactory = dataMappingFactory.getConstantValue(CONSTANT_TPL_ENGINE_OBJECTFACTORY);
			Assert.hasText(objectFactory, "constant '" + CONSTANT_TPL_ENGINE_OBJECTFACTORY + "' must not be empty");
			
			String tplEngineClassName = dataMappingFactory.getConstantValue(CONSTANT_TPL_ENGINE_CLASSNAME);
			Assert.hasText(tplEngineClassName, "constant '" + CONSTANT_TPL_ENGINE_CLASSNAME + "' must not be empty");
			
			tplEngine = (ITemplateEngine) this.getObject(objectFactory, tplEngineClassName);
		}
		
		Properties tplEngineSettings = dataMappingFactory.getConstantsOfPrefix(CONSTANT_TPL_PREFIX);
		tplEngine.initEngineSettings(tplEngineSettings);
	}
	
	public void setTplEngine(ITemplateEngine tplEngine) {
		this.tplEngine = tplEngine;
	}

	public ITemplateEngine getTplEngine() {
		return tplEngine;
	}
}