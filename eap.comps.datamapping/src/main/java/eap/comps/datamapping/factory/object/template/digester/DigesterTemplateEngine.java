package eap.comps.datamapping.factory.object.template.digester;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

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
public class DigesterTemplateEngine implements ITemplateEngine {
	
	private String templateLoaderPath;
	
	public void initEngineSettings(Properties engineSettings) {
		templateLoaderPath = engineSettings.getProperty("templateLoaderPath");
	}

	public Object process(String tplPath, Object input, Properties settings) {
		if (StringUtils.hasText(templateLoaderPath)) {
			tplPath = templateLoaderPath + tplPath;
		}
		
		Digester digester = this.createDigester(tplPath, settings);
		digester.setValidating(false);
		digester.setUseContextClassLoader(true);
		
		try {
			InputStream inputStream = (InputStream) input;
			return digester.parse(inputStream);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	private static Map<String, Digester> digesterCache = new ConcurrentHashMap<String, Digester>();
	private Digester createDigester(String tplPath, Properties settings) {
		if (digesterCache.containsKey(tplPath)) { // TODO 非线程安全， 该类仅用于datamapping规则文件单线程串行解析
			return digesterCache.get(tplPath);
		}
		
		Resource tplRes = new ClassPathResource(tplPath);
		Digester digester = null;
		try {
			digester = DigesterLoader.createDigester(tplRes.getURL());
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		digesterCache.put(tplPath, digester);
		return digester;
	}
}