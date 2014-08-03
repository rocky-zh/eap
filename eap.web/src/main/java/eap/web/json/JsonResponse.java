package eap.web.json;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class JsonResponse {
	
	private Date serverTime;
	private Map<String, List<Object[]>> events;
	
	private Object result;

	public Date getServerTime() {
		return serverTime;
	}
	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}
	public Map<String, List<Object[]>> getEvents() {
		return events;
	}
	public void setEvents(Map<String, List<Object[]>> events) {
		this.events = events;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}