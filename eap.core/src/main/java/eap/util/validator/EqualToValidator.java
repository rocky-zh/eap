package eap.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.ObjectUtils;

import eap.util.ReflectUtil;

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
public class EqualToValidator implements ConstraintValidator<EqualTo, Object> {

	private String fromProperty;
	private String toProperty;

	public void initialize(EqualTo constraintAnnotation) {
		this.fromProperty = constraintAnnotation.from();
		this.toProperty = constraintAnnotation.to();
	}

	public boolean isValid(Object target, ConstraintValidatorContext constraintValidatorContext) {
		Object toPropertyValue = ReflectUtil.getFieldValue(target, toProperty);
		if ( toPropertyValue == null || toPropertyValue.toString().length() == 0) {
			return true;
		}
		
		Object fromPropertyValue = ReflectUtil.getFieldValue(target, fromProperty);
		if (ObjectUtils.equals(toPropertyValue, fromPropertyValue)) {
			return true;
		}
		
		return false;
	}
}