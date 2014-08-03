package eap.util;

import java.io.IOException;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapTransportFactory;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.springframework.util.Assert;

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
public class CxfUtil  {
	
	public static void setProxyAuthorization(Bus bus) {
		ConduitInitiatorManager mgr = bus.getExtension(ConduitInitiatorManager.class);
		HTTPTransportFactory httpTransportFactory = new HTTPTransportFactory(bus) {
			@Override
			public Conduit getConduit(EndpointInfo endpointInfo, EndpointReferenceType target) throws IOException {
				return setProxyHttpConduit(super.getConduit(endpointInfo, target));
			}
		};
		for (String ns : HTTPTransportFactory.DEFAULT_NAMESPACES) {
			mgr.registerConduitInitiator(ns, httpTransportFactory);
		}
		SoapTransportFactory soapTransportFactory = new SoapTransportFactory(bus) {
			public Conduit getConduit(EndpointInfo ei, EndpointReferenceType target) throws IOException {
				return setProxyHttpConduit(super.getConduit(ei, target));
			};
		};
		for (String ns : SoapTransportFactory.DEFAULT_NAMESPACES) {
			mgr.registerConduitInitiator(ns, soapTransportFactory);
		}
	}
	
	private static Conduit setProxyHttpConduit(Conduit conduit) {
		if (conduit instanceof HTTPConduit) {
			HTTPConduit httpConduit = (HTTPConduit) conduit;
			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
			
			Env env = EapContext.getEnv();
			boolean proxy = Boolean.parseBoolean(env.getProperty("app.proxy", "false"));
			if (proxy) {
				String proxyHost = env.getProperty("app.proxy.host");
				Assert.hasText(proxyHost, "Env[proxyHost] must not be empty");
				Integer proxyPort = Integer.valueOf(env.getProperty("app.proxy.port", "0"));
				Assert.isTrue((proxyPort != null && proxyPort > 0), "Env[proxyPort] must not be empty");
				httpClientPolicy.setProxyServerType(ProxyServerType.HTTP);
				httpClientPolicy.setProxyServer(proxyHost);
				httpClientPolicy.setProxyServerPort(proxyPort);
				httpClientPolicy.setConnectionTimeout(Long.valueOf(env.getProperty("app.net.connectionTimeout", "15000")));
				httpClientPolicy.setReceiveTimeout(Long.valueOf(env.getProperty("app.net.receiveTimeout", "30000")));
				httpConduit.setClient(httpClientPolicy);
				
				String proxyUsername = env.getProperty("app.proxy.username");
				if (StringUtil.isNotBlank(proxyUsername)) {
					String proxyPassword = env.getProperty("app.proxy.password");
					httpConduit.getProxyAuthorization().setUserName(proxyUsername);
					httpConduit.getProxyAuthorization().setPassword(proxyPassword);
				}
			}
		}
		
		return conduit;
	}
}