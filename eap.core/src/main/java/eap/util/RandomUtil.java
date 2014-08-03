package eap.util;

import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
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
public class RandomUtil {
	
	private final static Random RANDOM = new Random();
	
	public static int nextInt(int n) {
		return RANDOM.nextInt(n);
	}
	
	public static <T> T nextItem(T[] arr) {
		if (arr == null || arr.length == 0) {
			return null;
		}
		
		return arr[RANDOM.nextInt(arr.length)];
	}
	public static String nextItem(String strArr) {
		if (StringUtils.isBlank(strArr)) {
			return null;
		}
		
		return nextItem(StringUtils.split(strArr, ","));
	}
	
	public static String randomNumeric(int count) {
		return RandomStringUtils.randomNumeric(count);
	}
	
	public static String randomAlphanumeric(int count) {
		return RandomStringUtils.randomAlphanumeric(count);
	}
	
	public static String randomAlphabetic(int count) {
		return RandomStringUtils.randomAlphabetic(count);
	}
	
	public static void main(String[] args) {
		String s = RandomStringUtils.random(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()");
		System.out.println(s);
	}
}