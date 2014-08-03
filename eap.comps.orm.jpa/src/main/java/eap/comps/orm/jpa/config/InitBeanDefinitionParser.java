package eap.comps.orm.jpa.config;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.Assert;
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
public class InitBeanDefinitionParser implements BeanDefinitionParser {
	
	public static final String DEFAULT_ENTITY_MANAGER_ID = "eap_comps_orm_jpa_entityManager";
	public static final String DEFAULT_ENTITY_MANAGER_JDBC_TEMPLATE_ID = "eap_comps_orm_jpa_entityManager_jdbcTempalte";
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		String id = element.getAttribute("id");
		Assert.hasText(id, "'id' must not be empty");
		String basePackage = element.hasAttribute("basePackage") ? element.getAttribute("basePackage") : env.getProperty("app.basePackage");
		String modelPackage = element.hasAttribute("modelPackage") ? element.getAttribute("modelPackage") : env.getProperty(String.format("jpa.%s.modelPackage", id), basePackage);
		String repositoryImplPostfix = element.hasAttribute("repositoryImplPostfix") ? element.getAttribute("repositoryImplPostfix") : "";
		boolean defaultFlag = element.hasAttribute("default") ? Boolean.parseBoolean(element.getAttribute("default")) : true;
		
		String dataSourceId = element.getAttribute("dataSource");
		String entityManagerFactoryId = element.getAttribute("entityManagerFactoryId");
		String transactionManagerId = element.getAttribute("transactionManagerId");
		
		String dataSourceProxyId = entityManagerFactoryId + "_dataSource";
		RootBeanDefinition taspDef = new RootBeanDefinition(TransactionAwareDataSourceProxy.class);
		taspDef.setSource(source);
		taspDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		taspDef.getPropertyValues().add("targetDataSource", new RuntimeBeanReference(dataSourceId));
		parserContext.getRegistry().registerBeanDefinition(dataSourceProxyId, taspDef);
		parserContext.registerComponent(new BeanComponentDefinition(taspDef, dataSourceProxyId));
		
		RootBeanDefinition lcemfbDef = new RootBeanDefinition(LocalContainerEntityManagerFactoryBean.class);
		lcemfbDef.setSource(source);
		lcemfbDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		lcemfbDef.getPropertyValues().add("dataSource", new RuntimeBeanReference(dataSourceProxyId));
		lcemfbDef.getPropertyValues().add("packagesToScan", modelPackage);
		lcemfbDef.getPropertyValues().add("jpaDialect", new HibernateJpaDialect());
		lcemfbDef.getPropertyValues().add("jpaVendorAdapter", new HibernateJpaVendorAdapter());
		Map<String, Object> jpaProps = env.filterForPrefix(String.format("jpa.%s.props.", id));
		if (jpaProps != null && jpaProps.size() > 0) {
			if (!jpaProps.containsKey("hibernate.ejb.interceptor")) {
				jpaProps.put("hibernate.ejb.interceptor", "eap.comps.orm.jpa.BaseBOHandlerInterceptor");
			}
			lcemfbDef.getPropertyValues().add("jpaPropertyMap", jpaProps);
		}
		parserContext.getRegistry().registerBeanDefinition(entityManagerFactoryId, lcemfbDef);
		parserContext.registerComponent(new BeanComponentDefinition(lcemfbDef, entityManagerFactoryId));
		if (defaultFlag && !DEFAULT_ENTITY_MANAGER_ID.equals(entityManagerFactoryId)) {
			parserContext.getRegistry().registerAlias(entityManagerFactoryId, DEFAULT_ENTITY_MANAGER_ID);
//			parserContext.getRegistry().registerBeanDefinition(DEFAULT_ENTITY_MANAGER_ID, lcemfbDef);
//			parserContext.registerComponent(new BeanComponentDefinition(lcemfbDef, DEFAULT_ENTITY_MANAGER_ID));
			
			RootBeanDefinition jdbcTemplateDef = new RootBeanDefinition(JdbcTemplate.class);
			jdbcTemplateDef.setSource(source);
			jdbcTemplateDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			jdbcTemplateDef.getPropertyValues().add("dataSource", new RuntimeBeanReference(dataSourceProxyId));
			parserContext.getRegistry().registerBeanDefinition(DEFAULT_ENTITY_MANAGER_JDBC_TEMPLATE_ID, jdbcTemplateDef);
			parserContext.registerComponent(new BeanComponentDefinition(jdbcTemplateDef, DEFAULT_ENTITY_MANAGER_JDBC_TEMPLATE_ID));
		}
		
		if (transactionManagerId != null && transactionManagerId.length() > 0) {
			RootBeanDefinition jpaTransactionManagerDef = new RootBeanDefinition(JpaTransactionManager.class);
			jpaTransactionManagerDef.setSource(source);
			jpaTransactionManagerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			jpaTransactionManagerDef.getPropertyValues().add("entityManagerFactory", new RuntimeBeanReference(entityManagerFactoryId));
			parserContext.getRegistry().registerBeanDefinition(transactionManagerId, jpaTransactionManagerDef);
			parserContext.registerComponent(new BeanComponentDefinition(jpaTransactionManagerDef, transactionManagerId));
		}
		
		Document doc = DomUtil.newDocument();
		Element adcElement = doc.createElementNS("", "repositories");
		adcElement.setAttribute("base-package", basePackage);
		adcElement.setAttribute("repository-impl-postfix", repositoryImplPostfix);
		adcElement.setAttribute("entity-manager-factory-ref", entityManagerFactoryId);
		if (transactionManagerId != null && transactionManagerId.length() > 0) {
			adcElement.setAttribute("transaction-manager-ref", transactionManagerId);
		}
//		adcElement.setAttribute("query-lookup-strategy", "classpath*:dd/**/dao/impl/*DAO.properties");
		// base-package="" repository-impl-postfix="" entity-manager-factory-ref="" transaction-manager-ref="" query-lookup-strategy="" named-queries-location="" factory-class=""
		RepositoryConfigurationExtension extension = new JpaRepositoryConfigExtension();
		RepositoryBeanDefinitionParser repositoryBeanDefinitionParser = new RepositoryBeanDefinitionParser(extension);
		repositoryBeanDefinitionParser.parse(adcElement, parserContext);
		
		return null;
	}
}