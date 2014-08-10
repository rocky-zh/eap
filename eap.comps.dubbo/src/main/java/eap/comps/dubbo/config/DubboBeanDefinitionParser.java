package eap.comps.dubbo.config;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;

import eap.EapContext;
import eap.Env;
import eap.util.BeanUtil;
import eap.util.DomUtil;
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
 * @see com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler
 */
public class DubboBeanDefinitionParser implements BeanDefinitionParser {
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		Document doc = DomUtil.newDocument();
		Element appElement = doc.createElementNS("", "application"); // dubbo:application
		DomUtil.setElAttrs(appElement, env.filterForPrefix("dubbo.application."));
		if (!appElement.hasAttribute("name")) {
			appElement.setAttribute("name", env.getProperty("app.name"));
		}
		new com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser(ApplicationConfig.class, true).parse(appElement, parserContext).getPropertyValues().removePropertyValue("parameters");
		
		Map<String, Object> registrysProps = env.filterForPrefix("dubbo.protocols.");
		for (Map.Entry<String, Object> entry : registrysProps.entrySet()) {
			String key = entry.getKey();
			if (key.lastIndexOf(".") == -1) {
//				String value = (String) entry.getValue();
				
				doc = DomUtil.newDocument();
				Element proElement = doc.createElementNS("", "protocol"); // dubbo:protocol
				DomUtil.setElAttrs(proElement, BeanUtil.filterForPrefix(registrysProps, key + "."));
				new com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser(ProtocolConfig.class, true).parse(proElement, parserContext).getPropertyValues().removePropertyValue("parameters");
			}
		}
		
		doc = DomUtil.newDocument();
		Element registryElement = doc.createElementNS("", "registry"); // dubbo:registry
		DomUtil.setElAttrs(registryElement, env.filterForPrefix("dubbo.registry."));
		new com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser(RegistryConfig.class, true).parse(registryElement, parserContext).getPropertyValues().removePropertyValue("parameters");
		
		doc = DomUtil.newDocument();
		Element providerElement = doc.createElementNS("", "provider"); // <dubbo:provider
		DomUtil.setElAttrs(providerElement, env.filterForPrefix("dubbo.provider."));
		new com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser(ProviderConfig.class, true).parse(providerElement, parserContext).getPropertyValues().removePropertyValue("parameters");
		
		doc = DomUtil.newDocument();
		Element consumerElement = doc.createElementNS("", "consumer"); // dubbo:consumer
		DomUtil.setElAttrs(consumerElement, env.filterForPrefix("dubbo.consumer."));
		new com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser(ConsumerConfig.class, true).parse(consumerElement, parserContext).getPropertyValues().removePropertyValue("parameters");
		
		String pgk = env.getProperty("dubbo.annotation.package");
		if (StringUtil.isNotBlank(pgk)) {
			doc = DomUtil.newDocument();
			Element annotationElement = doc.createElementNS("", "annotation"); // dubbo:annotation
			annotationElement.setAttribute("package", pgk);
			new com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser(AnnotationBean.class, true).parse(annotationElement, parserContext).getPropertyValues().removePropertyValue("parameters");
		}
		
		return null;
	}
}