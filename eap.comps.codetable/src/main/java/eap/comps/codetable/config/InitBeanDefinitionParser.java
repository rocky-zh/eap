package eap.comps.codetable.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import eap.comps.codetable.CodeTable;

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
public class InitBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	@Override
	protected Class<?> getBeanClass(Element element) {
		return CodeTable.class;
	}
	
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		element.setAttribute("id", "eap_comps_codeTable");
		
		String jdbcTemplateId = element.getAttribute("jdbcTemplate");
		
		builder.setInitMethodName("afterPropertiesSet");
		builder.addPropertyValue("defaultUseCollector", "DEFAULT"); // TODO
		builder.addPropertyReference("jdbcTemplate", jdbcTemplateId);
	}
}