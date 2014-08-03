package eap.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.w3c.dom.Element;

import eap.EapContext;
import eap.WebEnv;
import eap.util.propertyeditor.BigDecimalEditor;
import eap.util.propertyeditor.DateEditor;
import eap.util.propertyeditor.HtmlEscapePropertyEditor;
import eap.web.ConfigurableWebBindingInitializer;
import eap.web.HandlerExceptionResolver;
import eap.web.InternalResourcePackageViewResolver;
import eap.web.RequestToViewNameTranslator;
import eap.web.WebArgumentResolver;
import eap.web.interceptor.TokenSessionStoreInterceptor;
import eap.web.interceptor.WebEventsHandlerInterceptor;
import eap.web.json.JsonHttpMessageConverter;

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
public class MvcBeanDefinitionParser implements BeanDefinitionParser {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		WebEnv env = (WebEnv) EapContext.getEnv();
		
		/* localeResolver */
		if (Boolean.parseBoolean(env.getProperty("app.locale.cookie", "false"))) {
			RootBeanDefinition localeResolverDef = new RootBeanDefinition(CookieLocaleResolver.class);
			String localeResolverId = "localeResolver";
			localeResolverDef.setSource(source);
			localeResolverDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			localeResolverDef.getPropertyValues().add("defaultLocale", env.getProperty("app.locale", "zh_CN"));
			localeResolverDef.getPropertyValues().add("cookieName", env.getProperty("app.locale.cookie.name", "locale"));
			localeResolverDef.getPropertyValues().add("cookieMaxAge", env.getProperty("app.locale.cookie.maxAgeSeconds", "7776000")); // 60 * 60 * 24 * 90 = 90day
			parserContext.getRegistry().registerBeanDefinition(localeResolverId, localeResolverDef);
			parserContext.registerComponent(new BeanComponentDefinition(localeResolverDef, localeResolverId));
		}
		
		/* viewNameTranslator */
		RootBeanDefinition viewNameTranslatorDef = new RootBeanDefinition(RequestToViewNameTranslator.class);
		String viewNameTranslatorId = "viewNameTranslator";
		viewNameTranslatorDef.setSource(source);
		viewNameTranslatorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(viewNameTranslatorId, viewNameTranslatorDef);
		parserContext.registerComponent(new BeanComponentDefinition(viewNameTranslatorDef, viewNameTranslatorId));
		
		/* handlerExceptionResolver */
		RootBeanDefinition handlerExceptionResolverDef = new RootBeanDefinition(HandlerExceptionResolver.class);
		String handlerExceptionResolverId = parserContext.getReaderContext().generateBeanName(handlerExceptionResolverDef);
		handlerExceptionResolverDef.setSource(source);
		handlerExceptionResolverDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(handlerExceptionResolverId, handlerExceptionResolverDef);
		parserContext.registerComponent(new BeanComponentDefinition(handlerExceptionResolverDef, handlerExceptionResolverId));
		
		/* viewResolver */
		RootBeanDefinition viewResolverDef = new RootBeanDefinition(InternalResourcePackageViewResolver.class);
		String viewResolverId = "viewResolver";
		viewResolverDef.setSource(source);
		viewResolverDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		viewResolverDef.getPropertyValues().add("viewClass", "org.springframework.web.servlet.view.JstlView");
//		viewResolverDef.getPropertyValues().add("prefix", env.getViewPrefix());
//		viewResolverDef.getPropertyValues().add("suffix", env.getViewSuffix());
		parserContext.getRegistry().registerBeanDefinition(viewResolverId, viewResolverDef);
		parserContext.registerComponent(new BeanComponentDefinition(viewResolverDef, viewResolverId));
		
		/* multipartResolver */
		RootBeanDefinition multipartResolverDef = new RootBeanDefinition(CommonsMultipartResolver.class);
		String multipartResolverId = "multipartResolver";
		multipartResolverDef.setSource(source);
		multipartResolverDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		multipartResolverDef.getPropertyValues().add("defaultEncoding", env.getEncoding());
		multipartResolverDef.getPropertyValues().add("maxUploadSize", env.getProperty("app.web.form.upload.maxUploadSize", "1048576")); // 1024 * 1024 * 1 = 1M
		multipartResolverDef.getPropertyValues().add("maxInMemorySize", env.getProperty("app.web.form.upload.maxInMemorySize", "4096")); // 1024 * 4 = 4KB
		parserContext.getRegistry().registerBeanDefinition(multipartResolverId, multipartResolverDef);
		parserContext.registerComponent(new BeanComponentDefinition(multipartResolverDef, multipartResolverId));
		
		
		boolean useAnnotation = element.hasAttribute("useAnnotation") ? new Boolean(element.getAttribute("useAnnotation")) : true;
		
