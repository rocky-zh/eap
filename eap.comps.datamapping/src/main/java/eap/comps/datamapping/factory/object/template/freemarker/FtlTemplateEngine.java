package eap.comps.datamapping.factory.object.template.freemarker;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.util.StringUtils;

import eap.comps.datamapping.factory.object.template.ITemplateEngine;
import eap.comps.datamapping.util.ObjectUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;

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
public class FtlTemplateEngine implements ITemplateEngine {
	private static final Logger logger = LoggerFactory.getLogger(FtlTemplateEngine.class);
	
	private Configuration configuration;
	
	public synchronized void initEngineSettings(Properties engineSettings) {
		if (engineSettings == null) return;
		
		for (Object keyObj : engineSettings.keySet()) {
			String key = keyObj.toString();
			String value = engineSettings.getProperty(key);
			if (value == null || value.length() == 0) {
				continue;
			}
			
			if (configuration == null) { 
				FreeMarkerConfigurationFactoryBean fmcFactoryBean = new FreeMarkerConfigurationFactoryBean();
				String templateLoaderPath = engineSettings.getProperty("templateLoaderPath");
				if (StringUtils.hasText(templateLoaderPath)) {
					fmcFactoryBean.setTemplateLoaderPath(templateLoaderPath);
				}
				
				try {
					configuration = (Configuration) fmcFactoryBean.createConfiguration();
				} catch (Exception e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}
			
			try {
				configuration.setSetting(key, value);
			} catch (Exception e) {
				configuration.setCustomAttribute(key, value);
			}
		}
		
		String classicCompatibility = engineSettings.getProperty("classicCompatible");
		if (StringUtils.hasText(classicCompatibility)) {
			configuration.setClassicCompatible("true".equalsIgnoreCase(classicCompatibility) ? true : false);
		}
		
		String directives = engineSettings.getProperty("directives");
		if (StringUtils.hasText(directives)) {
			Map<String, String> directiveMap = ObjectUtil.splitPropString(directives);
			try {
				for (Map.Entry<String, String> directive : directiveMap.entrySet()) {
					configuration.setSharedVariable(directive.getKey(), ObjectUtil.instance(directive.getValue()));
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		
//		configuration.setTemplateExceptionHandler(new TemplateExceptionHandler() {
//			public void handleTemplateException(TemplateException te, Environment env, Writer writer) throws TemplateException {
//				if (te instanceof InvalidReferenceException) {
//					logger.debug(te.getMessage());
//					return;
//				}
//				throw te;
//			}
//		});
	}
	
	public Object process(String tpl, Object rootMap, Properties settings) {
		Template template = null;
		try {
			template = configuration.getTemplate(tpl);
			if (settings != null) {
				template.setSettings(settings);
			}
			
			StringWriter sw = new StringWriter();
			template.process(rootMap, sw);
			
			return sw.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}