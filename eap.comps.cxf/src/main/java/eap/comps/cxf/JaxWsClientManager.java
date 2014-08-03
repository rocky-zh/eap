package eap.comps.cxf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import eap.EapContext;
import eap.Env;
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
public class JaxWsClientManager {
	
	private static Map<String, Object> serviceCache = new ConcurrentHashMap<String, Object>();
	
	public static Object getService(String serviceClassName) {
		Class serviceClass = null;
		try {
			serviceClass = Class.forName(serviceClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		return getService(serviceClass);
		
	}
	public static <T> T getService(Class<T> serviceClass) {
		String serviceClassName = serviceClass.getName();
		
		Object service = serviceCache.get(serviceClassName);
		if (service == null) {
			synchronized (serviceClass) {
				service = createService(serviceClass);
				serviceCache.put(serviceClassName, service);
			}
		}
		
		return (T) service;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T createService(Class<T> serviceClass) {
		Env env = EapContext.getEnv();
		
		String serviceClassName = serviceClass.getName();
		
		JaxWsProxyFactoryBean pfb = new JaxWsProxyFactoryBean();
		pfb.setServiceClass(serviceClass);
		
		String address = env.getProperty(String.format("webService.%s.Address", serviceClassName));
		if (StringUtil.isBlank(address)) {
			throw new IllegalArgumentException("not found webService address, " + serviceClassName);
		}
		pfb.setAddress(address);
		
		String namespace = env.getProperty(String.format("webService.%s.Namespace", serviceClassName));
		if (StringUtil.isBlank(namespace)) {
			WebService wsa = serviceClass.getAnnotation(WebService.class);
			if (wsa != null) {
				namespace = wsa.targetNamespace();
			}
		}
		String serviceName = env.getProperty(String.format("webService.%s.ServiceName", serviceClassName));
		if (StringUtil.isNotBlank(namespace) && StringUtil.isNotBlank(serviceName)) {
			pfb.setServiceName(new QName(namespace, serviceName));
		}

		T service =  (T) pfb.create();
		
		return service;
	}
	
	public static void removeService(String serviceClassName) {
		serviceCache.remove(serviceClassName);
	}
	public static void removeAllService() {
		serviceCache.clear();
	}
}