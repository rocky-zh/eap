package eap.comps.orm.mybatis.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import eap.EapContext;
import eap.Env;
import eap.comps.orm.mybatis.BaseBOHandlerInterceptor;
import eap.comps.orm.mybatis.QueryScopeStatementHandlerInterceptor;
import eap.comps.orm.mybatis.RowBoundsResultSetHandlerInterceptor;
import eap.comps.orm.mybatis.RowBoundsStatementHandlerInterceptor;
import eap.comps.orm.mybatis.SqlExecutorImpl;
import eap.comps.orm.mybatis.SqlSessionFactoryBean;

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
	
	public static final String DEFAULT_SQL_EXECUTOR_ID = "eap_comps_orm_mybatis_sqlExecutor";
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		Env env = EapContext.getEnv();
		
		String id = element.getAttribute("id");
		Assert.hasText(id, "'id' must not be empty");
		String dataSource = element.hasAttribute("dataSource") ? element.getAttribute("dataSource") : env.getProperty(String.format("mybaits.%s.dataSource", id));
		String configLocation = element.hasAttribute("configLocation") ? element.getAttribute("configLocation") : env.getProperty(String.format("mybaits.%s.configLocation", id), env.getProperty("mybaits.configLocation"));
		String modelPackage = element.hasAttribute("modelPackage") ? element.getAttribute("modelPackage") : env.getProperty(String.format("mybaits.%s.modelPackage", id), env.getProperty("app.basePackage"));
		String mapperLocations = element.hasAttribute("mapperLocations") ? element.getAttribute("mapperLocations") : env.getProperty(String.format("mybaits.%s.mapperLocations", id));
		boolean defaultFlag = element.hasAttribute("default") ? Boolean.parseBoolean(element.getAttribute("default")) : true;
		
		String sqlSessionFactoryId = element.getAttribute("sqlSessionFactoryId");
		String sqlExecutorId = element.getAttribute("sqlExecutorId");
		
		RootBeanDefinition ssfbDef = new RootBeanDefinition(SqlSessionFactoryBean.class);
		if (sqlSessionFactoryId == null || sqlSessionFactoryId.length() == 0) {
			sqlSessionFactoryId = parserContext.getReaderContext().generateBeanName(ssfbDef);
		}
		ssfbDef.setSource(source);
		ssfbDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		ssfbDef.getPropertyValues()
			.add("dataSource", new RuntimeBeanReference(dataSource))
			.add("configLocation", configLocation)
			.add("typeAliasesPackage", modelPackage);
		if (mapperLocations != null && mapperLocations.length() > 0) {
//			String[] a = new String[2];
//			a[0] = "classpath:eap/web/demo/bizprocess/**/dao/impl/*DAOImpl.xml";
//			a[1] = "classpath:eap/web/demo/bizsupport/**/dao/impl/*DAOImpl.xml";
			ssfbDef.getPropertyValues().add("mapperLocations", mapperLocations.split(","));
		}
		List<Object> plugins = new ArrayList<Object>();
		plugins.add(new BaseBOHandlerInterceptor());
		plugins.add(new RowBoundsStatementHandlerInterceptor());
		plugins.add(new QueryScopeStatementHandlerInterceptor());
		plugins.add(new RowBoundsResultSetHandlerInterceptor());
		ssfbDef.getPropertyValues().add("plugins", plugins);
		parserContext.getRegistry().registerBeanDefinition(sqlSessionFactoryId, ssfbDef);
		parserContext.registerComponent(new BeanComponentDefinition(ssfbDef, sqlSessionFactoryId));
		
		RootBeanDefinition sqlExecutorDef = new RootBeanDefinition(SqlExecutorImpl.class);
		if (sqlExecutorId == null || sqlExecutorId.length() == 0) {
			sqlExecutorId = parserContext.getReaderContext().generateBeanName(sqlExecutorDef);
		}
		sqlExecutorDef.setSource(source);
		sqlExecutorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		sqlExecutorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference(sqlSessionFactoryId));
		parserContext.getRegistry().registerBeanDefinition(sqlExecutorId, sqlExecutorDef);
		parserContext.registerComponent(new BeanComponentDefinition(sqlExecutorDef, sqlExecutorId));
		if (defaultFlag && !DEFAULT_SQL_EXECUTOR_ID.equals(sqlExecutorId)) {
			parserContext.getRegistry().registerAlias(sqlExecutorId, DEFAULT_SQL_EXECUTOR_ID);
//			parserContext.getRegistry().registerBeanDefinition(DEFAULT_SQL_EXECUTOR_ID, sqlExecutorDef);
//			parserContext.registerComponent(new BeanComponentDefinition(sqlExecutorDef, DEFAULT_SQL_EXECUTOR_ID));
		}
		
		return null;
	}
}