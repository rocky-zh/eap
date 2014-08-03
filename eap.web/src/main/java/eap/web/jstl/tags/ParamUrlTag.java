package eap.web.jstl.tags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

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
public class ParamUrlTag extends eap.web.jstl.tags.UrlTag {
	
	@Override
	public int doEndTag() throws JspException {
		String url = null;
		try {
			Method createUrlMethod = this.getClass().getSuperclass().getSuperclass().getDeclaredMethod("createUrl"); // TODO
			createUrlMethod.setAccessible(true);
			url = (String) createUrlMethod.invoke(this);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			throw new IllegalArgumentException(t.getMessage(), t);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
//		String url = (String) ReflectUtil.invokeMethod(this, "createUrl", null); // TODO return => null
		
		RequestDataValueProcessor processor = getRequestContext().getRequestDataValueProcessor();
		ServletRequest request = this.pageContext.getRequest();
		if ((processor != null) && (request instanceof HttpServletRequest)) {
			url = processor.processUrl((HttpServletRequest) request, url);
		}
		
		// find a param aware ancestor
		ParamUrlAware paramUrlAwareTag = (ParamUrlAware) findAncestorWithClass(this, ParamUrlAware.class);
		if (paramUrlAwareTag == null) {
			throw new JspException(
					"The param tag must be a descendant of a tag that supports parameters");
		}
		paramUrlAwareTag.addParamUrl(url);
		
		return EVAL_PAGE;
	}
}