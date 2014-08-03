package eap.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本	   修改人		 修改时间		 修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class StringUtil extends StringUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
	
	public static final int MOBILE_NUMBER_LENGTH = 11;
	
	public static BigDecimal toBigDecimal(String str) {
		if (isBlank(str)) {
			return null;
		}
		
		try {
			return new BigDecimal(str);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}
	
	public static Integer toInteger(String str) {
		if (isBlank(str) || !isNumeric(str)) {
			return null;
		}
		
		try {
			return new Integer(str);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}
	
	public static Long toLong(String str) {
		if (isBlank(str) || !isNumeric(str)) {
			return null;
		}
		
		try {
			return new Long(str);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}
	
	public static Double toDouble(String str) {
		if (isBlank(str) || !isNumeric(str)) {
			return null;
		}
		
		try {
			return new Double(str);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}
	
	public static String[] regexGroup(String regex, String str) {
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE); //"");
		Matcher m = p.matcher(str);
		if (m.find()) {
			String[] result = new String[m.groupCount() + 1];
			for (int i = 0; i <= m.groupCount(); i++) {
				result[i] = m.group(i);
			}
			
			return result;
		}
		
		return null;
	}
	public static String regexGroup(String regex, String str, int groupIndex) {
		String[] groups = regexGroup(regex, str);
		if (groups != null && groups.length >= groupIndex) {
			return groups[groupIndex];
		}
		
		return null;
	}
	
	public static Object defaultIfBlank(String str, Object defaultValue) {
		if (isNotBlank(str)) {
			return str;
		}
		
		return defaultValue;
	}
	
	public static String mapUnderscoreToCamelCase(String str) {
		char c = '_';
		
		String[] strArr = split(str, c);
		if (strArr.length > 0) {
			StringBuilder buf = new StringBuilder();
			buf.append(strArr[0]);
			for (int i = 1; i < strArr.length; i++) {
				buf.append(capitalize(strArr[i]));
			}
			
			return buf.toString();
		}
		
		return null;
	}
	
	public static String getFirstCharByUnderscore(String str) {
		char c = '_';
		
		String[] strArr = split(str, c);
		if (strArr.length > 0) {
			StringBuilder buf = new StringBuilder();
			for (String s : strArr) {
				if (s != null && s.length() > 0) {
					buf.append(s.charAt(0));
				}
			}
			
			return buf.toString();
		}
		
		return null;
	}
	
	public static String indexOf(String str, String startPattern, String endPattern) {
		return substrBB(str, startPattern, endPattern);
	}
	public static String substrBB(String str, String startPattern, String endPattern) {
		if (StringUtil.isBlank(str)) {
			return null;
		}
		
		int sidx = str.indexOf(startPattern);
		if (sidx == -1) return null;
		int eidx = str.indexOf(endPattern, sidx + startPattern.length());
		if (eidx == -1) return null;
		if (sidx > eidx) return null;
		return str.substring(sidx + startPattern.length(), eidx).trim();
	}
	
	public static String lastIndexOf(String str, String startPattern, String endPattern) {
		return substrAA(str, startPattern, endPattern);
	}
	public static String substrAA(String str, String startPattern, String endPattern) {
		int sidx = str.lastIndexOf(startPattern);
		if (sidx == -1) return null;
		int eidx = str.lastIndexOf(endPattern);
		if (eidx == -1) return null;
		if (sidx > eidx) return null;
		return str.substring(sidx + startPattern.length(), eidx);
	}
	
	public static String substrBA(String str, String startPattern, String endPattern) {
		if (StringUtil.isBlank(str)) {
			return null;
		}
		
		int sidx = str.indexOf(startPattern);
		if (sidx == -1) return null;
		int eidx = str.lastIndexOf(endPattern);
		if (eidx == -1) return null;
		if (sidx > eidx) return null;
		return str.substring(sidx + startPattern.length(), eidx).trim();
	}
	
	public static String substr(String str, String pattern) {
		if (str == null || str.length() == 0) {
			return null;
		}
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		boolean result = matcher.find();
		if (result) {
			return matcher.group(1);
		}
		
		return null;
	}
	
	public static final String ELLIPSIS_CHARS = "...";
	public static String ellipsis(String str, int showCharLen, String ellipsisChars) {
		if (str == null) {
			return null;
		}
		
		if (str.length() > showCharLen) {
			return str.substring(0, showCharLen) + ellipsisChars;
		}
		
		return str;
	}
	public static String ellipsis(String str, int showCharLen) {
		return ellipsis(str, showCharLen, ELLIPSIS_CHARS);
	}
	
	public static String blanchMobileNumber(String mobileNumber) {
		if (mobileNumber == null) {
			return null;
		}
		
		if (mobileNumber.length() != MOBILE_NUMBER_LENGTH) {
			return mobileNumber;
		}
		return mobileNumber.substring(0, 3) + "****" + mobileNumber.substring(7);
	}
	
	public static String blanchEmail(String email) {
		if (email == null) {
			return null;
		}
		
		int atIdx = email.indexOf("@");
		if (atIdx == -1) {
			return email;
		}
		
		return leftPad(email.substring(atIdx), email.length(), "*");
	}
	
	public static String getEmailUserName(String email) {
		if (email == null) {
			return null;
		}
		
		int atIdx = email.indexOf("@");
		if (atIdx == -1) {
			return null;
		}
		
		return email.substring(0, atIdx);
	}
	
	public static boolean isUtf8String(byte[] data) {
		if (data == null || data.length == 0) {
			return true;
		}
		
		int count_good_utf = 0;  
		int count_bad_utf = 0;  
		byte current_byte = 0x00;  
		byte previous_byte = 0x00;  
		for (int i = 1; i < data.length; i++) {  
			current_byte = data[i];  
			previous_byte = data[i - 1];  
			if ((current_byte & 0xC0) == 0x80) {  
				if ((previous_byte & 0xC0) == 0xC0) {  
					count_good_utf++;  
				} else if ((previous_byte & 0x80) == 0x00) {  
					count_bad_utf++;  
				}  
			} else if ((previous_byte & 0xC0) == 0xC0) {  
				count_bad_utf++;  
			}  
		}  
		
		return (count_good_utf > count_bad_utf);  
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
//		System.out.println(getFirstCharByUnderscore("t_biz_car_ins_serial"));
		
//		System.out.println(isUtf8String("中国".getBytes()));
//		System.out.println(isUtf8String("中国".getBytes("UTF-8")));
//		System.out.println(isUtf8String("中国".getBytes("GBK")));
		
		String[] ss = {
//				"aaaa"
//				"aaaabbbb"
//				"ababbaba"
//				"abbabb"
//				"abbabbceecee"
//				"aabaab"
//				,"000000"
//				,"1s00000000d00000000"
//				"112233112233112233" // 3-1
//				,"0054321"
//				"654321"
//				,"00abcde"
//				,"00ABCDE"
//				,"00AbCdE"
//				,"111222333111222333" // 3+1
//				"111112222233333" // 3+1
//				"111222111222333444" ,
//				,"332211332211"
				"111222"
		};
		
//		System.out.println(regexGroup("\\.(\\w+)\\.", "com.infd.0001")[1]);
//		System.out.println(regexGroup("\\.(\\w+)\\.", "com.infd.0001", 1));
		
//		System.out.println(blanchMobileNumber("18659208697"));
//		System.out.println(blanchEmail("chiknin@gmail.com"));
//		System.out.println(getEmailUserName("chiknin@gmail.com"));
//		
//		String s = "<Document>  <CommercialDraftCommonTransmission>   <MsgId> <ReqInf>   <CommercialDraftRediscountWithCommercialBankRequest> </MsgId><Id>30 ";
//		System.out.println(indexOf(s, "<MsgId>", "</MsgId>"));
		
//		String s = ellipsis("我要取的很长很长的网名", 5);
//		System.out.println(s);
//		System.out.println(toInteger("595213"));
		
		System.out.println(RandomStringUtils.random(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()"));
	}
}