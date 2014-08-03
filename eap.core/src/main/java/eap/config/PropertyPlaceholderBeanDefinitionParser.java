package eap.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.spring31.properties.EncryptablePropertyPlaceholderConfigurer;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import eap.EapContext;
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
public class PropertyPlaceholderBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
		return element.hasAttribute("id") ? element.getAttribute("id") : "propertyConfigurer";
	}
	
	@Override
	protected Class<?> getBeanClass(Element element) {
		return EncryptablePropertyPlaceholderConfigurer.class;
	}
	
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		// algorithm, passwordSysPropertyName, locations, fileEncoding
		String algorithm = element.hasAttribute("algorithm") ? element.getAttribute("algorithm") : "PBEWithMD5AndDES";
		String passwordSysPropertyName = element.hasAttribute("passwordSysPropertyName") ? element.getAttribute("passwordSysPropertyName") : "app.password";
		String locations = element.hasAttribute("locations") ? element.getAttribute("locations") : "classpath*:eap_env.properties,classpath:env.properties";
		String fileEncoding = element.hasAttribute("fileEncoding") ? element.getAttribute("fileEncoding") : EapContext.getEnv().getEncoding();
		
		EnvironmentStringPBEConfig environmentStringPBEConfig = new EnvironmentStringPBEConfig();
		environmentStringPBEConfig.setAlgorithm(algorithm);
		environmentStringPBEConfig.setPasswordSysPropertyName(passwordSysPropertyName);
		
		StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
		standardPBEStringEncryptor.setConfig(environmentStringPBEConfig);
		
		builder.addConstructorArgValue(standardPBEStringEncryptor);
		builder.addPropertyValue("locations", StringUtil.split(locations, ","));
		builder.addPropertyValue("fileEncoding", fileEncoding);
	}
}