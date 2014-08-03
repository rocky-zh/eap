package eap.comps.datamapping.definition;

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
public class SectionDefinition extends Definition {
	
	public static final String RENDER_TYPE_HTML = "html";
	public static final String RENDER_TYPE_FILL_FORM = "fillForm";
	
	private String file;
	private String renderType;
	private boolean newPage = false;
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getRenderType() {
		return renderType;
	}
	public void setRenderType(String renderType) {
		this.renderType = renderType;
	}
	public boolean isNewPage() {
		return newPage;
	}
	public void setNewPage(boolean newPage) {
		this.newPage = newPage;
	}
}