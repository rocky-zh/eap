package eap.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eap.util.ValidationUtil;

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
public class AlphabeticValidator implements ConstraintValidator<Alphabetic, Object> {

	@Override
	public void initialize(Alphabetic kaptcha) {
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if ( value == null || value.toString().length() == 0 ) {
			return true;
		}
		
		return ValidationUtil.isAlphabetic(value.toString());
	}
}
