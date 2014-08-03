package eap.comps.httpclient;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.springframework.util.Assert;

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
public class HttpClient extends org.apache.commons.httpclient.HttpClient {
	
	public HttpClient() {
		super();
		setNetProxy();
	}
	public HttpClient(HttpClientParams params) {
		super(params);
		setNetProxy();
	}
	public HttpClient(HttpClientParams params, HttpConnectionManager httpConnectionManager) {
		super(params, httpConnectionManager);
		setNetProxy();
	}
	public HttpClient(HttpConnectionManager httpConnectionManager) {
		super(httpConnectionManager);
		setNetProxy();
	}
	
	private void setNetProxy() {
		Env env = EapContext.getEnv();
		
		boolean proxy = Boolean.parseBoolean(env.getProperty("app.proxy", "false"));
		if (proxy) {
			String proxyHost = env.getProperty("app.proxy.host");
			Assert.hasText(proxyHost, "Env[app.proxy.host] must not be empty");
			Integer proxyPort = Integer.valueOf(env.getProperty("app.proxy.port", "0"));
			Assert.isTrue((proxyPort != null && proxyPort > 0), "Env[app.proxy.port] must not be empty");
			this.getHostConfiguration().setProxy(proxyHost, proxyPort);
			
			this.getParams().setAuthenticationPreemptive(Boolean.parseBoolean(env.getProperty("app.proxy.preemptive", "true")));
			
			String proxyUsername = env.getProperty("app.proxy.username");
			if (StringUtil.isNotBlank(proxyUsername)) {
				String proxyPassword = env.getProperty("app.proxy.password");
				this.getState().setProxyCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
			}
		}
	}
}