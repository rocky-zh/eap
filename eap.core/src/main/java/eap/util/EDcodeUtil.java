package eap.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

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
public class EDcodeUtil {
	
	private static final int ITERATIONS = 1;
	
	private static Provider provider = new BouncyCastleProvider();
	static {
		Security.addProvider(provider);
	}
	
	public static byte[] flashDecode(String data) {
		char[] s = data.toCharArray();
		int len = s.length;
		byte[] r = new byte[len / 2];
		for (int i = 0; i < len; i = i + 2) {
			int k1 = s[i] - 48;
			k1 -= k1 > 9 ? 7 : 0;
			int k2 = s[i + 1] - 48;
			k2 -= k2 > 9 ? 7 : 0;
			r[i / 2] = (byte) (k1 << 4 | k2);
		}
		
		return r;
	}
	
	public static String md5(String data) {
		return md5(data, false);
	}
	public static String md5(String data, boolean hashAsBase64) {
		return encode(data, hashAsBase64, "MD5");
	}
	
	public static String sha1(String data) {
		return encode(data, false, "SHA-1");
	}
	public static String sha1(String data, boolean hashAsBase64) {
		return encode(data, hashAsBase64, "SHA-1");
	}
	
	private static String encode(String data, boolean hashAsBase64, String algorithm) {
		MessageDigest messageDigest = getMessageDigest(algorithm);
		
		byte[] digest = messageDigest.digest(utf8Encode(data));
		
		for (int i = 1; i < ITERATIONS; i++) {
			digest = messageDigest.digest(digest);
		}
		
		if (hashAsBase64) {
			return base64Encode(digest);
		} else {
			return hexEncode(digest);
		}
	}
	private static MessageDigest getMessageDigest(String algorithm) throws IllegalArgumentException {
		try {
			return MessageDigest.getInstance(algorithm, provider);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
		}
	}
	
	public static String base64Encode(String data) {
		return base64Encode(utf8Encode(data)); 
	}
	public static String base64Encode(byte[] data) {
		return utf8Decode(Base64.encodeBase64(data)); 
	}
	public static String base64Decode(String data) {
		return utf8Decode(Base64.decodeBase64(utf8Encode(data)));
	}
	
