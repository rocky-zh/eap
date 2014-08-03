package eap.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eap.util.StringUtil;
import eap.util.UrlUtil;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * @see org.springframework.web.filter.CharacterEncodingFilter
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class CharacterEncodingFilter extends EnhanceFilter {
	
	private String encoding;
	private boolean forceEncoding = false;
	
	private String onceCharsetParamName = "_charset";
	
	@Override
	protected void doFilterCleaned(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException 
	{
		String charset = UrlUtil.getUrlQueryStringAsMap(request.getQueryString()).get(onceCharsetParamName);
		if (StringUtil.isBlank(charset)) {
			charset = encoding;
		}
		
		if (charset != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
			request.setCharacterEncoding(charset);
			if (this.forceEncoding) {
				response.setCharacterEncoding(charset);
			}
		}
		
		filterChain.doFilter(request, response);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public void setForceEncoding(boolean forceEncoding) {
		this.forceEncoding = forceEncoding;
	}
	public String getOnceCharsetParamName() {
		return onceCharsetParamName;
	}
	public void setOnceCharsetParamName(String onceCharsetParamName) {
		this.onceCharsetParamName = onceCharsetParamName;
	}
}