package eap.util.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultMessageCodesResolver;

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
@SuppressWarnings("serial")
public class ValidateMessageCodesResolver extends DefaultMessageCodesResolver {
	
	public String[] resolveMessageCodes(String errorCode, String objectName) {
		return new String[] {
			postProcessMessageCode(objectName + CODE_SEPARATOR + errorCode),
			postProcessMessageCode(errorCode)
		};
	}
	
	public String[] resolveMessageCodes(String errorCode, String objectName, String field, Class<?> fieldType) {
		List<String> codeList = new ArrayList<String>();
		List<String> fieldList = new ArrayList<String>();
		buildFieldList(field, fieldList);
		for (String fieldInList : fieldList) {
			if (StringUtils.hasText(fieldInList)) {
				codeList.add(postProcessMessageCode(objectName + CODE_SEPARATOR + fieldInList + CODE_SEPARATOR + errorCode));
			} else {
				codeList.add(postProcessMessageCode(objectName + CODE_SEPARATOR + errorCode));
			}
		}
		int dotIndex = field.lastIndexOf('.');
		if (dotIndex != -1) {
			buildFieldList(field.substring(dotIndex + 1), fieldList);
		}
		for (String fieldInList : fieldList) {
			if (StringUtils.hasText(fieldInList)) {
				codeList.add(postProcessMessageCode(fieldInList + CODE_SEPARATOR + errorCode));
			}
		}
		if (fieldType != null) {
			codeList.add(postProcessMessageCode(fieldType.getName() + CODE_SEPARATOR + errorCode));
		}
		codeList.add(postProcessMessageCode(errorCode));
		return StringUtils.toStringArray(codeList);
	}
}
