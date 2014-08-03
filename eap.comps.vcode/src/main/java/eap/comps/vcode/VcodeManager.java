package eap.comps.vcode;

import java.awt.image.BufferedImage;

import javax.servlet.http.HttpSession;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import eap.EapContext;
import eap.Env;
import eap.util.PropertiesUtil;
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
public class VcodeManager {
	
	public static final String SESSION_VCODE_KEY = "__vcode";
	
	private static DefaultKaptcha vcodeProducer;
	
	public static String createText() {
		return getVcodeProducer().createText();
	}
	
	public static BufferedImage createImage() {
		return getVcodeProducer().createImage(createText());
	}
	
	public static BufferedImage createImage(String text) {
		return getVcodeProducer().createImage(text);
	}
	
	public static BufferedImage createImage(String text, HttpSession session) {
		BufferedImage img = getVcodeProducer().createImage(text);
		session.setAttribute(SESSION_VCODE_KEY, text);
		return img;
	}
	
	public static boolean isValid(String text, HttpSession session) {
		if (StringUtil.isBlank(text)) {
			return false;
		}
		
		return text.equalsIgnoreCase((String)session.getAttribute(SESSION_VCODE_KEY));
	}
	public static boolean pass(String text, HttpSession session) {
		boolean result = isValid(text, session);
		if (result) {
			session.removeAttribute(SESSION_VCODE_KEY);
		}
		
		return result;
	}
	
	private static Producer getVcodeProducer() {
		if (vcodeProducer == null) {
			Env env = EapContext.getEnv();
			
			vcodeProducer = new DefaultKaptcha();
			vcodeProducer.setConfig(new Config(PropertiesUtil.from(env.filterForPrefix("vcode.config."))));
		}
		
		return vcodeProducer;
	}
	
	public static void main(String[] args) {
		String text = createText();
		System.out.println(text);
	}
}
