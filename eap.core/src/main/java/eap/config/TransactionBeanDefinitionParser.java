package eap.config;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eap.EapContext;
import eap.Env;
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
 */
public class TransactionBeanDefinitionParser implements BeanDefinitionParser {
	
	private static final AtomicInteger COUNT = new AtomicInteger(0);

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Env env = EapContext.getEnv();
		
		Document doc = DomUtil.newDocument();
		
		String transactionManager = element.getAttribute("transactionManager");
		String[] required = StringUtil.split(element.hasAttribute("required") ? element.getAttribute("required") : env.getProperty("transaction.required"), ",");
		String[] requiresNew = StringUtil.split(element.hasAttribute("requiresNew") ? element.getAttribute("requiresNew") : env.getProperty("transaction.requiresNew", ""), ",");
		String[] pointcuts = StringUtil.split(element.getAttribute("pointcut"), ",");
		
		String adviceId = TransactionInterceptor.class.getName() + "##" + COUNT.getAndIncrement();
		Element adviceElement = doc.createElementNS("", "advice");
		adviceElement.setAttribute("id", adviceId);
		adviceElement.setAttribute("transaction-manager", transactionManager);
		Element attrsElement = doc.createElementNS("", "attributes");
		for (String methodName : required) {
			Element methodElement = doc.createElementNS("", "method");
			methodElement.setAttribute("name", methodName);
			methodElement.setAttribute("propagation", "REQUIRED");
			methodElement.setAttribute("isolation", "READ_COMMITTED");
			methodElement.setAttribute("rollback", "RollbackBizException");
			methodElement.setAttribute("no-rollback-for", "IgnoreBizException");
			attrsElement.appendChild(methodElement);
		}
		for (String methodName : requiresNew) {
			Element methodElement = doc.createElementNS("", "method");
			methodElement.setAttribute("name", methodName);
			methodElement.setAttribute("propagation", "REQUIRES_NEW");
			methodElement.setAttribute("isolation", "READ_COMMITTED");
			methodElement.setAttribute("rollback", "RollbackableBizException");
			methodElement.setAttribute("no-rollback-for", "IgnoreBizException");
			attrsElement.appendChild(methodElement);
		}
		Element methodElement = doc.createElementNS("", "method");
		methodElement.setAttribute("name", "*");
		methodElement.setAttribute("propagation", "REQUIRED");
		methodElement.setAttribute("read-only", "true");
		attrsElement.appendChild(methodElement);
		adviceElement.appendChild(attrsElement);
		new TxAdviceBeanDefinitionParser().parse(adviceElement, parserContext);
		
		if (pointcuts != null && pointcuts.length > 0) {
			for (String pointcut : pointcuts) {
				Element configElement = doc.createElementNS("", "config");
				String pointcutId = AspectJExpressionPointcut.class.getName() + "##" + COUNT.getAndIncrement();
				Element pointcutElement = doc.createElementNS("", "pointcut");
				pointcutElement.setAttribute("id", pointcutId);
				pointcutElement.setAttribute("expression", pointcut);
				configElement.appendChild(pointcutElement);
				Element advisorElement = doc.createElementNS("", "advisor");
				advisorElement.setAttribute("advice-ref", adviceId);
				advisorElement.setAttribute("pointcut-ref", pointcutId);
				configElement.appendChild(advisorElement);
				new ConfigBeanDefinitionParser().parse(configElement, parserContext);
			}
		}
		
		return null;
	}
}