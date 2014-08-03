package eap.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.ClassPathResource;

import eap.EapContext;

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
public class MessageUtil { // TODO htmlEscape
	
	private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);
	
	private static MessageSource messageSource;
	
	private static Map<String, Map<String, Properties>> cacheProperties = new ConcurrentHashMap<String, Map<String, Properties>>(); // ICache   new ConcurrentHashMap
	
	public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		checkInit();
		try {
			return messageSource.getMessage(code, args, defaultMessage, locale);
		} catch (NoSuchMessageException e) {
			return null;
		}
	}
	
	public static String getMessage(String code, Object[] args, String defaultMessage) {
		checkInit();
		try {
			return messageSource.getMessage(code, args, defaultMessage, EapContext.getLocale());
		} catch (NoSuchMessageException e) {
			return null;
		}
	}
	
	public static String getMessage(String code, Object[] args, Locale locale) {
		checkInit();
		try {
			return messageSource.getMessage(code, args, locale);
		} catch (NoSuchMessageException e) {
			return null;
		}
	}
	
	public static String getMessage(String code, Object[] args) {
		checkInit();
		try {
			return messageSource.getMessage(code, args, EapContext.getLocale());
		} catch (NoSuchMessageException e) {
			return null;
		}
	}
	public static String getMessage(String code) {
		checkInit();
		try {
			return messageSource.getMessage(code, null, EapContext.getLocale());
		} catch (NoSuchMessageException e) {
			return null;
		}
	}
	
	public static Properties loadProperties(String classPathDir, String baseName, String locale) { // allow null  concurrent
		Map<String, Properties> localeProps = cacheProperties.get(baseName);
		if (localeProps == null) {
			localeProps = new HashMap<String, Properties>();
			cacheProperties.put(baseName, localeProps);
		}
		Properties props = localeProps.get(locale);
		if (props == null) {
			props = new Properties();
			InputStream propsInputStream = null;
			try {
				propsInputStream = new ClassPathResource(String.format(classPathDir + "/%s%s.properties", baseName, StringUtils.isNotBlank(locale) ? "_" + locale : "")).getInputStream();
				props.load(propsInputStream);
			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
				throw new IllegalArgumentException(e.getMessage(), e);
			} finally {
				if (propsInputStream != null) {
					try {
						propsInputStream.close();
					} catch (IOException e) {
					}
				}
			}
			localeProps.put(locale, props);
		}
		
		return localeProps.get(locale);
	}
	
	private static void checkInit() {
		if (messageSource == null) {
			throw new IllegalStateException("messageSource not initialized");
		}
	}
	
	public void setMessageSource(MessageSource ms) {
		messageSource = ms;
	}
	public static MessageSource getMessageSource() {
		return messageSource;
	}
}