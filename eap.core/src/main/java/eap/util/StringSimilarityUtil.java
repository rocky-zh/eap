package eap.util;

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
public class StringSimilarityUtil {
	
	public static void main(String[] args) {
//		matchFirst("aaaa", new Unit("AAAA", 1, 2, 2));  							// {[(a)(a)]}{[(a)(a)]}
//		matchFirst("aabb", new Unit("AABB", 1, 2, 2));  							// {[(a)][(a)]}{[(b)][(b)]}
//		matchFirst("aaaa", new Unit("AAAA", 4, 1, 1));  							// {[(aaaa)]}
//		matchFirst("aaaa", new Unit("AAAA", 2, 2, 1));  							// {[(aa)(aa)]}
//		matchFirst("aaaa", new Unit("AAAA", 2, 1, 2));  							// {[(aa)]}{[(aa)]}
//		matchFirst("aabb", new Unit("AABB", 2, 1, 2));  							// {[(aa)]}{[(bb)]}
		
//		matchFirst("123", new Unit("ABC", 1, 3, 1, 1)); 							// {[(1)(2)(3)]}
//		matchFirst("123123", new Unit("ABC", 1, 3, 2, 1)); 						// {[(1)(2)(3)]}{[(1)(2)(3)]}
//		matchFirst("112233", new Unit("AABBCC", 2, 3, 1, 1)); 				// {[(11)(22)(33)]}
//		matchFirst("aabbaabb", new Unit("AABBAABB", 2, 2, 2, 1));	// {[(aa)(bb)]}{[(aa)(bb)]}
//		matchFirst("aabbaabb1122", new Unit("AABBAABB", 2, 2, 3, 1)); // {[(aa)(bb)]}{[(aa)(bb)]}{[(11)(22)]}
//		matchFirst("aaBBAAbb1122", true, new Unit("AABBAABB", 2, 2, 3, 1)); // {[(aa)(BB)]}{[(AA)(bb)]}{[(11)(22)]}
//		matchFirst("aabb", new Unit("AABB", 2, 1, 1, 1));						// {[(aa)]}
//		matchFirst("aabb", new Unit("AABB", 2, 2, 1, 1));						// {[(aa)(bb)]}
		
//		matchFirst("321", new Unit("CBA", 1, 3, 1, -1)); 								// {[(3)(2)(1)]}
//		matchFirst("321321", new Unit("CBA", 1, 3, 2, -1)); 						// {[(3)(2)(1)]}{[(3)(2)(1)]}
//		matchFirst("332211", new Unit("CCBBAA", 2, 3, 1, -1)); 				// {[(33)(22)(11)]}
//		matchFirst("bbaabbaa", new Unit("BBAABBAA", 2, 2, 2, -1));		// {[(bb)(aa)]}{[(bb)(aa)]}
//		matchFirst("2211bbaabbaa", new Unit("BBAABBAABBAA", 2, 2, 3, -1)); // {[(22)(11)]}{[(bb)(aa)]}{[(bb)(aa)]}
//		matchFirst("BBaabbAA", true, new Unit("BBAABBAA", 2, 2, 2, -1)); // {[(BB)(aa)]}{[(bb)(AA)]}
//		matchFirst("bbaa", new Unit("BBAA", 2, 1, 1, -1));						// {[(bb)]}
//		matchFirst("bbaa", new Unit("BBAA", 2, 2, 1, -1));						// {[(bb)(aa)]}
		
//		matchFirst("13579", new Unit("ABC", 1, 3, 1, 2)); 							// {[(135)]}
//		matchFirst("acegik", new Unit("ABC", 1, 3, 2, 2)); 							// {[(ace)]}{[(gik)]}
		
//		matchFirst("97531", new Unit("CBA", 1, 3, 1, -2)); 							// {[(975)]}
//		matchFirst("kigeca", new Unit("CBA", 1, 3, 2, -2)); 							// {[(gik)]}{[(eca)]}
		
		
		matchFirst("11111", new Unit("AAAAA", 1, 5, 1));
		matchFirst("12345", new Unit("ABCDE", 1, 5, 1, 1));
		matchFirst("98765", new Unit("ABCDE", 1, 5, 1, -1));
	}
	
	public static Unit matchFirstForMobileNumber(String mobileNumber) {
		if (StringUtils.isBlank(mobileNumber)) {
			return null;
		}
		
		return matchFirst(mobileNumber, 
			new Unit("AAAAA", 1, 5, 1),
			new Unit("ABCDE", 1, 5, 1, 1),
			new Unit("EDCBA", 1, 5, 1, -1)
		);
	}
	
	public static Unit matchFirst(String str, Unit... units) {
		return matchFirst(str, false, units);
	}
	public static Unit matchFirst(String str, boolean IgnoreCase, Unit... units) { // TODO match -> return: all match
		if (StringUtils.isBlank(str) || units == null || units.length == 0) {
			return null;
		}
		
		int[][] unitsRt = new int[units.length][6];
		Unit u = null;
		for (int i = 0; i < units.length; i++) {
			u = units[i];
			unitsRt[i] = new int[]{
				1, 
				u.count == 1 ? 1 : u.width, 
//				u.count == 1 ? u.width -1 : ((u.count - 1) * u.width), 0, 
				u.count == 1 ? (u.width == 1 ? 1 : u.width -1) : ((u.count - 1) * u.width), 0, 
				u.repeat, 0
			};
		}
		
		char[] chars = IgnoreCase ? str.toLowerCase().toCharArray() : str.toCharArray();
		int len = chars.length;
		for (int i = 1; i < len; i++) {
			for (int j = 0; j < unitsRt.length; j++) {
				int _ui = unitsRt[j][0], _uw = unitsRt[j][1], _uc = unitsRt[j][2], _uci = unitsRt[j][3], _ur = unitsRt[j][4], _uri = unitsRt[j][5];
				
				if (i == _ui) {
					if ((i+_uw-1) >= len) {
//						System.out.println("-----------------------------1> " + i);
					} else if ((int)chars[i+_uw-1] - (int)chars[i-1] == units[j].interval) { // ==1   ==0   ==-1
//						System.out.println(chars[i-1] + " " + chars[i]);
						if (++unitsRt[j][3] == _uc) {
							if (_ur > 1) {
								if (++unitsRt[j][5] == _ur) {
//									System.out.println(" _uri " + _uri);
									return units[j];
//									break;
								}
								unitsRt[j][0] += _uw;
								unitsRt[j][3] = 0;
							} else {
//								System.out.println("_uci " + _uci);
								return units[j];
//								break;
							}
						}
					} else {
						unitsRt[j][3] = 0;
						unitsRt[j][5] = 0;
					}
					unitsRt[j][0]++;
				}
			}
		}
		
		return null;
	}
	
	public static enum Trend {
		
	}
	public static class Unit {
		
		private String name;
		
		private int width = 1;
		private int count = 1;
		private int repeat = 1;
		
		private int interval = 0;
		
		public Unit() {
		}
		public Unit(String name, int width, int count, int repeat) {
			this.name = name;
			this.width = width;
			this.count = count;
			this.repeat = repeat;
		}
		public Unit(String name, int width, int count, int repeat, int interval) {
			this.name = name;
			this.width = width;
			this.count = count;
			this.repeat = repeat;
			this.interval = interval;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public int getRepeat() {
			return repeat;
		}
		public void setRepeat(int repeat) {
			this.repeat = repeat;
		}
		public int getInterval() {
			return interval;
		}
		public void setInterval(int interval) {
			this.interval = interval;
		}
	}
}