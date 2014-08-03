package eap.web.jstl.tags;

import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.HtmlUtils;

import eap.util.PagingDataList;

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
public class PagingTag extends HtmlEscapingAwareTag {
	
	public static final String DEFAULT_CSS_CLASS = "table-footer clearfix";
	
	private String cssClass;
	
	private PagingDataList dataList;
	
	private String jsCallback;
	
	private Boolean showGo;

	@Override
	protected int doStartTagInternal() throws Exception {
		PagingDataList pagingDataList = this.getDataList();
		if (pagingDataList != null) {
			String jsCallbackFn = this.getJsCallback();
			
			StringBuilder html = new StringBuilder();
			html.append("<div class=\"" + this.getCssClass() + "\">");
			html.append(String.format("<div class=\"fl\">共有<em>%d</em>条记录，当前第<em>%d</em>页，共<em>%d</em>页</div>", 
				pagingDataList.getTotalCount(), pagingDataList.getPaginator().getCurrPage(), pagingDataList.getPaginator().getPages()));
			html.append("<div class=\"pages fr\">");
			
			if(pagingDataList.getItems() != null && !pagingDataList.getItems().isEmpty()){
				if(!pagingDataList.getPaginator().isFirst()){
					html.append(String.format("<a href=\"javascript:%s\">首页</a> ", jsCallbackFn + "(1)"));
				}
				if(pagingDataList.getPaginator().hasPrev()){
					html.append(String.format("<a href=\"javascript:%s\">上一页</a> ", jsCallbackFn + "(" + pagingDataList.getPaginator().prev() + ")"));
				}
				if(pagingDataList.getPaginator().hasNext()){
					html.append(String.format("<a href=\"javascript:%s\">下一页</a> ", jsCallbackFn + "(" + pagingDataList.getPaginator().next() + ")"));
				}
//				html.append(String.format("<a href=\"javascript:%s\" %s>上一页</a> ", jsCallbackFn + "(" + pagingDataList.getPaginator().prev() + ")", !pagingDataList.getPaginator().hasPrev() ? "disabled=\"true\"" : ""));
				
//				html.append(String.format("<a href=\"javascript:%s\" %s>下一页</a> ", jsCallbackFn + "(" + pagingDataList.getPaginator().next() + ")", !pagingDataList.getPaginator().hasNext() ? "disabled=\"true\" ": ""));
				
				if(!pagingDataList.getPaginator().isLast()){
					html.append(String.format("<a href=\"javascript:%s\">尾页</a> ", jsCallbackFn + "(" + pagingDataList.getPaginator().getPages()+ ")"));
				}
//				html.append(String.format("<span>转到</span><input type=\"text\" class=\"pub-txt\" onblur=\"javascript:%s\" /><span>页</span>", "if (this.value && this.value > 0 && this.value <= " + pagingDataList.getPaginator().getPages() + " ) {" + jsCallbackFn + "(this.value);} else {this.value = '';}"));
//				html.append(String.format("<span>转到</span><input id=\"goto\" type=\"text\" class=\"pub-txt\" /><span>页</span><a onclick=\"javascript:%s\"></a>", "if ($('#goto').val() && $('#goto').val() > 0 && $('#goto').val() <= " + pagingDataList.getPaginator().getPages() + " ) {" + jsCallbackFn + "($('#goto').val());} else {$('#goto').val('');}"));
				if (getShowGo()) {
					html.append(String.format("<span>转到</span><input type=\"text\" class=\"pub-txt\" /><span>页</span><a href=\"javascript:void(0)\" class=\"go\" onclick=\"javascript:%s\">转</a>", "var input = this.parentElement.getElementsByTagName('input')[0]; if (input.value && input.value > 0 && input.value <= " + pagingDataList.getPaginator().getPages() + " ) {window." + jsCallbackFn + "(input.value);} else {input.value = '';}"));
				}
			}
			html.append("</div>");
			html.append("</div>");
			
			String htmlStr = html.toString();
			if (isHtmlEscape()) {
				htmlStr = HtmlUtils.htmlEscape(htmlStr);
			}
			
			this.pageContext.getOut().write(htmlStr);
		}
		
		return EVAL_PAGE;
	}
	
	public String getCssClass() {
		return cssClass != null ? cssClass : DEFAULT_CSS_CLASS;
	}
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	public PagingDataList getDataList() {
		return dataList;
	}
	public void setDataList(PagingDataList dataList) {
		this.dataList = dataList;
	}
	public String getJsCallback() {
		return jsCallback;
	}
	public void setJsCallback(String jsCallback) {
		this.jsCallback = jsCallback;
	}

	public Boolean getShowGo() {
		return showGo == null ? true : showGo;
	}

	public void setShowGo(Boolean showGo) {
		this.showGo = showGo;
	}
	
}