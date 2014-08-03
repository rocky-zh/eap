package eap.comps.datamapping.factory.object.template.oxm;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.oxm.support.AbstractMarshaller;
import org.springframework.xml.transform.StringResult;

import eap.EapContext;
import eap.Env;
import eap.comps.datamapping.factory.object.template.ITemplateEngine;
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
public class OxmTemplateEngine implements ITemplateEngine {
	
	public static final String ACTION_MARSHAL = "marshal";
	public static final String ACTION_UNMARSHAL = "unmarshal";
	
	private Map<String, AbstractMarshaller> marshallers = new ConcurrentHashMap<String, AbstractMarshaller>();
	private final Object buildMarshallerMonitor = new Object(); // ReentrantLock
	
	private String templateLoaderPath;
	
	@Override
	public void initEngineSettings(Properties engineSettings) {
		templateLoaderPath = engineSettings.getProperty("templateLoaderPath");
	}
	
	@Override
	public Object process(String tplPath, Object data, Properties settings) {
		if (StringUtil.isNotBlank(templateLoaderPath)) {
			tplPath = templateLoaderPath + tplPath;
		}
		
		AbstractMarshaller marshaller = this.getMarshaller(tplPath, settings);
		
		String action = settings.getProperty("action");
		if (StringUtil.isNotBlank(action) && action.equalsIgnoreCase(ACTION_MARSHAL)) {
			return this.doMarshal(tplPath, data, settings, marshaller);
		} 
		else {
			return this.doUnmarshal(tplPath, data, settings, marshaller);
		}
	}
	
	private AbstractMarshaller getMarshaller(String mappingPath, Properties config) {
		return this.getCastorMarshaller(mappingPath, config);
	}
	
	private AbstractMarshaller getCastorMarshaller(String mappingPath, Properties config) {
		String marshallerName = mappingPath;
		AbstractMarshaller marshaller = marshallers.get(marshallerName);
		synchronized (buildMarshallerMonitor) {
			if (marshaller == null) {
				Env env = EapContext.getEnv();
				CastorMarshaller castorMarshaller = null;
				try {
					castorMarshaller = new CastorMarshaller();
//					castorMarshaller.setEncoding(Env.getInstance().getEncoding());
					castorMarshaller.setEncoding(config.getProperty("encoding", env.getEncoding()));
					castorMarshaller.setIgnoreExtraAttributes(true);
					castorMarshaller.setIgnoreExtraElements(true);
					castorMarshaller.setWhitespacePreserve(false);
					castorMarshaller.setSuppressNamespaces(false);
					castorMarshaller.setSuppressXsiType(false);
					
					String[] includeMappings = StringUtil.split(config.getProperty("includeMappings", ""), ',');
					Resource[] mappingLocations = new Resource[includeMappings.length + 1];
					for (int i = 0; i < includeMappings.length; i++) {
						mappingLocations[i] = new ClassPathResource(templateLoaderPath + includeMappings[i]);
					}
					mappingLocations[mappingLocations.length - 1] = new ClassPathResource(mappingPath);
					castorMarshaller.setMappingLocations(mappingLocations);
					
					castorMarshaller.afterPropertiesSet();
				} catch (Exception e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
				
				marshaller = castorMarshaller;
				marshallers.put(marshallerName, marshaller);
			}
		}
		
		return marshaller;
	}
	
	private Object doMarshal(String tplPath, Object data, Properties settings, AbstractMarshaller marshaller) {
		try {
			StringResult result = new StringResult();
			marshaller.marshal(data, result);
			
			return result.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	private Object doUnmarshal(String tplPath, Object data, Properties settings, AbstractMarshaller marshaller) {
		try {
			return marshaller.unmarshal(new StreamSource((InputStream) data));
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
}