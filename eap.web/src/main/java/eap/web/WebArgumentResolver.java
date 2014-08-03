package eap.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import eap.EapContext;
import eap.Env;
import eap.base.UserDetailsVO;
import eap.util.Paginator;

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
public class WebArgumentResolver implements org.springframework.web.bind.support.WebArgumentResolver {
	
	private static final Logger logger = LoggerFactory.getLogger(WebArgumentResolver.class);
	
	@Override
	public Object resolveArgument(MethodParameter mp, NativeWebRequest nwr) throws Exception {
		HttpServletRequest request = (HttpServletRequest) nwr.getNativeRequest();
		
		Class<?> paramType = mp.getParameterType();
		if (Paginator.class.isAssignableFrom(paramType)) {
			return this.getPaginator(request);
		}
		else if (UserDetailsVO.class.isAssignableFrom(paramType)) {
			return this.getUserDetailsVO(request);
		}
		
		return UNRESOLVED;
	}
	
	private Paginator getPaginator(HttpServletRequest request) {
		Env env = EapContext.getEnv();
		
		Paginator paginator = new Paginator();
		String currPage = request.getParameter(env.getProperty("app.paging.currPageParam", "currPage"));
		if (StringUtils.isNotBlank(currPage)) {
			try {
				paginator.setCurrPage(Integer.parseInt(currPage));
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}
		String pageSize = request.getParameter(env.getProperty("app.paging.pageSizeParam", "pageSize"));
		if (StringUtils.isNotBlank(pageSize)) {
			try {
				paginator.setPageSize(Integer.parseInt(pageSize));
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}
		
		return paginator;
	}
	
	private UserDetailsVO getUserDetailsVO(HttpServletRequest request) {
		return EapContext.getUserDetailsVO();
//		return (UserDetailsVO) request.getSession().getAttribute(Env.SESSION_USER_DETAILS_KEY);
	}
}