package eap.util.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.util.Date;

import org.springframework.util.StringUtils;

import eap.util.DateUtil;

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
public class DateEditor extends PropertyEditorSupport {

	private boolean allowEmpty = true;
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			setValue(null);
		}
		else {
			setValue(DateUtil.parse(text.trim()));
		}
	}

	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		return (value != null ? DateUtil.format(value) : "");
	}
	
	public void setAllowEmpty(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}
}
