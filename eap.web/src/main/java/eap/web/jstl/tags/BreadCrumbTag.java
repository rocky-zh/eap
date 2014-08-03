package eap.web.jstl.tags;

import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import eap.comps.datastore.DataStore;

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
public class BreadCrumbTag extends HtmlEscapingAwareTag {
	
	private String cssClass;
	
	private String fail;

	@Override
	protected int doStartTagInternal() throws Exception {
		TagWriter tagWriter = this.createTagWriter();
		tagWriter.startTag("div");
		tagWriter.writeOptionalAttributeValue("class", "breadCrumb " + this.getCssClass());
		tagWriter.forceBlock();
		tagWriter.appendValue(this.getBreadCrumbHtml(fail));
		tagWriter.endTag(true);
		
		return EVAL_PAGE;
	}
	
	private TagWriter createTagWriter() {
		return new TagWriter(this.pageContext);
	}
	
	private String getBreadCrumbHtml(String fail) {
		String cacheKey = "bc_" + fail;;
		String htmlInCache = (String) DataStore.getInMem(cacheKey);
		if (htmlInCache != null) {
			return htmlInCache;
		}
		
		StringBuilder html = new StringBuilder(); // TODO
		html.append("<h2 class=\"fl\"><span><a href=\"\">首页</a></span></h2>");
		html.append("<div class=\"location fl\">");
		html.append("<span>></span><a href=\"\">我的钱豆</a>");
		html.append("<span>></span>个人资料");
		html.append("</div>");
		htmlInCache = html.toString(); // (isHtmlEscape() ? HtmlUtils.htmlEscape(html.toString()) : html.toString());
		
		DataStore.setInMem(cacheKey, htmlInCache);
		
		return htmlInCache;
	}
	
	public String getCssClass() {
		return cssClass != null ? cssClass : "";
	}
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
}