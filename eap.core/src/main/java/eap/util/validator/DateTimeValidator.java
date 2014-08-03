package eap.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

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
public class DateTimeValidator implements ConstraintValidator<DateTime, Object> {

	private DateTimeFormatType formatType;

	public void initialize(DateTime constraintAnnotation) {
		this.formatType = constraintAnnotation.formatType();
	}

	public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
		if ( value == null || StringUtils.isBlank(value.toString())) {
			return true;
		}
		
		if (value instanceof String) {
			try {
				DateUtil.parse(value.toString(), formatType.getPattern());
			} catch (Exception e) {
				return false;
			}
		}
//		else if (value instanceof java.util.Date) {
//			 // not handle
//		}
		
		return true;
	}
}