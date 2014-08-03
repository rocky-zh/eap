package eap.web.jstl.tags;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

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
public class CodeLabelTag extends TagSupport {
	
	/** 码表类型 */
	private String codeType;
	/** 码表键 */
	private String codeKey;
	/** 码表值 */
	private Object codeValue;
	
	/** 格式化参数分隔符 */
	public final static String ARGS_SEPARATOR = ",";
	/** 格式化参数 */
	private String args;
	private String argsSeparator = ARGS_SEPARATOR;
	
	@Override
	public int doStartTag() throws JspException{
		if (StringUtils.isNotBlank(codeType)) {
			String codeName = null;
			if (StringUtils.isNotBlank(codeKey)) {
				codeName = CodeTable.getName(codeType, codeKey);
			} 
			else if (codeValue != null) {
				codeName = CodeTable.getNameByValue(codeType, codeValue.toString());
			} 
			else {
				codeName = "";
			}
			
			if (StringUtils.isNotBlank(codeName) && args != null && args.length() > 0) {
				codeName = MessageFormat.format(codeName, StringUtils.split(args, argsSeparator));
			}
			
			try {
				pageContext.getOut().write(codeName);
			} catch (IOException e) {
				// not handle
			}
		}
		
		return super.doStartTag();
	}
	
	public String getCodeType() {
		return codeType;
	}
	
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getCodeKey() {
		return codeKey;
	}

	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}

	public Object getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(Object codeValue) {
		this.codeValue = codeValue;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getArgsSeparator() {
		return argsSeparator;
	}

	public void setArgsSeparator(String argsSeparator) {
		this.argsSeparator = argsSeparator;
	}
}

//public class LabelTag extends org.springframework.web.servlet.tags.form.LabelTag {
//	
//	/** 码表类型 */
//	private String codeType;
//	/** 码表键 */
//	private String codeKey;
//	/** 码表值 */
//	private Object codeValue;
//	
//	/** 格式化参数分隔符 */
//	public final static String ARGS_SEPARATOR = ",";
//	/** 格式化参数 */
//	private String args;
//	
//	protected int writeTagContent(TagWriter tagWriter) throws JspException {
//		int instruct = super.writeTagContent(tagWriter);
//		
//		if (StringUtils.isNotBlank(codeType)) {
//			String codeName = null;
//			if (StringUtils.isNotBlank(codeKey)) {
//				codeName = CodeTable.getName(codeType, codeKey);
//			} 
//			else if (codeValue != null) {
//				codeName = CodeTable.getNameByValue(codeType, codeValue.toString());
//			} 
//			else {
//				Object value = this.getBindStatus().getValue();
//				codeName = CodeTable.getNameByValue(codeType, (value != null ? value.toString() : null));
//			}
//			
//			if (StringUtils.isNotBlank(codeName) && args != null && args.length() > 0) {
//				codeName = MessageFormat.format(codeName, StringUtils.split(args, ARGS_SEPARATOR));
//			}
//			
//			tagWriter.appendValue(this.getDisplayString(codeName));
//		}
//		
//		return instruct;
//	}
//
//	public String getCodeType() {
//		return codeType;
//	}
//	
//	public void setCodeType(String codeType) {
//		this.codeType = codeType;
//	}
//
//	public String getCodeKey() {
//		return codeKey;
//	}
//
//	public void setCodeKey(String codeKey) {
//		this.codeKey = codeKey;
//	}
//
//	public Object getCodeValue() {
//		return codeValue;
//	}
//
//	public void setCodeValue(Object codeValue) {
//		this.codeValue = codeValue;
//	}
//
//	public String getArgs() {
//		return args;
//	}
//
//	public void setArgs(String args) {
//		this.args = args;
//	}
//}