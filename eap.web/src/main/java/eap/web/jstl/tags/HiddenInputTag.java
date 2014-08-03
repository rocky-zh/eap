package eap.web.jstl.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.form.TagWriter;

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
public class HiddenInputTag extends org.springframework.web.servlet.tags.form.HiddenInputTag {
	
	private Boolean showText;
	
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		if (this.getShowText()) {
			String value = getDisplayString(getBoundValue(), getPropertyEditor());
			try {
				this.pageContext.getOut().write(processFieldValue(getName(), value, "hidden"));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		return super.writeTagContent(tagWriter);
	}
	
	public Boolean getShowText() {
		return showText != null ? showText : false;
	}

	public void setShowText(Boolean showText) {
		this.showText = showText;
	}
}