package eap.comps.datamapping.factory.object;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.Assert;

import eap.comps.datamapping.definition.ClassMetadataDefinition;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.TemplateDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.definition.parser.XmlToPojoParserDefinition;
import eap.comps.datamapping.exception.ValidateFailExceptions;
import eap.comps.datamapping.factory.IDataMappingFactory;
import eap.comps.datamapping.factory.object.template.ITemplateEngine;
import eap.util.CharsetUtil;
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
public class XmlToPojoMapping extends AbstractDataMapping<String, Object> {
	public static final String CONSTANT_TPL_PREFIX = "XmlToPojo.Template.";
	
	public static final String CONSTANT_TPL_ENGINE_OBJECTFACTORY= CONSTANT_TPL_PREFIX + "engine.objectFactory";
	public static final String CONSTANT_TPL_ENGINE_CLASSNAME	= CONSTANT_TPL_PREFIX + "engine.className";
	
	public static final String XML_DEFAULT_ENCODING = "UTF-8";
	
	private ITemplateEngine tplEngine;
	
	protected Object mappingImpl(String dtName, String source, Map<String, Object> params,
			DataMappingDefinition dtd, ParserDefinition pd) 
	{
		this.initTplEngine();
		
		XmlToPojoParserDefinition xtppd = (XmlToPojoParserDefinition) pd;
		String failMode = xtppd.getFailMode();
		TemplateDefinition td = xtppd.getTemplate();
		
		source = (String) this.render(source, td, td.getRenderers(), RendererDefinition.PHASE_BEFORE);
		
		ValidateFailExceptions validateFailExceptions = new ValidateFailExceptions();
		boolean validatePassed = this.validate(source, td, td.getValidator(), failMode, validateFailExceptions);
		if (!validatePassed) {
			this.handle(null, null, dtd, dtd.getHandlers(), validateFailExceptions);
			return null;
		}
		
		String tpl = td.getFile();
		Properties tplSettings = td.getProps();
		tplSettings.putAll(params);
		String encoding = tplSettings.getProperty("encoding");
		if (StringUtil.isBlank(encoding)) {
			encoding = dataMappingFactory.getConstantValue(CONSTANT_TPL_PREFIX + ".encoding", XML_DEFAULT_ENCODING); // // TODO encoding
		}
		byte[] sourceBytes = CharsetUtil.getBytes(source, encoding);
		
		Object result = (Object) tplEngine.process(tpl, new ByteArrayInputStream(sourceBytes), tplSettings);
		
		result = this.render(result, td, td.getRenderers(), RendererDefinition.PHASE_AFTER);
		
		this.handle(result, source, dtd, dtd.getHandlers(), null);
		
		return result;
	}
	
	@Override
	public void setDataMappingFactory(IDataMappingFactory dataMappingFactory) {
		super.setDataMappingFactory(dataMappingFactory);
		this.initTplEngine();
	}
	private void initTplEngine() {
		if (tplEngine == null) {
			String objectFactory = dataMappingFactory.getConstantValue(CONSTANT_TPL_ENGINE_OBJECTFACTORY, ClassMetadataDefinition.OBJECTFACTORY_NEW);
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
}