package eap.web.jstl.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import eap.util.StringUtil;

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
public class PageMetaTag extends RequestContextAwareTag {
	
//	private String contentType = "text/html; charset=utf-8";
	private String title;
	private String pageGroup;
	private String pageNo;
	private String pageKeywords;
	private String pageDescription;
	
	private String cache;
	
	@Override
	protected int doStartTagInternal() throws Exception {
		return EVAL_PAGE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		StringBuilder html = new StringBuilder();
		if (StringUtil.isNotBlank(this.getTitle())) {
			html.append(String.format("<title>%s</title>\r\n", this.getTitle()));
		}
//		if (StringUtil.isNotBlank(this.getContentType())) {
//			html.append(String.format("<meta http-equiv=\"Content-Type\" content=\"%s\"/>\r\n", this.getContentType()));
//		}
		if (StringUtil.isNotBlank(this.getPageGroup())) {
			html.append(String.format("<meta name=\"pageGroup\" content=\"%s\"/>\r\n", this.getPageGroup()));
		}
		if (StringUtil.isNotBlank(this.getPageNo())) {
			html.append(String.format("<meta name=\"pageNo\" content=\"%s\"/>\r\n", this.getPageNo()));
		}
		if (StringUtil.isNotBlank(this.getPageKeywords())) {
			html.append(String.format("<meta name=\"keywords\" content=\"%s\"/>\r\n", this.getPageKeywords()));
		}
		if (StringUtil.isNotBlank(this.getPageDescription())) {
			html.append(String.format("<meta name=\"description\" content=\"%s\"/>\r\n", this.getPageDescription()));
		}
		if (Boolean.parseBoolean(this.getCache())) {
			html.append("<meta http-equiv=\"Cache-Control\" content=\"no-cache\" />\r\n");
			html.append("<meta http-equiv=\"Pragma\" content=\"no-cache\" />\r\n");
			html.append("<meta http-equiv=\"Expires\" content=\"0\" />\r\n");
			
			HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
		}
		
		if (html.length() > 0) {
			try {
				this.pageContext.getOut().write(html.toString());
			} catch (IOException e) {
				throw new JspException(e.getMessage(), e);
			}
		}
		
		return EVAL_PAGE;
	}
		
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
//	public String getContentType() {
//		return contentType;
//	}
//	public void setContentType(String contentType) {
//		this.contentType = contentType;
//	}
	public String getPageNo() {
		return pageNo;
	}
	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}
	public String getPageGroup() {
		return pageGroup;
	}
	public void setPageGroup(String pageGroup) {
		this.pageGroup = pageGroup;
	}
	public String getPageKeywords() {
		return pageKeywords;
	}
	public void setPageKeywords(String pageKeywords) {
		this.pageKeywords = pageKeywords;
	}
	public String getPageDescription() {
		return pageDescription;
	}
	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}
	public String getCache() {
		return StringUtil.isBlank(cache) ? "false" : "true";
	}
	public void setCache(String cache) {
		this.cache = cache;
	}
}