package eap.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本	   修改人		 修改时间		 修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 * @see org.springframework.web.servlet.view.AbstractTemplateViewResolver
 * @ses org.springframework.web.servlet.view.freemarker.FreeMarkerView
 */
public class ViewUtil {
	
	public static void includePage(String url, Map<String, ?> paramsMap, Map<String,?> requestAttributesMap, HttpServletRequest request, Writer writer) {
		HttpServletRequestWrapper wrappedRequest = new CustomParamsRequest(request, paramsMap, true);
		if (requestAttributesMap != null && requestAttributesMap.size() > 0) {
			for (Map.Entry<String, ?> entry : requestAttributesMap.entrySet()) {
				wrappedRequest.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		
		HttpServletResponse wrappedResponse = new CustomWriterRequest(writer);
		
		try {
			wrappedRequest.getRequestDispatcher(url).include(wrappedRequest, wrappedResponse);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	public static String includePageAsString(String url, Map<String, ?> paramsMap, Map<String,?> requestAttributesMap, HttpServletRequest request) {
		StringWriter writer = new StringWriter();
		includePage(url, paramsMap, requestAttributesMap, request, writer);
		return writer.toString();
	}
	
	public static void render(String url, Map<String, ?> model, HttpServletRequest request, Writer writer, AbstractTemplateView view)  { // AbstractUrlBasedView
		view.setServletContext(request.getServletContext());
		view.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()));
		view.setUrl(url);
//		view.setContentType(null); 
//		view.setRequestContextAttribute(null);
//		view.setAttributesMap(null);
//		view.setExposePathVariables(false);
//		view.setExposeRequestAttributes(false);
//		view.setAllowRequestOverride(true);
//		view.setExposeSessionAttributes(false);
//		view.setAllowSessionOverride(false);
//		view.setExposeSpringMacroHelpers(false);
		
		HttpServletResponse wrappedResponse = new CustomWriterRequest(writer);
		
		try {
			view.render(model, request, wrappedResponse);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	public static String renderAsString(String url, Map<String, ?> model, HttpServletRequest request, AbstractTemplateView view) {
		StringWriter writer = new StringWriter();
		render(url, model, request, writer, view);
		return writer.toString();
	}
	
	private final static class CustomWriterRequest implements HttpServletResponse {
		
		private PrintWriter writer;
		public CustomWriterRequest(Writer writer) {
			this.writer = new PrintWriter(writer);
		}
		public PrintWriter getWriter() throws IOException {
			return writer;
		}

		public String getCharacterEncoding() {
			throw new UnsupportedOperationException();
		}
		public String getContentType() {
			return null;
		}
		public ServletOutputStream getOutputStream() throws IOException {
			throw new UnsupportedOperationException();
		}
		public void setCharacterEncoding(String charset) {
			throw new UnsupportedOperationException();
		}
		public void setContentLength(int len) {
			throw new UnsupportedOperationException();
		}
		public void setContentType(String type) {
		}
		public void setBufferSize(int size) {
			throw new UnsupportedOperationException();
		}
		public int getBufferSize() {
			throw new UnsupportedOperationException();
		}
		public void flushBuffer() throws IOException {
			throw new UnsupportedOperationException();
		}
		public void resetBuffer() {
			throw new UnsupportedOperationException();
		}
		public boolean isCommitted() {
			throw new UnsupportedOperationException();
		}
		public void reset() {
			throw new UnsupportedOperationException();
		}
		public void setLocale(Locale loc) {
			throw new UnsupportedOperationException();
		}
		public Locale getLocale() {
			throw new UnsupportedOperationException();
		}
		public void addCookie(Cookie cookie) {
			throw new UnsupportedOperationException();
		}
		public boolean containsHeader(String name) {
			throw new UnsupportedOperationException();
		}
		public String encodeURL(String url) {
			throw new UnsupportedOperationException();
		}
		public String encodeRedirectURL(String url) {
			throw new UnsupportedOperationException();
		}
		public String encodeUrl(String url) {
			throw new UnsupportedOperationException();
		}
		public String encodeRedirectUrl(String url) {
			throw new UnsupportedOperationException();
		}
		public void sendError(int sc, String msg) throws IOException {
			throw new UnsupportedOperationException();
		}
		public void sendError(int sc) throws IOException {
			throw new UnsupportedOperationException();
		}
		public void sendRedirect(String location) throws IOException {
			throw new UnsupportedOperationException();
		}
		public void setDateHeader(String name, long date) {
			throw new UnsupportedOperationException();
		}
		public void addDateHeader(String name, long date) {
			throw new UnsupportedOperationException();
		}
		public void setHeader(String name, String value) {
			throw new UnsupportedOperationException();
		}
		public void addHeader(String name, String value) {
			throw new UnsupportedOperationException();
		}
		public void setIntHeader(String name, int value) {
			throw new UnsupportedOperationException();
		}
		public void addIntHeader(String name, int value) {
			throw new UnsupportedOperationException();
		}
		public void setStatus(int sc) {
			throw new UnsupportedOperationException();
		}
		public void setStatus(int sc, String sm) {
			throw new UnsupportedOperationException();
		}
		public int getStatus() {
			throw new UnsupportedOperationException();
		}
		public String getHeader(String name) {
			throw new UnsupportedOperationException();
		}
		public Collection<String> getHeaders(String name) {
			throw new UnsupportedOperationException();
		}
		public Collection<String> getHeaderNames() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final class CustomParamsRequest extends HttpServletRequestWrapper { // see freemarker.ext.servlet.IncludePage
		private final HashMap paramsMap;

		private CustomParamsRequest(HttpServletRequest request, Map paramMap, boolean inheritParams) {
			super(request);
			paramsMap = inheritParams ? new HashMap(request.getParameterMap()) : new HashMap();
			
			if (paramMap != null && paramMap.size() > 0) {
				for (Iterator it = paramMap.entrySet().iterator(); it.hasNext();) {
					Map.Entry entry = (Map.Entry)it.next();
					String name = String.valueOf(entry.getKey());
					Object value = entry.getValue();
					final String[] valueArray;
					if(value == null) {
						// Null values are explicitly added (so, among other 
						// things, we can hide inherited param values).
						valueArray = new String[] { null };
					}
					else if(value instanceof String[]) {
						// String[] arrays are just passed through
						valueArray = (String[])value;
					}
					else if(value instanceof Collection) {
						// Collections are converted to String[], with 
						// String.valueOf() used on elements
						Collection col = (Collection)value;
						valueArray = new String[col.size()];
						int i = 0;
						for (Iterator it2 = col.iterator(); it2.hasNext();) {
							valueArray[i++] = String.valueOf(it2.next());
						}
					}
					else if(value.getClass().isArray()) {
						// Other array types are too converted to String[], with 
						// String.valueOf() used on elements
						int len = Array.getLength(value);
						valueArray = new String[len];
						for(int i = 0; i < len; ++i) {
							valueArray[i] = String.valueOf(Array.get(value, i));
						}
					}
					else {
						// All other values (including strings) are converted to a
						// single-element String[], with String.valueOf applied to
						// the value.
						valueArray = new String[] { String.valueOf(value) };
					}
					String[] existingParams = (String[])paramsMap.get(name);
					int el = existingParams == null ? 0 : existingParams.length;
					if(el == 0)
					{
						// No original params, just put our array
						paramsMap.put(name, valueArray);
					}
					else
					{
						int vl = valueArray.length;
						if(vl > 0)
						{
							// Both original params and new params, prepend our
							// params to original params
							String[] newValueArray = new String[el + vl];
							System.arraycopy(valueArray, 0, newValueArray, 0, vl);
							System.arraycopy(existingParams, 0, newValueArray, vl, el);
							paramsMap.put(name, newValueArray);
						}
					}
				}
			}
		}

		public String[] getParameterValues(String name) {
			String[] value = ((String[])paramsMap.get(name));
			return value != null ? (String[])value.clone() : null;
		}

		public String getParameter(String name) {
			String[] values = (String[])paramsMap.get(name);
			return values != null && values.length > 0 ? values[0] : null;
		}

		public Enumeration getParameterNames() {
			return Collections.enumeration(paramsMap.keySet());
		}

		public Map getParameterMap() {
			HashMap clone = (HashMap)paramsMap.clone();
			for (Iterator it = clone.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry)it.next();
				entry.setValue(((String[])entry.getValue()).clone());
			}
			return Collections.unmodifiableMap(clone);
		}
	}
}