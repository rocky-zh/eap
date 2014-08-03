package eap.comps.datamapping.definition.parser;

import java.util.ArrayList;
import java.util.List;

import eap.comps.datamapping.definition.HandlerDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.SectionDefinition;
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
public class PojoToPdfParserDefinition extends ParserDefinition {
	
	public static final String PARSER_TYPE = "PojoToPdf";
	public String getParserType() {
		return PARSER_TYPE;
	}
	
	private String encoding = "UTF-8";
	
	private String title;
	private String author;
	private String subject;
	private String keywords;
	private String creator;
	private boolean creationDate = true;
	private boolean producer = false;
	
	private String userPassword;
	private String ownerPassword;
	private String permissions; // 
	
	private String pageRectangle = "A4";
	private String margins; // Top,Right,Bottom,Left
	private Float[] marginsAsFloat;
	
	private List<SectionDefinition> sections = new ArrayList<SectionDefinition>();
	
	private List<RendererDefinition> renderers = new ArrayList<RendererDefinition>();
	private List<HandlerDefinition> handlers = new ArrayList<HandlerDefinition>();
	
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public boolean isCreationDate() {
		return creationDate;
	}
	public void setCreationDate(boolean creationDate) {
		this.creationDate = creationDate;
	}
	public boolean isProducer() {
		return producer;
	}
	public void setProducer(boolean producer) {
		this.producer = producer;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getOwnerPassword() {
		return ownerPassword;
	}
	public void setOwnerPassword(String ownerPassword) {
		this.ownerPassword = ownerPassword;
	}
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	public String getPageRectangle() {
		return pageRectangle;
	}
	public void setPageRectangle(String pageRectangle) {
		this.pageRectangle = pageRectangle;
	}
	public String getMargins() {
		return margins;
	}
	public void setMargins(String margins) {
		this.margins = margins;
		if (StringUtil.isNotBlank(margins)) {
			marginsAsFloat = new Float[4];
			String[] marginArr = margins.split(",");
			if (marginArr.length == 1) {
				marginsAsFloat[0] = Float.valueOf(marginArr[0]);
				marginsAsFloat[1] = marginsAsFloat[0];
				marginsAsFloat[2] = marginsAsFloat[0];
				marginsAsFloat[3] = marginsAsFloat[0];
			} else if (marginArr.length == 2) {
				marginsAsFloat[0] = Float.valueOf(marginArr[0]);
				marginsAsFloat[1] = Float.valueOf(marginArr[1]);
				marginsAsFloat[2] = marginsAsFloat[0];
				marginsAsFloat[3] = marginsAsFloat[1];
			} else if (marginArr.length == 4) {
				marginsAsFloat[0] = Float.valueOf(marginArr[0]);
				marginsAsFloat[1] = Float.valueOf(marginArr[1]);
				marginsAsFloat[2] = Float.valueOf(marginArr[2]);
				marginsAsFloat[3] = Float.valueOf(marginArr[3]);
			}
		}
	}
	public Float[] getMarginsAsFloat() {
		return marginsAsFloat;
	}
	public void setMarginsAsFloat(Float[] marginsAsFloat) {
		this.marginsAsFloat = marginsAsFloat;
	}
	public void addSection(SectionDefinition section) {
		this.sections.add(section);
	}
	public List<SectionDefinition> getSections() {
		return sections;
	}
	public void setSections(List<SectionDefinition> sections) {
		this.sections = sections;
	}
	public void addRenderer(RendererDefinition renderer) {
		this.renderers.add(renderer);
	}
	public List<RendererDefinition> getRenderers() {
		return renderers;
	}
	public void setRenderers(List<RendererDefinition> renderers) {
		this.renderers = renderers;
	}
	public void addHandler(HandlerDefinition handler) {
		this.handlers.add(handler);
	}
	public List<HandlerDefinition> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<HandlerDefinition> handlers) {
		this.handlers = handlers;
	}
}