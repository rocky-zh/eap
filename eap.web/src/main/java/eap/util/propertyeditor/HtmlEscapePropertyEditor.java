package eap.util.propertyeditor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.util.HtmlUtils;

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
public class HtmlEscapePropertyEditor extends StringTrimmerEditor {
	
	public HtmlEscapePropertyEditor() {
		super(true);
	}
	
	public void setAsText(String text) {
		if (StringUtils.isNotBlank(text)) {
			text = HtmlUtils.htmlEscape(text.trim());
		}
		
		super.setAsText(text);
	}
}
