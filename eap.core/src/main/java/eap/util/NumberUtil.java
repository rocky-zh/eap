package eap.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
public class NumberUtil {
	
	public static BigDecimal round(BigDecimal num, int scale) {
		String numStr = num.toString();
		int idx = numStr.lastIndexOf('.');
		scale = (idx > 0) ? Math.min((numStr.length() - (idx + 1)), scale) : 0;
		
		return num.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}
	
	public static BigDecimal roundMoney(BigDecimal num) {
		return  num.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	public static BigDecimal roundMoneyWan(BigDecimal num) {
		return num.divide(new BigDecimal(10000), 0, BigDecimal.ROUND_HALF_UP);
	}
	
	public static Double divide(Double num, Integer dividend) {
		if (num == null) {
			return null;
		}
		if (num == 0) {
			return 0D;
		}
		
		return new BigDecimal(num).divide(new BigDecimal(dividend), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static Double divide100(Double num) {
		if (num == null) {
			return null;
		}
		if (num == 0) {
			return 0D;
		}
		
		return new BigDecimal(num).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static Double divide100(Integer num) {
		if (num == null) {
			return null;
		}
		if (num.intValue() == 0) {
			return Double.valueOf(0.0D);
		}
		
		return Double.valueOf(new BigDecimal(num.intValue()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public static Double multiply100(Double num) {
		if (num == null) {
			return null;
		}
		if (num.doubleValue() == 0.0D) {
			return Double.valueOf(0.0D);
		}
		
		return Double.valueOf(new BigDecimal(num.doubleValue()).multiply(new BigDecimal(100)).doubleValue());
	}
	
	public static String format(BigDecimal num, String pattern) {
		return new DecimalFormat(pattern).format(num);
	}
	
	public static String formatMoney(BigDecimal num) {
		return new DecimalFormat("#,###.##").format(num);
	}
	public static String formatMoney00(BigDecimal num) {
		return new DecimalFormat("#,###.00").format(num);
	}
	public static String formatMoneyWan(BigDecimal num) {
		return new DecimalFormat("#,###.####").format(num.divide(new BigDecimal(10000), 4, BigDecimal.ROUND_HALF_UP));
	}
	
	public static BigDecimal toBigDecimal(Double num) {
		if (num != null) {
			return new BigDecimal(num); // .setScale(2, BigDecimal.ROUND_HALF_UP)
		}
		
		return null;
	}
	
	public static Double toDouble(BigDecimal num) {
		if (num != null) {
			return num.doubleValue();
		}
		
		return null;
	}
	
	public static double[] calRate(long[] nums) {
		double total = 0;
		for (int i = 0; i < nums.length; i++) {
			total += nums[i];
		}
		
		double[] result = new double[nums.length];
		for (int i = 0; i < nums.length; i++) {
			if (total == 0) {
				result[i] = 0;
			} else {
				double r = (nums[i] / total) * 100.0;
				result[i] = NumberUtil.round(new BigDecimal(r), 2).doubleValue();
			}
		}
		
		return result;
	}
	
	/** 汉语中数字大写 */
	private static final String[] CN_UPPER_NUMBER = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	/** 汉语中货币单位大写，这样的设计类似于占位符 */
	private static final String[] CN_UPPER_MONETRAY_UNIT = { "分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟" };
	/** 特殊字符：整 */
	private static final String CN_FULL = "整";
	/** 特殊字符：负 */
	private static final String CN_NEGATIVE = "负";
	/** 金额的精度，默认值为2 */
	private static final int MONEY_PRECISION = 2;
	/** 特殊字符：零元整 */
	private static final String CN_ZEOR_FULL = "零元" + CN_FULL;
	
	/** 
	* 把输入的金额转换为汉语中人民币的大写
	*  
	* @param numberOfMoney 输入的金额
	* @return 对应的汉语大写
	*/
	public static String toChineseMonetray(BigDecimal numberOfMoney) {
		StringBuffer sb = new StringBuffer();
		int signum = numberOfMoney.signum();
		
		if (signum == 0) { // 零元整的情况
			return CN_ZEOR_FULL;
		}
		
		long number = numberOfMoney.movePointRight(MONEY_PRECISION).setScale(0, 4).abs().longValue(); //这里会进行金额的四舍五入
		long scale = number % 100; // 得到小数点后两位值
		int numUnit = 0;
		int numIndex = 0;
		boolean getZero = false;
		if (!(scale > 0)) { // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
			numIndex = 2;
			number = number / 100;
			getZero = true;
		}
		if ((scale > 0) && (!(scale % 10 > 0))) {
			numIndex = 1;
			number = number / 10;
			getZero = true;
		}
		int zeroSize = 0;
		while (true) {
			if (number <= 0) {
				break;
			}
			
			numUnit = (int) (number % 10); // 每次获取到最后一个数
			if (numUnit > 0) {
				if ((numIndex == 9) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
				}
				if ((numIndex == 13) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
				}
				sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				getZero = false;
				zeroSize = 0;
			} else {
				++zeroSize;
				if (!(getZero) && numIndex != 2) { // numIndex != 2
					sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				}
				if (numIndex == 2) {
					if (number > 0) {
						sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
					}
				} else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				}
				getZero = true;
			}
			
			number = number / 10; // 让number每次都去掉最后一个数
			++numIndex;
		}
		
		if (signum == -1) { // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
			sb.insert(0, CN_NEGATIVE);
		}
		
		if (!(scale > 0)) { // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
			sb.append(CN_FULL);
		}
		
		return sb.toString();
	}
	
	/* 多进制 */
	
	private static final char[] MULTI_DECIMAL_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private static final int MULTI_DECIMAL_CHARS_LENGTH = MULTI_DECIMAL_CHARS.length;
	private static final Map<Character, Integer> MULTI_DECIMAL_CHARS_INDEX = new HashMap<Character, Integer>();
	static {
		for (int i = 0; i < MULTI_DECIMAL_CHARS.length; i++) {
			MULTI_DECIMAL_CHARS_INDEX.put(MULTI_DECIMAL_CHARS[i], i);
		}
	}
	public static String toMultiDecimal(Long num) {
		if (num == null) {
			return null;
		}
		if (num < 0) {
			throw new IllegalArgumentException("'num' must be greater than 0");
		}
		
		StringBuilder result = new StringBuilder();
		long n1 = num;
		do {
			result.insert(0, MULTI_DECIMAL_CHARS[(int) (n1 % MULTI_DECIMAL_CHARS_LENGTH)]);
			n1 = n1 / MULTI_DECIMAL_CHARS_LENGTH;
		} while (n1 > 0);
		
		return result.toString();
	}
	
	public static Long parseMultiDecimal(String multiDeciaml) {
		if (multiDeciaml == null || multiDeciaml.length() == 0) {
			return null;
		}
		
		Long result = 0L;
		for (int i = multiDeciaml.length() -1, j = 0; i >= 0; i--, j++) {
			int n1 = MULTI_DECIMAL_CHARS_INDEX.get(multiDeciaml.charAt(i));
			result += (long)(n1 * Math.pow(Double.parseDouble(MULTI_DECIMAL_CHARS_LENGTH + ""), Double.parseDouble(j+"")));
		}
		
		return result;
	}
	
	public static void main(String[] args) {
//		System.out.println(round(new BigDecimal(12345.678), 3));
//		System.out.println(roundMoney(new BigDecimal(12345.678)));
//		System.out.println(format(new BigDecimal(12345.678), '#,###.00"));
//		System.out.println(formatMoney(new BigDecimal(12345.001)));
//		System.out.println(formatMoney00(new BigDecimal(12345.001)));
//		
//		System.out.println(formatMoneyWan(new BigDecimal(126400.4)));
//		System.out.println(roundMoneyWan(new BigDecimal(12345.001)));
//		System.out.println(NumberUtil.round(new BigDecimal(1120900).divide(new BigDecimal(10000)), 4));
//		System.out.println(new BigDecimal(12342.02).divide(new BigDecimal(10000)));
//		BigDecimal d = new BigDecimal(1).divide(new BigDecimal(1.2), 2, BigDecimal.ROUND_HALF_UP);
//		System.out.println(1.0d / 1.2d);
//		System.out.println(d);
//		
//		Double d1 = 99999999d;
//		BigDecimal d2 = new BigDecimal(99999999);
//		System.out.println(d2);
		
		System.out.println(toChineseMonetray(new BigDecimal(1000200200.33)));
//		for (int i = 0; i <= MULTI_DECIMAL_CHARS_LENGTH + 1000; i++) {
		long i = 356194432229310464L;
		System.out.println(i + " = " + toMultiDecimal(new Long(i)) +" == " + parseMultiDecimal(toMultiDecimal(new Long(i))));
		
		System.out.println(divide100(123456));
		
//		}
	}
}