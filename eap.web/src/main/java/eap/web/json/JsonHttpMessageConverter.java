package eap.web.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import eap.EapContext;
import eap.WebEnv;
import eap.comps.webevent.WebEvents;
import eap.comps.webevent.WebEventsHelper;
import eap.util.DateUtil;
import eap.util.StringUtil;
import eap.util.ViewUtil;

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
public class JsonHttpMessageConverter extends MappingJacksonHttpMessageConverter {
	
	private String viewPath = "/WEB-INF/classes/%s/view/%s.jsp"; // TODO
	
	public JsonHttpMessageConverter() {
		super();
		setSupportedMediaTypes(Arrays.asList(
//			new MediaType("text", "*"),
			new MediaType("application", "json", DEFAULT_CHARSET)
		));
	}
	
	@Override
	protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) 
		throws IOException, HttpMessageNotReadableException 
	{
		return super.readInternal(clazz, inputMessage);
	}
	
	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) 
		throws IOException, HttpMessageNotWritableException 
	{
		JsonResponse responseObject = new JsonResponse();
		responseObject.setServerTime(DateUtil.currDate());
		responseObject.setEvents(this.getWebEvents());
		
		if (object instanceof ModelAndView) {
			ModelAndView mav = (ModelAndView) object;
			Assert.hasText(mav.getViewName(), "ModelAndView 'viewName' must not be empty");
				
			HttpServletRequest request = EapContext.get("request", HttpServletRequest.class);
			String basePackage = (String) request.getAttribute(WebEnv.REQUEST_LAST_HANDLER_BASE_PACKAGE_KEY);
			String newViewName = String.format(viewPath, StringUtil.replaceChars(basePackage, ".", "/"), mav.getViewName());
			String result = ViewUtil.includePageAsString(newViewName, null, mav.getModel(), request);
			responseObject.setResult(result);
		} else {
			responseObject.setResult(object);
		}
		
		super.writeInternal(responseObject, outputMessage);
	}
	
	private Map<String, List<Object[]>> getWebEvents() {
		WebEvents webEvents = WebEventsHelper.getWebEvents0(EapContext.get("request",  HttpServletRequest.class)); // not clear
		if (webEvents != null) {
			return webEvents.getEvents();
		} else {
			return null;
		}
	}

	public String getViewPath() {
		return viewPath;
	}
	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}
}