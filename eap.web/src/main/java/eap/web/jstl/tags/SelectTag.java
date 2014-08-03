package eap.web.jstl.tags;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.form.TagWriter;

import eap.EapContext;
import eap.comps.codetable.CodeTable;
import eap.util.MessageUtil;

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
public class SelectTag extends org.springframework.web.servlet.tags.form.SelectTag {
	
	/** 码表类型 */
	private String codeType;
	/** 码表组别 */
	private String codeGroup;
	
	/** 是否显示头选择；true-显示, false-不显示   */
	private Boolean headerOption;
	/** 头选择标签 */
	private String headerOptionLabel;
	/** 头选择标签国际化代码 */
	private String headerOptionLabelCode;
	/** 头选择值 */
	private String headerOptionValue;
	
	public SelectTag() {
		super();
	}
	
	@Override
	protected void writeDefaultAttributes(TagWriter tagWriter) throws JspException {
		String uri = this.getRequestContext().getContextPath();
		
		this.setDynamicAttribute(uri, "codetype", codeType);
		if (StringUtils.isNotBlank(codeGroup)) {
			this.setDynamicAttribute(uri, "codegroup", codeGroup);
		}
		if (this.getHeaderOption()) {
			this.setDynamicAttribute(uri, "headeroption", "true");
			this.setDynamicAttribute(uri, "headeroptionlabel", this.getHeaderOptionLabel());
			this.setDynamicAttribute(uri, "headeroptionvalue", this.getHeaderOptionValue());
		}
		
		super.writeDefaultAttributes(tagWriter);
	}
	
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
			if (this.getHeaderOption()) {
				return CodeTable.getComboItems(codeType, 
						(headerOptionLabel != null || StringUtils.isNotBlank(headerOptionLabelCode)) ? new String[] {this.getHeaderOptionValue(), this.getHeaderOptionLabel()} : CodeTable.COMBO_ITEM_CHOICE_HEADER 
					, codeGroup);
			} else {
				return CodeTable.getCodes(codeType, codeGroup);
			}
		} 
//		else {
//			if (this.getHeaderOption()) {
//				Object itemsObj = super.getItems();
//				if (itemsObj instanceof Collection) {
//					List newItems = new ArrayList();
//					
//					Map<String, String> headerItem = new HashMap<String, String>();
//					headerItem.put(this.getItemValue(), this.getHeaderOptionValue());
//					headerItem.put(this.getItemLabel(), this.getHeaderOptionLabel());
//					newItems.add(headerItem);
//					
//					newItems.addAll((Collection) itemsObj);
//					return newItems; // GET / SET
//				}
//			}
			
			return super.getItems();
//		}
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
		this.codeGroup = codeGroup;
	}

	public Boolean getHeaderOption() {
		return headerOption != null ? headerOption : false;
	}

	public void setHeaderOption(Boolean headerOption) {
		this.headerOption = headerOption;
	}

	public String getHeaderOptionLabel() {
		String label = null;
		if (StringUtils.isNotBlank(headerOptionLabelCode)) {
			label = MessageUtil.getMessage(headerOptionLabelCode, null, EapContext.getLocale());
		}
		if (StringUtils.isBlank(label)) {
			label = headerOptionLabel != null ? headerOptionLabel : CodeTable.COMBO_ITEM_CHOICE_HEADER[1];
		}
		
		return label;
	}

	public void setHeaderOptionLabel(String headerOptionLabel) {
		this.headerOptionLabel = headerOptionLabel;
	}
	
	public String getHeaderOptionLabelCode() {
		return headerOptionLabelCode;
	}

	public void setHeaderOptionLabelCode(String headerOptionLabelCode) {
		this.headerOptionLabelCode = headerOptionLabelCode;
	}

	public String getHeaderOptionValue() {
		return headerOptionValue != null ? headerOptionValue : CodeTable.COMBO_ITEM_CHOICE_HEADER[0];
	}

	public void setHeaderOptionValue(String headerOptionValue) {
		this.headerOptionValue = headerOptionValue;
	}
}