	public static byte[] genHmacMD5Key() {
		return genHmacKey("HmacMD5");
	}
	public static byte[] genHmacSHA1Key() {
		return genHmacKey("HmacSHA1");
	}
	public static byte[] genHmacSHA256Key() {
		return genHmacKey("HmacSHA256");
	}
	public static byte[] genHmacSHA384Key() {
		return genHmacKey("HmacSHA384");
	}
	public static byte[] genHmacSHA512Key() {
		return genHmacKey("HmacSHA512");
	}
	private static byte[] genHmacKey(String algorithm) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm, provider);
			SecretKey secretKey = keyGenerator.generateKey();
			
			return secretKey.getEncoded();  
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
		}
	}
	
	public static byte[] hmacMD5(byte[] data, byte[] key) {
		return hmac(data, key, "HmacMD5");
	}
	public static byte[] hmacSHA1(byte[] data, byte[] key) {
		return hmac(data, key, "HmacSHA1");
	}
	public static byte[] hmacSHA256(byte[] data, byte[] key) {
		return hmac(data, key, "HmacSHA256");
	}
	public static byte[] hmacSHA384(byte[] data, byte[] key) {
		return hmac(data, key, "HmacSHA384");
	}
	public static byte[] hmacSHA512(byte[] data, byte[] key) {
		return hmac(data, key, "HmacSHA512");
	}
	private static byte[] hmac(byte[] data, byte[] key, String algorithm) {
		try {
			SecretKey secretKey=new SecretKeySpec(key, algorithm);
			
			Mac mac = Mac.getInstance(secretKey.getAlgorithm(), provider);
			mac.init(secretKey);
			
			return mac.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static byte[] desEncode(byte[] data, byte[] key) {
		return des(data, key, Cipher.ENCRYPT_MODE);
	}
	public static String desEncodeAsHex(byte[] data, byte[] key) {
		return hexEncode(des(data, key, Cipher.ENCRYPT_MODE));
	}
	public static String desEncodeAsHex(String data, String key) {
		return hexEncode(des(utf8Encode(data), utf8Encode(key), Cipher.ENCRYPT_MODE));
	}
	public static String desEncodeAsBase64(String data, String key) {
		return base64Encode(des(utf8Encode(data), utf8Encode(key), Cipher.ENCRYPT_MODE));
	}
	public static byte[] desDecode(byte[] data, byte[] key) {
		return des(data, key, Cipher.DECRYPT_MODE);
	}
	public static byte[] desDecodeForHex(String dataHex, byte[] key) {
		return des(hexDecode(dataHex), key, Cipher.DECRYPT_MODE);
	}
	public static String desDecodeForHexAsString(String dataHex, String key) {
		return utf8Decode(des(hexDecode(dataHex), utf8Encode(key), Cipher.DECRYPT_MODE));
	}
	public static String desDecodeForBase64AsString(String dataBase64, String key) {
		return utf8Decode(des(Base64.decodeBase64(utf8Encode(dataBase64)), utf8Encode(key), Cipher.DECRYPT_MODE));
	}
	private static byte[] des(byte[] data, byte[] key, int opMode) {
		try {
			DESKeySpec desKey = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES", provider);
			SecretKey secureKey = keyFactory.generateSecret(desKey); 
			
			Cipher cipher = Cipher.getInstance("DES", provider);
//			SecureRandom secureRandom = new SecureRandom();
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");  // provider
			cipher.init(opMode, secureKey, secureRandom);
			
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static byte[] aesEncode(byte[] data, byte[] key) {
		return aes(data, key, 128, Cipher.ENCRYPT_MODE);
	}
	public static String aesEncodeAsHex(byte[] data, byte[] key) {
		return hexEncode(aes(data, key, 128, Cipher.ENCRYPT_MODE));
	}
	public static String aesEncodeAsHex(String data, String key) {
		if (data == null) {return null;}
		else if (data.length() == 0) { return "";}
		return hexEncode(aes(utf8Encode(data), utf8Encode(key), 128, Cipher.ENCRYPT_MODE));
	}
	public static String aesEncodeAsBase64(String data, String key) {
		return base64Encode(aes(utf8Encode(data), utf8Encode(key), 128, Cipher.ENCRYPT_MODE));
	}
	public static byte[] aesDecode(byte[] data, byte[] key) {
		return aes(data, key, 128, Cipher.DECRYPT_MODE);
	}
	public static byte[] aesDecodeForHex(String dataHex, byte[] key) {
		return aes(hexDecode(dataHex), key, 128, Cipher.DECRYPT_MODE);
	}
	public static String aesDecodeForHexAsString(String dataHex, String key) {
		if (dataHex == null) {return null;}
		else if (dataHex.length() == 0) { return "";}
		return utf8Decode(aes(hexDecode(dataHex), utf8Encode(key), 128, Cipher.DECRYPT_MODE));
	}
	public static String aesDecodeForBase64AsString(String dataBase64, String key) {
		if (dataBase64 == null) {return null;}
		else if (dataBase64.length() == 0) { return "";}
		return utf8Decode(aes(Base64.decodeBase64(utf8Encode(dataBase64)), utf8Encode(key), 128, Cipher.DECRYPT_MODE));
	}
	private static byte[] aes(byte[] data, byte[] key, int keyLen, int opMode) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES", provider);
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG"); // provider
			secureRandom.setSeed(key);
			kgen.init(keyLen, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
			
			 /* mode:	ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128<br/> 
			 * padding: Nopadding/PKCS5Padding/ISO10126Padding
			 */
			Cipher cipher = Cipher.getInstance("AES", provider); // ECB/PKCS5Padding
			cipher.init(opMode, keySpec);
			
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
//		// we're using Bouncy Castle
//	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
//
//	    // create our key specification
//	    val secretKeySpec = new SecretKeySpec(hexStringToByteArray(hexEncodedKey), "AES")
//	     
//	    // create an AES engine in CTR mode (no padding)
//	    val aes = Cipher.getInstance("AES/CTR/NoPadding", BouncyCastleProvider.PROVIDER_NAME)
//	     
//	    // initialize the AES engine in encrypt mode with the key and IV
//	    aes.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(hexStringToByteArray(hexEncodedIv)))
//	     
//	    // encrypt the message and return the encrypted byte array
//	    aes.doFinal(hexStringToByteArray(hexEncodedMessage))
	}
	
	public static long ipToLong(String ipStr){
		if (ipStr.equals("0:0:0:0:0:0:0:1")) {
			return 0l;
		}
		
		String[] ipArr = ipStr.split("\\.");
		long[] ip = new long[4];
		ip[0]= Long.parseLong(ipArr[0]);
		ip[1]= Long.parseLong(ipArr[1]);
		ip[2]= Long.parseLong(ipArr[2]);
		ip[3]= Long.parseLong(ipArr[3]);
		
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3]; 
	}
	
	public static String ipToString(long ipLong){
		StringBuffer ip = new StringBuffer();
		ip.append(String.valueOf(ipLong >>> 24));
		ip.append(".");
		ip.append(String.valueOf((ipLong & 0x00FFFFFF) >>> 16));
		ip.append(".");
		ip.append(String.valueOf((ipLong & 0x0000FFFF) >>> 8));
		ip.append(".");
		ip.append(String.valueOf(ipLong & 0x000000FF));
		
		return ip.toString();
	}
	
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
	
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		
		return tmp.toString();
	}
	
	public static String md5Encode(String data, String encoding) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(data.getBytes(encoding));
			return hexEncode(md.digest());
		} catch (Exception e) {
			throw new IllegalArgumentException("Encoding failed", e);
		}
	}
	
	private static final char[] HEX = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	public static String hexEncode(byte[] bytes) {
		final int nBytes = bytes.length;
		char[] result = new char[2*nBytes];

		int j = 0;
		for (int i=0; i < nBytes; i++) {
			// Char for top 4 bits
			result[j++] = HEX[(0xF0 & bytes[i]) >>> 4 ];
			// Bottom 4
			result[j++] = HEX[(0x0F & bytes[i])];
		}

		return new String(result);
	}
	public static byte[] hexDecode(CharSequence s) {
		int nChars = s.length();

		if (nChars % 2 != 0) {
			throw new IllegalArgumentException("Hex-encoded string must have an even number of characters");
		}

		byte[] result = new byte[nChars / 2];

		for (int i = 0; i < nChars; i += 2) {
			int msb = Character.digit(s.charAt(i), 16);
			int lsb = Character.digit(s.charAt(i+1), 16);

			if (msb < 0 || lsb < 0) {
				throw new IllegalArgumentException("Non-hex character in input: " + s);
			}
			result[i / 2] = (byte) ((msb << 4) | lsb);
		}
		return result;
	}
	
	private static final Charset CHARSET = Charset.forName("UTF-8");
	public static byte[] utf8Encode(CharSequence string) {
		try {
			ByteBuffer bytes = CHARSET.newEncoder().encode(CharBuffer.wrap(string));
			byte[] bytesCopy = new byte[bytes.limit()];
			System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());

			return bytesCopy;
		} catch (CharacterCodingException e) {
			throw new IllegalArgumentException("Encoding failed", e);
		}
	}
	public static String utf8Decode(byte[] bytes) {
		try {
			return CHARSET.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
		} catch (CharacterCodingException e) {
			throw new IllegalArgumentException("Decoding failed", e);
		}
	}
	
	public static byte[] gbk2utf8(String chenese) {
		char c[] = chenese.toCharArray();
		byte[] fullByte = new byte[3 * c.length];
		for (int i = 0; i < c.length; i++) {
			int m = (int) c[i];
			String word = Integer.toBinaryString(m);

			StringBuffer sb = new StringBuffer();
			int len = 16 - word.length();
			for (int j = 0; j < len; j++) {
				sb.append("0");
			}
			sb.append(word);
			sb.insert(0, "1110");
			sb.insert(8, "10");
			sb.insert(16, "10");

			byte[] bf = new byte[3];
			bf[0] = Integer.valueOf(sb.substring(0, 8), 2).byteValue();
			fullByte[i * 3] = bf[0];
			bf[1] = Integer.valueOf(sb.substring(8, 16), 2).byteValue();
			fullByte[i * 3 + 1] = bf[1];
			bf[2] = Integer.valueOf(sb.substring(16), 2).byteValue();
			fullByte[i * 3 + 2] = bf[2];

		}
		return fullByte;
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
//		String s  = md5("chiknin@gmail.com");
//		System.out.println(s);
		
//		System.out.println(base64Decode("MjU1MDE2fGh0dHA6Ly94LmNhaWRvci5jb206ODA4MS9hbmFseXRpY3Mvc3RhdGljL2MuaHRtbHx8fHx8fHx8fDB8MTM2OTAzODUwNnwxMTUuMzIuMi4yNDR8MTAwfGNwYXwxN3w0Mjd8MTM4fDgyMHwyNjg"));
//		System.out.println(base64Decode("MTAxLjcxLjI0Ni4yMjZ8NDI2fDExNnwyNjd8ODE4fDE2fGh0dHA6Ly94LmNhaWRvci5jb206ODA4MS9hbmFseXRpY3Mvc3RhdGljL2MuaHRtbHww"));
//		System.out.println(base64Decode(HtmlUtils.htmlEscape("MTE1LjMyLjIuMjQ0fDQyN3wxMzh8MjY4fDgyMHwxN3xodHRwOi8veC5jYWlkb3IuY29tOjgwODEvYW5hbHl0aWNzL3N0YXRpYy9jLmh0bWx8MA")));
//		System.out.println(base64Decode("NDQ0ODcxfGh0dHA6Ly9hLmNvbTo4MDgxL2FuYWx5dGljcy9zdGF0aWMvYy5odG1sfHx8fHx8fHx8MHwxMzY5MDM5NTg5fDExNS4zMi4yLjI0NHwxMDB8Y3BhfDE3fDQyN3wxMzh8ODIwfDI2OA=="));
		
//		GET /iclk/?s=NDQ0ODcxfGh0dHA6Ly9hLmNvbTo4MDgxL2FuYWx5dGljcy9zdGF0aWMvYy5odG1sfHx8fHx8fHx8MHwxMzY5MDM5NTg5fDExNS4zMi4yLjI0NHwxMDB8Y3BhfDE3fDQyN3wxMzh8ODIwfDI2OA==;fa2eef7e0eddb0e8228724b6ae531cfb;http%3A%2F%2Fx.caidor.com%3A8081%2Fanalytics%2Fstatic%2Fa.html;ck;1;63;19;7;31;7;207&a=11.7.700;1366x768;http%3A//a.com%3A8081/analytics/static/c.html; HTTP/1.1

		
//		System.out.println(md5("123456"));
//		System.out.println(md5("101002101000103"));
//		System.out.println(md5("001101000000201"));
//		System.out.println(md5("203101000000201"));
//		System.out.println(md5("123", true));
//		
//		System.out.println(sha1("chiknin@gmail.com", false));
		
//		System.out.println(base64Encode("chiknin@gmail.com"));
//		System.out.println(base64Encode("chiknin中国sdf="));
//		System.out.println(base64Decode("Uvm486GbE2WM3Q1GzzQhrMi2fTQBrMyKD5QPrbSdw0ItyTjcsYJ44eSojmSPsUiqr7PkrRycrNT6rQSRrfYrsaSG7re4wz"));
		
		
//		System.out.println(ipToLong("192.16j35521L));
		
//		 KeyGenerator kg = KeyGenerator.getInstance("DES");  
//			//初始化此密钥生成器，使其具有确定的密钥大小  
//			kg.init(56);  
//			//生成一个密钥  
//			SecretKey  secretKey = kg.generateKey();  
//			System.out.println(new String(secretKey.getEncoded()));
		
//		String text = "123456";
//		String key = "1234567890123456";
		//61267f9c61f0bf6abee22f4ec14dd70f
//		String s5 = aesEncodeAsHex(text, key);
//		System.out.println(s5);
//		String s6 = aesDecodeForHexAsString(s5, key);
//		System.out.println(s6);
//		
//		String s1 = hexEncode(desEncode(text.getBytes(), key.getBytes()));
//		s1 = desEncodeAsHex(text.getBytes(), key.getBytes());
//		s1 = desEncodeAsHex(text, key);
//		System.out.println(s1);
////		
//		String s2 = new String(desDecode(hexDecode(s1), key.getBytes()));
//		s2 = new String(desDecodeForHex(s1, key.getBytes()));
//		s2 = desDecodeForHexAsString(s1, key);
//		System.out.println(s2);
		
//		System.out.println(base64Encode(hmacSHA256(text.getBytes(), key.getBytes())));
//		System.out.println(base64Encode(hmacSHA512(text.getBytes(), key.getBytes())));
		
//		System.out.println(desDecodeForHexAsString("ce4463497a6f12c9ef915b1a748b8a93440daff0e6aedd0551248607922e42b82416944480b28b31e47df5e98aee665d93c3e426187829d0e605cf0d3e5c5b843cccc0519a719fe7c894db751615a552b6992df3c6660fc63d5ebfb1d8d9efc60a7c73ab4a81c760", "98765432"));
		
//		String data = "测试数据";
//		MessageDigest messageDigest = getMessageDigest("MD5");
//		byte[] digest = messageDigest.digest(Utf8.encode(data));
//		String result = new String(Hex.encode(digest));
//		
//		System.out.println(result);
		
//		System.out.println(md5("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><request><header><transCode>001000010101</transCode><channelId>204</channelId><transSn></transSn><transTime>2013-07-0317:15:53</transTime></header><body><transLineNum>urUjVzA6lkE9tRsAZuTEdNm8kFrP9LBV</transLineNum><adSource>059</adSource><carAreaCode>1101</carAreaCode><newCar>2</newCar><licenseNo>京A12345</licenseNo><enrollDate>2008-08-03</enrollDate><purchasePrice>129799</purchasePrice><contactName>张飞</contactName><contactMobile>13333333333</contactMobile><contactSex>1</contactSex><insurerNum>103</insurerNum></body></request>^dc3PeeXcdgoccmFR$NdTnX(WBrkon%@"));
//		System.out.println(md5("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><request><header><transCode>001000010101</transCode><channelId>001</channelId><transSn></transSn><transTime>2012-11-01 16:00:00</transTime><version></version></header><body><transLineNum>urUjVzA6lkE9tRsAZuTEdNm8kFrP9LBV</transLineNum><adSource></adSource><carAreaCode>1101</carAreaCode><newCar>1</newCar><licenseNo></licenseNo><enrollDate>2012-10-31</enrollDate><purchasePrice>10000</purchasePrice><contactName>姓名</contactName><contactMobile>13800138000</contactMobile><contactSex>1</contactSex><insurerNum>101</insurerNum></body></request>KYxwLy9fRz1Mgk3M2yDl6ZqjyY#PN*UK"));
		
//		System.out.println(md5Encode("3c006c2c58c771ab065a2666e442530d,cooperateId=cp001,utmsn=12345,type=underwrite", "GBK"));
		
//		ExecutorService pool = Executors.newFixedThreadPool(5); 
//		for (int i = 0; i < 8; i++) { 
//				pool.execute(new Runnable() {
//					@Override
//					public void run() {
//						System.out.println("123");
//					}
//				}); 
//		} 
//		pool.shutdown(); 

		
		
//		DruidDataSource ds = new DruidDataSource();
//		ds.setUrl("jdbc:mysql://127.0.0.1:3306/iplat?useUnicode=true&characterEncoding=utf8&autoReconnect=true");
//		ds.setUsername("dev");
//		ds.setPassword("dev123");
//		ds.init();
//		
//		final JdbcTemplate jt = new JdbcTemplate(ds);
//		
//		final Set<String> uuids = new ConcurrentHashSet<String>();
//		
//		for (int i = 0; i < 100; i++) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					for (int j = 0; j < 10; j++) {
//						String uuid = uuid();
//						
//						try {
//						jt.execute("insert into uuid(id) values('" + uuid +"')");
//						} catch (Exception e) {
//							System.out.println(uuid);
//						}
//						
////						if (!uuids.contains(uuid)) {
////							uuids.add(uuid);
////						} else {
////							System.out.println(uuid);
////						}
//					}
//				}
//			}).start();
//		}
//		
//		
////		Thread.currentThread().join();
//		Thread.sleep(5000);
//		
//		System.out.println(uuids.size());
		
		String s = "20000002A24011410000121407901826523chiknin@gmail.com";
		System.out.println(md5Encode(s, "UTF-8"));
//		
//		
//		System.out.println(JavaScriptUtils.javaScriptEscape(s));
		
		String r1 = "XWhIsop+x+znlkDOq/GGTg=="; // aesEncodeAsBase64(s, "123456789");
		System.out.println(r1);
		System.out.println(aesDecodeForBase64AsString(r1, "0123456789ABCDEF"));
		
//		String r2 = desEncodeAsBase64(s, "12345678");
//		System.out.println(r2);
//		System.out.println(desDecodeForBase64AsString(r2, "1234567"));
		
	}
}