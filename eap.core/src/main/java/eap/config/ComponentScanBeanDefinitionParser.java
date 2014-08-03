package eap.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eap.EapContext;
import eap.Env;
import eap.util.DomUtil;

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
public class ComponentScanBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Env env = EapContext.getEnv();
		Document doc = DomUtil.newDocument();
		
		String basePackage = element.hasAttribute("basePackage") ? element.getAttribute("basePackage") : env.getProperty("app.basePackage");
		
		Element csElement = doc.createElementNS("", "component-scan");
		csElement.setAttribute("base-package", basePackage);
		Boolean isController = element.hasAttribute("isController") ? Boolean.parseBoolean(element.getAttribute("isController")) : false;
		Element fElement = doc.createElementNS("", isController ? "include-filter" : "exclude-filter");
		fElement.setAttribute("type", "annotation");
		fElement.setAttribute("expression", "org.springframework.stereotype.Controller");
		csElement.appendChild(fElement);
 		new org.springframework.context.annotation.ComponentScanBeanDefinitionParser().parse(csElement, parserContext);
 		
		return null;
	}
}