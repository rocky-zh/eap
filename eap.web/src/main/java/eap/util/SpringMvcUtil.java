package eap.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

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
public class SpringMvcUtil {
	
	public static AbstractHandlerMapping getAnnotationHandlerMapping(HttpServletRequest request) {
		return RequestContextUtils.getWebApplicationContext(request).getBean(DefaultAnnotationHandlerMapping.class);
	}
	
	public static AnnotationMethodHandlerAdapter getAnnotationMethodHandlerAdapter(HttpServletRequest request) {
		return RequestContextUtils.getWebApplicationContext(request).getBean(AnnotationMethodHandlerAdapter.class);
	}
	
	public static RequestMapping getMethodRequestMapping(HttpServletRequest request, Object handler) {
		AnnotationMethodHandlerAdapter methodHandler = getAnnotationMethodHandlerAdapter(request);
		return methodHandler.getMethodAnnotation(request, handler, RequestMapping.class);
	}
	
	public static RequestMapping getMethodRequestMapping(HttpServletRequest request) {
		AbstractHandlerMapping  handleMapping = getAnnotationHandlerMapping(request);
		if (handleMapping != null) {
			try {
				HandlerExecutionChain handlerEC = handleMapping.getHandler(request);
				if (handlerEC != null) {
					Object handler = handlerEC.getHandler();
					if (handler != null) {
						return SpringMvcUtil.getMethodRequestMapping(request, handler);
					}
				}
			} catch (Exception e) {
			}
		}
		
		return null;
	}
	
//	public static HttpServlet getDispatcherServlet(ServletContext servletContext) {
////		servletContext.children
////		return (HttpServlet) ReflectUtil.getFieldValue(servletContext.getNamedDispatcher(DISPATCHER_SERVLET_NAME), "wrapper", StandardWrapper.class).getServlet();
//		return null;
//	}
	
//	public static <T extends Annotation> T getMethodAnnotation(HttpServletRequest request, Object handler, Class<T> annotationType) {
//		AnnotationMethodHandlerAdapter methodHandler = getAnnotationMethodHandlerAdapter(request.getSession().getServletContext());
//		Object methodResolver = ReflectUtil.invokeMethod(AnnotationMethodHandlerAdapter.class.getDeclaredMethod("getMethodResolver", Object.class), methodHandler, handler);
//		Method handlerMethod = null;
//		try {
//			ReflectUtil.invokeMethod(AnnotationMethodHandlerAdapter.class.getDeclaredMethod("getMethodResolver", Object.class), methodHandler, handler);
//			handlerMethod = methodResolver.resolveHandlerMethod(request);
//		} catch (ServletException e) {
//			throw new IllegalArgumentException(e.getMessage(), e);
//		}
//		Method specificMethod = ClassUtils.getMostSpecificMethod(handlerMethod, handler.getClass());
////		RequestSpecificMappingInfo mappingInfo = new RequestSpecificMappingInfo(methodResolver.mappings.get(specificMethod));
//		
//		return AnnotationUtils.findAnnotation(specificMethod, annotationType);
//	}
}