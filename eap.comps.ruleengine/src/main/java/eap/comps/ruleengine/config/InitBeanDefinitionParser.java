package eap.comps.ruleengine.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import eap.EapContext;
import eap.Env;
import eap.comps.ruleengine.drools.DroolsRuleEngineImpl;
import eap.util.PropertiesUtil;

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
public class InitBeanDefinitionParser implements BeanDefinitionParser {
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		String id = element.getAttribute("id");
		Assert.hasText(id, "'id' must not be empty");
		String engine = element.hasAttribute("engine") ? element.getAttribute("engine") : env.getProperty(String.format("ruleengine.%s.engine", id), "drools");
		
		if ("drools".equals(engine)) {
			RootBeanDefinition droolsRuleEngineDef = new RootBeanDefinition(DroolsRuleEngineImpl.class);
			droolsRuleEngineDef.setSource(source);
			droolsRuleEngineDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			droolsRuleEngineDef.getPropertyValues().add("config", PropertiesUtil.from(env.filterForPrefix(String.format("ruleengine.%s.", id))));
			droolsRuleEngineDef.setInitMethodName("init");
			parserContext.getRegistry().registerBeanDefinition(id, droolsRuleEngineDef);
			parserContext.registerComponent(new BeanComponentDefinition(droolsRuleEngineDef, id));
		} else {
			throw new IllegalArgumentException(engine + " RuleEngine not found");
		}
		
		return null;
	}
}