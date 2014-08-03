package eap.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

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
public class RequiredValidator implements ConstraintValidator<Required, Object> {

	private String script;

	public void initialize(Required constraintAnnotation) {
//		this.script = constraintAnnotation.script();
	}

	public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
		if ( value == null || StringUtils.isBlank(value.toString())) {
			return false;
		}
		
		return true;
	}
}