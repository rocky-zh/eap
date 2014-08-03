package eap.web.jstl.tags;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import eap.comps.codetable.CodeTable;

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
public class RadioButtonsTag extends org.springframework.web.servlet.tags.form.RadioButtonsTag {
	
	/** 码表类型 */
	private String codeType;
	/** 码表组别 */
	private String codeGroup;
	
	@Override
	protected String getItemLabel() {
		if (StringUtils.isNotBlank(codeType)) {
			return CodeTable.SELECT_OPTION_ITEM_LABLE;
		}
		
		return super.getItemLabel();
	}
	
	@Override
	protected String getItemValue() {
		if (StringUtils.isNotBlank(codeType)) {
			return CodeTable.SELECT_OPTION_ITEM_VALUE;
		}
		
		return super.getItemValue();
	}
	
	@Override
	protected Object getItems() {
		if (StringUtils.isNotBlank(codeType)) {
			return CodeTable.getCodes(codeType, codeGroup);
		}
		
		return super.getItems();
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		Assert.hasText(codeType, "'codeType' must not be empty");
		this.codeType = codeType;
	}

	public String getCodeGroup() {
		return codeGroup;
	}

	public void setCodeGroup(String codeGroup) {
		Assert.hasText(codeGroup, "'codeGroup' must not be empty");
		this.codeGroup = codeGroup;
	}
}