		/* handlerMapping */
		RootBeanDefinition handlerMappingDef = new RootBeanDefinition(useAnnotation ? DefaultAnnotationHandlerMapping.class : RequestMappingHandlerMapping.class);
		String annotationHandlerMappingId = parserContext.getReaderContext().generateBeanName(handlerMappingDef);
		handlerMappingDef.setSource(source);
		handlerMappingDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		
		List interceptors = new ArrayList();
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName(env.getProperty("app.locale.name"));
		interceptors.add(localeChangeInterceptor);
		if (env.isFormTokenEnable()) {
			TokenSessionStoreInterceptor tokenSessionStoreInterceptor = new TokenSessionStoreInterceptor();
			tokenSessionStoreInterceptor.setExcludeUrlPatterns(env.getProperty("app.web.form.token.excludeUrlPatterns"));
			interceptors.add(tokenSessionStoreInterceptor);
		}
		interceptors.add(new WebEventsHandlerInterceptor());
		interceptors.add(new InternalResourcePackageViewResolver()); // interceptors.add(new RuntimeBeanReference(viewResolverId));
		handlerMappingDef.getPropertyValues().add("interceptors", interceptors);
		
		parserContext.getRegistry().registerBeanDefinition(annotationHandlerMappingId, handlerMappingDef);
		parserContext.registerComponent(new BeanComponentDefinition(handlerMappingDef, annotationHandlerMappingId));
		
		/* methodHandlerAdapter */
		RootBeanDefinition methodHandlerAdapterDef = new RootBeanDefinition(useAnnotation ? AnnotationMethodHandlerAdapter.class : RequestMappingHandlerAdapter.class);
		String methodHandlerAdapterId = parserContext.getReaderContext().generateBeanName(handlerExceptionResolverDef);
		methodHandlerAdapterDef.setSource(source);
		methodHandlerAdapterDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		
		RootBeanDefinition webBindingInitializerDef = new RootBeanDefinition(ConfigurableWebBindingInitializer.class);
		String webBindingInitializerId = parserContext.getReaderContext().generateBeanName(webBindingInitializerDef);
		webBindingInitializerDef.setSource(source);
		webBindingInitializerDef.setRole(BeanDefinition.ROLE_SUPPORT);
		webBindingInitializerDef.getPropertyValues().add("messageCodesResolver", new RuntimeBeanReference("validateMessageCodesResolver"));
		webBindingInitializerDef.getPropertyValues().add("validator", new RuntimeBeanReference("validator"));
		Map<String, Object> propertyEditors = new HashMap<String, Object>();
		propertyEditors.put("java.lang.String", new HtmlEscapePropertyEditor());
		propertyEditors.put("java.lang.String[]", new StringArrayPropertyEditor());
		propertyEditors.put("java.util.Date", new DateEditor());
		propertyEditors.put("java.math.BigDecimal", new BigDecimalEditor());
		webBindingInitializerDef.getPropertyValues().add("propertyEditors", propertyEditors);
		parserContext.getRegistry().registerBeanDefinition(webBindingInitializerId, webBindingInitializerDef);
		parserContext.registerComponent(new BeanComponentDefinition(webBindingInitializerDef, webBindingInitializerId));
		methodHandlerAdapterDef.getPropertyValues().add("webBindingInitializer", new RuntimeBeanReference(webBindingInitializerId));
		if (useAnnotation) {
			methodHandlerAdapterDef.getPropertyValues().add("customArgumentResolver", new WebArgumentResolver());
		}
		
//		List<Object> messageConverters = new ArrayList<Object>();
		RootBeanDefinition jsonHttpMessageConverterDef = new RootBeanDefinition(JsonHttpMessageConverter.class);
		String jsonHttpMessageConverterId = parserContext.getReaderContext().generateBeanName(jsonHttpMessageConverterDef);
		jsonHttpMessageConverterDef.setSource(source);
		jsonHttpMessageConverterDef.setRole(BeanDefinition.ROLE_SUPPORT);
		jsonHttpMessageConverterDef.getPropertyValues().add("objectMapper", new RuntimeBeanReference("jacksonObjectMapper"));
		parserContext.getRegistry().registerBeanDefinition(jsonHttpMessageConverterId, jsonHttpMessageConverterDef);
		parserContext.registerComponent(new BeanComponentDefinition(jsonHttpMessageConverterDef, jsonHttpMessageConverterId));
//		messageConverters.add(new RuntimeBeanReference(jsonHttpMessageConverterId));
		methodHandlerAdapterDef.getPropertyValues().add("messageConverters", new RuntimeBeanReference(jsonHttpMessageConverterId));
		
		parserContext.getRegistry().registerBeanDefinition(methodHandlerAdapterId, methodHandlerAdapterDef);
		parserContext.registerComponent(new BeanComponentDefinition(methodHandlerAdapterDef, methodHandlerAdapterId));
		
		return null;
	}

}
