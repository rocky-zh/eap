package eap.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

import eap.EapContext;
import eap.WebEnv;
import eap.util.ElParser;
import eap.util.HttpUtil;
import eap.util.SpringMvcUtil;

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
public class RequestToViewNameTranslator extends DefaultRequestToViewNameTranslator {
	
	@Override
	public String getViewName(HttpServletRequest request) {
		RequestMapping requestMapping = SpringMvcUtil.getMethodRequestMapping(request);
		String inputView = requestMapping.inputView();
		if (requestMapping != null && StringUtils.isNotBlank(inputView)) {
			return parseViewName(inputView, request);
		}
		
		return super.getViewName(request);
	}
	
	private String parseViewName(String viewName, HttpServletRequest request) {
		if (viewName.indexOf("#env") != -1 || viewName.indexOf("#requestScope") != -1 || viewName.indexOf("#paramScope") != -1) {
			ElParser elParser = ElParser.getInstance();
			if (viewName.indexOf("#env") != -1) {
				WebEnv env = (WebEnv) EapContext.getEnv();
				elParser.setVar("env", env.getProperties());
			}
			if (viewName.indexOf("#requestScope") != -1) {
				elParser.setVar("requestScope", HttpUtil.getRequsetMap(request));
			}
			if (viewName.indexOf("#paramScope") != -1) {
				elParser.setVar("paramScope", HttpUtil.getRequestParamMap(request));
			}
			
			return elParser.eval(viewName, String.class); 
		} else {
			return viewName;
		}
	}
}