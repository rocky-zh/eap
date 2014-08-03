package eap.util.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * <p> Title: 比较</p>
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
@Documented
@Constraint(validatedBy = EqualToValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface EqualTo {
	
	String message() default "{validator.constraints.EqualTo.message}";
	
	String from();
	String to();
	
	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

	@Target({ TYPE })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		EqualTo[] value();
	}
}
