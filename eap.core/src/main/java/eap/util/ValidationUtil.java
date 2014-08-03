package eap.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import eap.util.validator.Alphabetic;
import eap.util.validator.Alphanumeric;
import eap.util.validator.AsciiCharacter;
import eap.util.validator.CarNumber;
import eap.util.validator.ChineseCharacter;
import eap.util.validator.DateTime;
import eap.util.validator.MobileNumber;
import eap.util.validator.Money;
import eap.util.validator.PostCode;
import eap.util.validator.Required;
import eap.util.validator.TelephoneNumber;

/**
 * <p> Title: </p>
 * <p> Description: 
 * <pre>
 * {@link Required} 必填
 * {@link Length} 字符长度
 * {@link Size} 数值长度
 * {@link Range} 数值区间
 * {@link Min} 数值最小值
 * {@link Max} 数值最大值
 * {@link Digits} 数字
 * {@link Email} 电子邮箱
 * {@link URL} URL
 * {@link CreditCardNumber} 信用卡号
 * {@link DateTime} 日期时间
 * {@link MobileNumber} 手机号码
 * {@link CarNumber} 车牌号
 * {@link TelephoneNumber} 电话号码
 * {@link Money} 金额
 * {@link ChineseCharacter} 中文
 * {@link AsciiCharacter} ASCII
 * {@link PostCode} 邮政编码
 * {@link Alphabetic} 大小写字母
 * {@link Alphanumeric} 大小写字符和数字
 * {@link Enum} 枚举
 * </pre>
 * </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本  修改人    修改时间    修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class ValidationUtil {
	
	public static String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
	public static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
	public static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
	public static final Pattern EMAIL_PATTERN = Pattern.compile("^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$", Pattern.CASE_INSENSITIVE);
	public static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^1[3458][0-9]{9}$");
	public static final Pattern CAR_NUMBER_PATTERN = Pattern.compile("^[\u4e00-\u9fa5][a-zA-Z][a-zA-Z0-9]{5}$");
//	public static final Pattern TELEPHONE_NUMBER_PATTERN = Pattern.compile("^(0[0-9]{2,3}-)?([2-9][0-9]{6,7})+(-[0-9]{1,4})?$|^[48]0{2}[0-9]{7}$|^[48]0{2}-[0-9]{3}-[0-9]{4}$");
	public static final Pattern TELEPHONE_NUMBER_PATTERN = Pattern.compile("^[0-9-]{5,18}$");
	public static final Pattern CHINESE_CHARACTER_PATTERN = Pattern.compile("^[\u4e00-\u9fa5]*$");
	public static final Pattern ASCII_CHARACTER_PATTERN = Pattern.compile("^[\\x20-\\x7F]*$");
	public static final Pattern POST_CODE_PATTERN = Pattern.compile("^[1-9][0-9]{5}$");
	public static final Pattern ALPHABETIC_PATTERN= Pattern.compile("^[a-zA-Z]*$");
	public static final Pattern ALPHANUMERIC_PATTERN= Pattern.compile("^[a-zA-Z0-9]*$");
	public static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-_]{6,16}$");
	public static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]*$");
	public static final Pattern SPECIALCHAR_PATTERN = Pattern.compile("^[^a-zA-Z0-9]*$");
	public static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}$");
	
	private static Validator validator;
	
	public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		return getValidator().validate(object, groups);
	}
	public static List<String[]> validateAsErrorList(Object object, Class<?>... groups) {
		Set<ConstraintViolation<Object>> result = validate(object, groups);
		if (!result.isEmpty()) {
		    List<String[]> validErrors = new ArrayList<String[]>(result.size());
			for (Iterator<ConstraintViolation<Object>> it = result.iterator(); it.hasNext();) {
				ConstraintViolation<Object> violation = it.next();
				validErrors.add(new String[] {violation.getPropertyPath().toString(), violation.getMessage()});
			}
			
			return validErrors;
		}
		
		return Collections.EMPTY_LIST;
	}
	public static String validateAsMessageString(Object object, Class<?>... groups) {
		Set<ConstraintViolation<Object>> result = validate(object, groups);
		if (!result.isEmpty()) {
			StringBuilder sb = new StringBuilder("data is invalid: ");
			for (Iterator<ConstraintViolation<Object>> it = result.iterator(); it.hasNext();) {
				ConstraintViolation<Object> violation = it.next();
				sb.append(violation.getMessage());
				if (it.hasNext()) {
					sb.append("; ");
				}
			}
			return sb.toString();
		}
		
		return null;
	}
	
	public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
		return getValidator().validateProperty(object, propertyName, groups);
	}
	
	public static Set<ConstraintDescriptor<?>> getConstraintsForProperty(Class<?> targetClass, String property) {
		PropertyDescriptor propertyDescriptor = getValidator().getConstraintsForClass(targetClass).getConstraintsForProperty(property);
		if (propertyDescriptor != null) {
			return propertyDescriptor.getConstraintDescriptors();
		}
		
		return Collections.EMPTY_SET;
	}
	
	public static boolean hasConstraintsForProperty(Class<?> targetClass, String property, Class<?>[] constraintClasss) {
		Set<ConstraintDescriptor<?>> constraintDescriptors = getConstraintsForProperty(targetClass, property);
		for (ConstraintDescriptor<?> constraintDescriptor : constraintDescriptors) {
			Class<?> annotationType = ReflectUtil.getFieldValue(constraintDescriptor, "annotationType", Class.class);
			if (ArrayUtils.contains(constraintClasss, annotationType)) { // TODO if (annotationType.isAssignableFrom(constraintClass)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Map<String, Map<String, Object>> getInputStyles(Class<?> targetClass) {
		Map<String, Map<String, Object>> inputStyles = new HashMap<String, Map<String, Object>>() {
			@Override
			public Map<String, Object> get(Object key) {
				Map<String, Object> value = super.get(key);
				if (value == null) {
					value = new HashMap<String, Object>();
					super.put(key != null ? key.toString(): null, value);
				}
				
				return value;
			}
		};
		
		BeanDescriptor beanDescriptor = getValidator().getConstraintsForClass(targetClass);
//		for (ConstraintDescriptor<?> constraintDescriptor : beanDescriptor.getConstraintDescriptors()) {
//			Object constraint = constraintDescriptor.getAnnotation();
//			if (constraint instanceof EqualTo) {
//				EqualTo equalToConstraint = (EqualTo) constraint;
//				String from = equalToConstraint.from();
//				String to = equalToConstraint.to();
//				inputStyles.get(to).put("equalTo", from);
//			}
//		}
		
		Set<PropertyDescriptor> constrainedProperties = beanDescriptor.getConstrainedProperties();
		for (PropertyDescriptor propertyDescriptor : constrainedProperties) {
			String propertyName = propertyDescriptor.getPropertyName();
			Map<String, Object> fieldStyles = inputStyles.get(propertyName);	
			for (ConstraintDescriptor<?> constraintDescriptor : propertyDescriptor.getConstraintDescriptors()) {
				Object constraint = constraintDescriptor.getAnnotation();
				if (constraint instanceof Required) {
					Required requiredConstraint = (Required) constraint;
//					fieldStyles.put("required", StringUtil.defaultIfBlank(requiredConstraint.script(), true));
					fieldStyles.put("required", true);
				}
				else if (constraint instanceof NotEmpty || constraint instanceof NotBlank || constraint instanceof NotNull) {
					fieldStyles.put("required", true);
				} 
				else if (constraint instanceof Digits) {
					Digits digitsConstraint = (Digits) constraint;
					int fraction = digitsConstraint.fraction();
					if (fraction > 0) {
						fieldStyles.put("number", true);
					} else {
						fieldStyles.put("digits", true);
					}
				}
				else if (constraint instanceof Size) {
					Size sizeConstraint = (Size) constraint;
					int min = sizeConstraint.min();
					int max = sizeConstraint.max();
					if (min > 0 && max < Integer.MAX_VALUE) {
						fieldStyles.put("rangelength", new Integer[] {min, max});
					} else {
						if (min > 0) {
							fieldStyles.put("minlength", min);
						} else if (max < Integer.MAX_VALUE) {
							fieldStyles.put("maxlength", max);
						}
					}
				}
				else if (constraint instanceof Length) {
					Length lengthConstraint = (Length) constraint;
					int min = lengthConstraint.min();
					int max = lengthConstraint.max();
					if (min > 0 && max < Integer.MAX_VALUE) {
						fieldStyles.put("rangelength", new Integer[] {min, max});
					} else {
						if (min > 0) {
							fieldStyles.put("minlength", min);
						} else if (max < Integer.MAX_VALUE) {
							fieldStyles.put("maxlength", max);
						}
					}
				}
				else if (constraint instanceof Range) {
					Range rangeConstraint = (Range) constraint;
					long min = rangeConstraint.min();
					long max = rangeConstraint.max();
					if (min > 0 && max < Long.MAX_VALUE) {
						fieldStyles.put("range", new Long[] {min, max});
					} else {
						if (min > 0) {
							fieldStyles.put("min", min);
						} else if (max < Long.MAX_VALUE) {
							fieldStyles.put("max", max);
						}
					}
				}
				else if (constraint instanceof Min) {
					Min minConstraint = (Min) constraint;
					long value = minConstraint.value();
					if (value > 0) {
						fieldStyles.put("min", value);
					}
				}
				else if (constraint instanceof Max) {
					Max maxConstraint = (Max) constraint;
					long value = maxConstraint.value();
					fieldStyles.put("max", value);
				}
				else if (constraint instanceof Email) {
					fieldStyles.put("email", true);
				}
				else if (constraint instanceof DateTime) {
					DateTime dateTimeConstraint = (DateTime) constraint;
					String format = dateTimeConstraint.formatType().getPattern();
					
					Map<String, Object> dtOpts = new HashMap<String, Object>();
					dtOpts.put("format", format);
					
					fieldStyles.put("datetime", dtOpts);
				}
				else if (constraint instanceof CreditCardNumber) {
					fieldStyles.put("creditcard", true);
				}
				else if (constraint instanceof URL) {
					fieldStyles.put("url", true);
				}
//				else if (constraint instanceof Kaptcha) {
//					fieldStyles.put("kaptcha", true);
//				}
				else if (constraint instanceof MobileNumber) {
					fieldStyles.put("maxlength", 11);
					fieldStyles.put("mobilenumber", true);
				}
				else if (constraint instanceof CarNumber) {
					fieldStyles.put("carnumber", true);
				}
				else if (constraint instanceof TelephoneNumber) {
					fieldStyles.put("telephonenumber", true);
				}
				else if (constraint instanceof ChineseCharacter) {
					fieldStyles.put("chinesecharacter", true);
				} 
				else if (constraint instanceof Money) {
					Money moneyConstraint = (Money) constraint;
					int precision  = moneyConstraint.integer();
					int scale = moneyConstraint.fraction();
					
					fieldStyles.put("money", new Integer[] {precision, scale});
				}
				else if (constraint instanceof PostCode) {
					fieldStyles.put("postcode", true);
				}
				else if (constraint instanceof Alphabetic) {
					fieldStyles.put("alphabetic", true);
				}
				else if (constraint instanceof Alphanumeric) {
					fieldStyles.put("alphanumeric", true);
				}
			}
			if (fieldStyles.size() == 0) {
				inputStyles.remove(propertyName);
			}
		}
		
		return inputStyles;
	}
	
	public static boolean isSpecialChar(String value){
		if(null == value || value.length() == 0){
			return true;
		}
		return SPECIALCHAR_PATTERN.matcher(value).matches();
	}
	
	public static boolean isNumber(String value){
		if(null == value || value.length() == 0){
			return true;
		}
		return NUMBER_PATTERN.matcher(value).matches();
	}
	
	public static boolean isEmail(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = EMAIL_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isMobileNumber(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = MOBILE_NUMBER_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isCarNumber(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = CAR_NUMBER_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isTelephoneNumber(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = TELEPHONE_NUMBER_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isChineseCharacter(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = CHINESE_CHARACTER_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isAsciiCharacter(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = ASCII_CHARACTER_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isPostCode(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = POST_CODE_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isAlphabetic(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = ALPHABETIC_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isAlphanumeric(String value) {
		if ( value == null || value.length() == 0 ) {
			return false;
		}
		Matcher m = ALPHANUMERIC_PATTERN.matcher( value );
		return m.matches();
	}
	
	public static boolean isValidPassword(String password){
		if(password == null || password.length() == 0){
			return false;
		}
		Matcher m = PASSWORD_PATTERN.matcher(password);
		return m.matches();
	}
	
	public static boolean isUuid(String uuid) {
		if(uuid == null || uuid.length() == 0){
			return false;
		}
		return UUID_PATTERN.matcher(uuid).matches();
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isValidID(String idStr){
		if(idStr == null || idStr.length()!=18){
			return false;
		}
		
		String regex = "[0-9]{17}[0-9|xX]$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		if (!pattern.matcher(idStr).matches()) { //前17位数字，第18位数字或字母X
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		Hashtable areacodeHashtable = new Hashtable();
		areacodeHashtable.put("11", "北京");
		areacodeHashtable.put("12", "天津");
		areacodeHashtable.put("13", "河北");
		areacodeHashtable.put("14", "山西");
		areacodeHashtable.put("15", "内蒙古");
		areacodeHashtable.put("21", "辽宁");
		areacodeHashtable.put("22", "吉林");
		areacodeHashtable.put("23", "黑龙江");
		areacodeHashtable.put("31", "上海");
		areacodeHashtable.put("32", "江苏");
		areacodeHashtable.put("33", "浙江");
		areacodeHashtable.put("34", "安徽");
		areacodeHashtable.put("35", "福建");
		areacodeHashtable.put("36", "江西");
		areacodeHashtable.put("37", "山东");
		areacodeHashtable.put("41", "河南");
		areacodeHashtable.put("42", "湖北");
		areacodeHashtable.put("43", "湖南");
		areacodeHashtable.put("44", "广东");
		areacodeHashtable.put("45", "广西");
		areacodeHashtable.put("46", "海南");
		areacodeHashtable.put("50", "重庆");
		areacodeHashtable.put("51", "四川");
		areacodeHashtable.put("52", "贵州");
		areacodeHashtable.put("53", "云南");
		areacodeHashtable.put("54", "西藏");
		areacodeHashtable.put("61", "陕西");
		areacodeHashtable.put("62", "甘肃");
		areacodeHashtable.put("63", "青海");
		areacodeHashtable.put("64", "宁夏");
		areacodeHashtable.put("65", "新疆");
		areacodeHashtable.put("71", "台湾");
		areacodeHashtable.put("81", "香港");
		areacodeHashtable.put("82", "澳门");
		areacodeHashtable.put("91", "国外");
		
		if (areacodeHashtable.get(idStr.substring(0, 2))==null) {
			return false;
		}
		
		String birthdate = idStr.substring(6, 14);//获取生日
		int birthyear = Integer.parseInt(birthdate.substring(0, 4));//出生年份
		int curyear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);//当前时间年份
		if (birthyear >= curyear || birthyear < 1900) { //不在1900年与当前时间之间
			return false;
		}
		
		String dateRegex = "^((\\d{2}(([02468][048])|([13579][26]))[\\/\\/\\s]?((((0 ?[13578])|(1[02]))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\/\\/\\s]?((((0?[13578])|(1[02]))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		if (!Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matches(dateRegex, birthdate)) { //出生年月日不合法
			return false;
		}
		
		/*
		 * 计算校验码（第十八位数）：
		 * 十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0...16 ，先对前17位数字的权求和；
		 * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2；
		 * 计算模 Y = mod(S, 11)
		 * 通过模Y得到对应的校验码: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9 8 7 6 5 4 3 2
		 */
		final String[] LASTCODE = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };// 18位身份证中最后一位校验码
		final int[] WEIGHT = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };// 18位身份证中，前17位数字各个数字的生成校验码时的权值
		String tempLastCode = "";//临时记录身份证号码最后一位
		int sum = 0;//前17位号码与对应权重值相乘总和
		for(int i=0; i<17; i++){
			sum += ((int)(idStr.charAt(i)-'0'))*WEIGHT[i];
		}
		tempLastCode = LASTCODE[sum%11];//实际最后一位号码
		if (idStr.substring(17).equals(tempLastCode)) {//最后一位符合
			return true;
		} else {
			return false;
		}
	}
	
	public static Validator getValidator() {
		if (validator == null) {
			validator = Validation.buildDefaultValidatorFactory().getValidator();
		}
		
		return validator;
	}
	public void setValidator(Validator v) {
		validator = v;
	}
	
	public static void main(String[] args) {
//		System.out.println(isValidIDCardNum("130322198602250016"));
//		System.out.println("isEmail: chiknin@gmail.com => " + isEmail("中文jj@gmail.com"));
//		System.out.println("isEmail: @gmail.com => " + isEmail("@gmail.com"));
		System.out.println("isMobileNumber: 18659208697 => " + isMobileNumber("1225920869"));
//		System.out.println("isMobileNumber: 110 => " + isMobileNumber("110"));
//		System.out.println("isCarNumber: 闽D12345 => " + isCarNumber("闽D12345"));
//		System.out.println("isCarNumber: xxxxxxx => " + isCarNumber("xxxxxxx"));
		
//		String i = "0210-2234567-1234";
//		String i = "400-40-01000";
//		String i = "80041232";
		
//		System.out.println("isTelephoneNumber: " + TELEPHONE_NUMBER_PATTERN.matcher(i).matches());
		
//		System.out.println(isAsciiCharacter("qa12KJ!@zho中#$%^&*("));
//		System.out.println(isAlphanumeric("asdLIJ1212"));
		
//		System.out.println(ValidationUtil.isMobileNumber("13521998208"));
		
//		System.out.println(ValidationUtil.isValidPassword("-_-_-_"));
	}
}