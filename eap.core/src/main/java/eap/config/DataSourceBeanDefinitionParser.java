package eap.config;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.w3c.dom.Element;

import com.alibaba.druid.pool.DruidDataSource;

import eap.EapContext;
import eap.Env;

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
public class DataSourceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	@Override
	protected Class<?> getBeanClass(Element element) {
		return DruidDataSource.class;
	}
	
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		String dataSourceId = element.getAttribute("id");
		String dataSourcePropsFormat = element.hasAttribute("dataSourcePropsFormat") ? element.getAttribute("dataSourcePropsFormat") : "dataSource.%s.props.";
		String jdbcTemplateId = element.getAttribute("jdbcTemplateId");
		String transactionManagerId = element.getAttribute("transactionManagerId");

		builder.setInitMethodName("init");
		builder.setDestroyMethodName("close");
		Map<String, Object> dataSourceProps = env.filterForPrefix(String.format(dataSourcePropsFormat, dataSourceId));
		// TODO check 
		for (Map.Entry<String, Object> dataSourceProp : dataSourceProps.entrySet()) {
			builder.addPropertyValue(dataSourceProp.getKey(), dataSourceProp.getValue());
		}
		
		if (StringUtils.isNotBlank(jdbcTemplateId)) {
			RootBeanDefinition jdbcTemplateDef = new RootBeanDefinition(JdbcTemplate.class);
			jdbcTemplateDef.setSource(source);
			jdbcTemplateDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			jdbcTemplateDef.getPropertyValues().add("dataSource", new RuntimeBeanReference(dataSourceId));
			parserContext.getRegistry().registerBeanDefinition(jdbcTemplateId, jdbcTemplateDef);
			parserContext.registerComponent(new BeanComponentDefinition(jdbcTemplateDef, jdbcTemplateId));
		}
		
		if (StringUtils.isNotBlank(transactionManagerId)) {
			RootBeanDefinition dataSourceTransactionManagerDef = new RootBeanDefinition(DataSourceTransactionManager.class);
			dataSourceTransactionManagerDef.setSource(source);
			dataSourceTransactionManagerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			dataSourceTransactionManagerDef.getPropertyValues().add("dataSource", new RuntimeBeanReference(dataSourceId));
			parserContext.getRegistry().registerBeanDefinition(transactionManagerId, dataSourceTransactionManagerDef);
			parserContext.registerComponent(new BeanComponentDefinition(dataSourceTransactionManagerDef, transactionManagerId));
		}
	}
}