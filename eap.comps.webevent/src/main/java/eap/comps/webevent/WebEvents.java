package eap.comps.webevent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import eap.util.JsonUtil;

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
public final class WebEvents {
	
	public static final String WEB_EVENTS_FORM = "form";
	
	public static final String WEB_EVENTS_INPUT_STYLES = "inputStyles";
	
	public static final String WEB_EVENTS_ALERT = "alert";
	public static final String WEB_EVENTS_ALERT_ERROR = "error";
	public static final String WEB_EVENTS_ALERT_WARN = "warn";
	public static final String WEB_EVENTS_ALERT_INFO = "info";
	
	@SuppressWarnings("serial")
	private Map<String, List<Object[]>> events = new TreeMap<String, List<Object[]>>() {
		public List<Object[]> get(Object key) {
			List<Object[]> value = super.get(key);
			if (value == null) {
				value = new ArrayList<Object[]>();
				super.put(key.toString(), value);
			}
			
			return value;
		};
	};
	
	public void setForm(String formId, WebFormVO webFormVO) {
		events.get(WEB_EVENTS_FORM).add(new Object[] {formId, webFormVO});
	}
	public WebFormVO getForm(String formId, boolean createWithNotFound) {
		WebFormVO webFormVO = null;
		for (Object[] form : events.get(WEB_EVENTS_FORM)) {
			if (formId.equals(form[0].toString())) {
				webFormVO = (WebFormVO) form[1];
				break;
			}
		}
		if (webFormVO == null && createWithNotFound == true) {
			webFormVO = new WebFormVO();
			events.get(WEB_EVENTS_FORM).add(new Object[] {formId, webFormVO});
		}
		
		return webFormVO;
	}
	
	public void alert(String msgType, String msg) {
		if (StringUtils.isNotBlank(msg)) {
//			msg = HtmlUtils.htmlEscape(msg);
			msg = StringEscapeUtils.escapeHtml(msg);
			events.get(WEB_EVENTS_ALERT).add(new Object[] {msgType, msg});
		}
	}
	public void alert(String message) {
		this.alert(WEB_EVENTS_ALERT_INFO, message);
	}
	
	public void setInputStyles(String inputStylesId, Map<String, Map<String, Object>> inputStyles) {
		Map<String, Map<String, Object>> oldInputStyles = getInputStyles(inputStylesId);
		if (oldInputStyles == null) {
			events.get(WEB_EVENTS_INPUT_STYLES).add(new Object[] {inputStylesId, inputStyles});
		} else {
			oldInputStyles.putAll(inputStyles);
		}
	}
	
	public Map<String, Map<String, Object>> getInputStyles(String inputStylesId) {
		for (Object[] inputStyles : events.get(WEB_EVENTS_INPUT_STYLES)) {
			if (inputStylesId.equals(inputStyles[0].toString())) {
				return (Map<String, Map<String, Object>>) inputStyles[1];
			}
		}
		
		return null;
	}
	
	public Map<String, List<Object[]>> getEvents() {
		return events;
	}
	
	public static void main(String[] args) throws Exception {
		JsonUtil jsonUtil = new JsonUtil();
		
		WebEvents events = new WebEvents();
		events.alert("</a>提示\r\n信息");
		events.alert(WEB_EVENTS_ALERT_ERROR, "提示错误信息");
		
		WebFormVO webFormVO = new WebFormVO();
		webFormVO.addError("userName", "alreay exists");
		
		events.setForm("loginForm", webFormVO);
		
		String json = JsonUtil.toJson(events.getEvents());
		System.out.println(json);
	}
}