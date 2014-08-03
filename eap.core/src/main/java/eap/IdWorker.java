package eap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


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
 * @see https://github.com/twitter/snowflake
 */
public class IdWorker {

	private final long workerId;
	private final long twepoch = 1303895660503L;
	private long sequence = 0L;
	private final long workerIdBits = 10L;
	private final long maxWorkerId = -1L ^ -1L << this.workerIdBits;
	private final long sequenceBits = 12L;

	private final long workerIdShift = this.sequenceBits;
	private final long timestampLeftShift = this.sequenceBits + this.workerIdBits;
	private final long sequenceMask = -1L ^ -1L << this.sequenceBits;

	private long lastTimestamp = -1L;

	public IdWorker(long workerId) {
		super();
		if (workerId > this.maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
		}
		this.workerId = workerId;
	}

	public synchronized long nextId() {
		long timestamp = this.timeGen();
		if (this.lastTimestamp == timestamp) {
			this.sequence = this.sequence + 1 & this.sequenceMask;
			if (this.sequence == 0) {
				timestamp = this.tilNextMillis(this.lastTimestamp);
			}
		} else {
			this.sequence = 0;
		}
		if (timestamp < this.lastTimestamp) {
			throw new IllegalStateException(
					String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
		}

		this.lastTimestamp = timestamp;
		return timestamp - this.twepoch << this.timestampLeftShift | this.workerId << this.workerIdShift | this.sequence;
	}

	private long tilNextMillis(long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = this.timeGen();
		}
		return timestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}
	
	/* 
	 * START: nextCharId 
	 * @see eap.util.NumberUtil
	 */
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
	public String nextCharId() {
		return toMultiDecimal(nextId());
	}
	/* 
	 * END: nextCharId 
	 */
	
	public static void main(String[] args) throws InterruptedException {
		IdWorker idWorker = new IdWorker(1);
		for (int i = 0; i < 1000; i++) {
			System.out.println(idWorker.nextId());
			System.out.println(idWorker.nextId());
			System.out.println(idWorker.nextId());
			Thread.sleep((new Random().nextInt(5) + 1) * 1000);
		}
	}

}