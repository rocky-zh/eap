package eap.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import eap.util.HttpUtil;

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
public class MappingExceptionResolver extends SimpleMappingExceptionResolver {
	
	@Override
	protected String determineViewName(Exception ex, HttpServletRequest request) {
		String viewName = super.determineViewName(ex, request);
		if (HttpUtil.isAjaxRequest(request)) {
			viewName += "Ajax";
		}
		
		return viewName;
	}
}