package eap.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.NotImplementedException;

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
public class EnumValidator implements ConstraintValidator<Enum, Object> {
	
	private String[] list;
	private Class<?> enumClass;

	@Override
	public void initialize(Enum constraintAnnotation) {
		list = constraintAnnotation.list();
		enumClass = constraintAnnotation.enumClass();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if ( value == null || value.toString().length() == 0 ) {
			return true;
		}
		else if (list != null && list.length > 0) {
			for (String item : list) {
				if (value.toString().equals(item)) {
					return true;
				}
			}
			
			return false;
		}
		else if (enumClass != null && enumClass.isEnum()) {
			throw new NotImplementedException();
		}
		
		return false;
	}
}