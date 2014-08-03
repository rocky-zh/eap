package eap.comps.datamapping.exception;

import java.util.ArrayList;
import java.util.List;

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
public class ValidateFailExceptions extends RuntimeException {
	
	List<ValidateFailException> validateFailExceptions = new ArrayList<ValidateFailException>();

	public ValidateFailExceptions() {
		
	}
	
	public void addException(ValidateFailException validateFailException) {
		this.validateFailExceptions.add(validateFailException);
	}
	public void addException(ValidateFailExceptions validateFailExceptions) {
		this.validateFailExceptions.addAll(validateFailExceptions.getValidateFailExceptions());
	}
	
	public boolean existsException() {
		return validateFailExceptions.size() > 0;
	}

	public List<ValidateFailException> getValidateFailExceptions() {
		return validateFailExceptions;
	}

	public void setValidateFailExceptions(
			List<ValidateFailException> validateFailExceptions) {
		this.validateFailExceptions = validateFailExceptions;
	}
}