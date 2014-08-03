package eap.util.validator;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
public class MoneyValidator implements ConstraintValidator<Money, Object> {

	private int maxIntegerLength;
	private int maxFractionLength;

	public void initialize(Money constraintAnnotation) {
		this.maxIntegerLength = constraintAnnotation.integer();
		this.maxFractionLength = constraintAnnotation.fraction();
		validateParameters();
	}

	public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
		if ( value == null || value.toString().length() == 0) {
			return true;
		}

		BigDecimal bigNum = getBigDecimalValue( value.toString() );
		if ( bigNum == null ) {
			return false;
		}

		int integerPartLength = bigNum.precision() - bigNum.scale();
		int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();

		return ( maxIntegerLength >= integerPartLength && maxFractionLength >= fractionPartLength );
	}

	private BigDecimal getBigDecimalValue(String str) {
		BigDecimal bd;
		try {
			bd = new BigDecimal( str );
		}
		catch ( NumberFormatException nfe ) {
			return null;
		}
		return bd;
	}

	private void validateParameters() {
		if ( maxIntegerLength < 0 ) {
			throw new IllegalArgumentException( "The length of the integer part cannot be negative." );
		}
		if ( maxFractionLength < 0 ) {
			throw new IllegalArgumentException( "The length of the fraction part cannot be negative." );
		}
	}
}
