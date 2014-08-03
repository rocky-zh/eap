package eap.comps.cxf;

import java.util.Map;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.AbstractBasicInterceptorProvider;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import eap.util.CxfUtil;

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
public class JaxWsServerManager extends AbstractBasicInterceptorProvider implements ApplicationContextAware, InitializingBean, Ordered {
	
	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	private Bus bus;

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		bus = applicationContext.getBean(Bus.DEFAULT_BUS_ID, Bus.class);
		CxfUtil.setProxyAuthorization(bus);
		
		Map<String, Object> wsBeans = applicationContext.getBeansWithAnnotation(WebService.class);
		for (Map.Entry<String, Object> wsEntry : wsBeans.entrySet()) {
			String serviceName = wsEntry.getKey();
			Object serviceBean = wsEntry.getValue();
			Class serviceClass = wsEntry.getValue().getClass().getInterfaces()[0];
			String namespace = ((WebService) serviceClass.getAnnotation(WebService.class)).targetNamespace(); //serviceBean.getClass().getAnnotation(WebService.class).targetNamespace();
			
//			JaxWsServiceFactoryBean serviceFactoryBean = new JaxWsServiceFactoryBean();
//			WebServiceProviderConfiguration jaxWsConfiguration = new WebServiceProviderConfiguration();
//			jaxWsConfiguration.setServiceFactory(serviceFactoryBean);
//			jaxWsConfiguration.getInterfaceName()
//			serviceFactoryBean.getServiceConfigurations().add(0, jaxWsConfiguration);
//			serviceFactoryBean.setJaxWsImplementorInfo(new JaxWsImplementorInfo(ic))
//			JaxWsServerFactoryBean serverFactoryBean = new JaxWsServerFactoryBean(serviceFactoryBean);
			
			JaxWsServerFactoryBean serverFactoryBean = new JaxWsServerFactoryBean();
			serverFactoryBean.setAddress("/" + serviceName);
			serverFactoryBean.setServiceBean(serviceBean);
			serverFactoryBean.setServiceClass(serviceClass);
			serverFactoryBean.setServiceName(new QName(namespace, serviceName));
			serverFactoryBean.setEndpointName(new QName(namespace, serviceName + "Port"));
			
			serverFactoryBean.setInInterceptors(this.getInInterceptors());
			serverFactoryBean.setOutInterceptors(this.getOutInterceptors());
			serverFactoryBean.setInFaultInterceptors(this.getInFaultInterceptors());
			serverFactoryBean.setOutFaultInterceptors(this.getOutFaultInterceptors());
			
			serverFactoryBean.create();
		}
	}
}