package eap.util;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
public class AntiSamyUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(AntiSamyUtil.class);
	
	private static AntiSamy antiSamy =null;
	
	public static String getCleanHtml(String html) {
		if (antiSamy == null) {
			init();
		}
		
		CleanResults results = null;
		try {
			results = antiSamy.scan(html);//"<a href='javascript:alert(1)'>123</a><script type='text/javascript'>alert(1);</script>456");
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		return results.getCleanHTML();
	}
	
	private static void init() {
		try {
			Env env = EapContext.getEnv();
			
			String policyPath= env.getProperty("app.conf.antiSamy.policy.path", "antisamy_ebay_1.4.4.xml");
			Resource resource = new ClassPathResource(policyPath);
			Policy policy = Policy.getInstance(resource.getInputStream());
			
			antiSamy = new AntiSamy();
			antiSamy.setPolicy(policy);
			antiSamy.setInputEncoding(env.getEncoding());
			antiSamy.setOutputEncoding(env.getEncoding());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String s = AntiSamyUtil.getCleanHtml("<a href='javascript:alert(1)'>123</a><script type='text/javascript'>alert(1);</script>456");
		System.out.println(s);
	}
}