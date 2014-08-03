package eap.comps.useragent;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class UserAgent {
	
	private static final Map<String, Pattern> patternMap = new ConcurrentHashMap<String, Pattern>();
	
	private static final Object[][] osRules = {
		{"Windows", new String[] {"static", "8.1"}, "Computer", new String[] {"windows nt 6.3"}, null},
		{"Windows", new String[] {"static", "8"}, "Computer", new String[] {"windows nt 6.2"}, null},
		{"Windows", new String[] {"static", "7"}, "Computer", new String[] {"windows nt 6.1"}, null},
		{"Windows", new String[] {"static", "Vista"}, "Computer", new String[] {"windows nt 6.0"}, null},
		{"Windows", new String[] {"static", "Server 2003"}, "Computer", new String[] {"windows nt 5.2"}, null},
		{"Windows", new String[] {"static", "XP"}, "Computer", new String[] {"windows nt 5.1"}, null},
		{"Windows", new String[] {"static", "2000(SP1)"}, "Computer", new String[] {"windows nt 5.01"}, null},
		{"Windows", new String[] {"static", "2000"}, "Computer", new String[] {"windows nt 5.0"}, null},
		{"Windows", new String[] {"static", "NT 4.0"}, "Computer", new String[] {"windows nt 4.0"}, null},
		{"Windows", new String[] {"static", "Me"}, "Computer", new String[] {"win 9x 4.90"}, null},
		{"Windows", new String[] {"static", "98"}, "Computer", new String[] {"windows 98"}, null},
		{"Windows", new String[] {"static", "95"}, "Computer", new String[] {"windows 95"}, null},
		{"Windows", new String[] {"static", "CE"}, "Mobile", new String[] {"windows ce"}, null},
		{"Windows Phone", new String[] {"static", "8"}, "Mobile", new String[] {"windows phone os 8.0"}, null},
		{"Windows Phone", new String[] {"static", "7.5"}, "Mobile", new String[] {"windows phone os 7.1"}, null},
		{"Windows Phone", new String[] {"static", "7"}, "Mobile", new String[] {"windows phone os 7.0"}, null},
		{"Android Mobile", new String[] {"regex", "android[ ]?(([\\w]+)\\.([\\w]+)(\\.([\\w]+))?)"}, "Mobile", null, new String[] {"android", "mobile"}},
		{"Android Mobile", new String[] {"regex", "adr (([\\w]+)\\.([\\w]+)(\\.([\\w]+))?)"}, "Mobile", null, new String[] {" adr ", "mobile"}},
		{"Android Tablet", new String[] {"regex", "android (([\\w]+)\\.([\\w]+)(\\.([\\w]+))?)"}, "Tablet", new String[] {"android"}, null},
		{"WebOS", new String[] {"regex", "webos[ /](([\\w]+)\\.([\\w]+)\\.([\\w]+))"}, "Mobile", new String[] {"webos"}, null},
		{"PalmOS", new String[] {"regex", "palm (([\\w]+)\\/([\\w]+))"}, "Mobile", new String[] {"palm"}, null},
		{"iPad", new String[] {"regex", "ipad os (([\\w]+)_([\\w]+)(_([\\w]+))?)", "os (([\\w]+)_([\\w]+)(_([\\w]+))?) like mac os x", "os (([\\w]+)_([\\w]+)(_([\\w]+))?)"}, "Tablet", new String[] {"ipad"}, null},
		{"iPod", new String[] {"regex", "os (([\\w]+)_([\\w]+)(_([\\w]+))?) like mac os x", "os (([\\w]+)_([\\w]+)(_([\\w]+))?)"}, "Tablet", new String[] {"ipod"}, null},
		{"iPhone", new String[] {"regex", "iphone os (([\\w]+)_([\\w]+)(_([\\w]+))?) like mac os x", "iphone[ ]?(\\d\\w*)?"}, "Mobile", new String[] {"iphone"}, null},
		{"Mac OS X", new String[] {"regex", "mac os x (([\\w]+)[\\._]([\\w]+)([\\._]([\\w]+))?)"}, "Computer", new String[] {"mac os x"}, null},
		{"Mac OS X", null, "Computer", new String[] {"cfnetwork"}, null},
		{"Mac OS", null, "Computer", new String[] {"mac"}, null},
		{"Maemo", null, "Mobile", new String[] {"maemo"}, null},
		{"Bada", new String[] {"regex", "bada/(([\\w]+)\\.([\\w]+))"}, "Mobile", new String[] {"bada"}, null},
		{"Symbian", new String[] {"regex", "symbianos\\/(([\\w]+)\\.([\\w]+))"}, "Mobile", new String[] {"symbian", "series60"}, null},
		{"SonyEricsson", null, "Mobile", new String[] {"sonyericsson"}, null}, // new String[] {"regex", "sonyericsson([\\w]+)"}
		{"sunos", new String[] {"regex", "sunos (([\\w]+)\\.([\\w]+))"}, "Mobile", new String[] {"sunos"}, null},
		{"PlayStation", new String[] {"regex", "playstation ([\\w]+)"}, "Game Console", new String[] {"playstation"}, null},
		{"Wii", null, "Game Console", new String[] {"wii"}, null},
		{"BlackBerry", new String[] {"regex", "blackberry\\d*\\/(([\\w]+)\\.([\\w]+)\\.([\\w]+))"}, "Mobile", new String[] {"blackberry"}, null},
		{"BlackBerry Tablet", new String[] {"regex", "rim tablet os (([\\w]+)\\.([\\w]+)\\.([\\w]+))"}, "Tablet", new String[] {"rim tablet os"}, null},
		{"Java MIDP", new String[] {"regex", "midp-(([\\w]+)\\.([\\w]+))"}, "Mobile", new String[] {"midp-"}, null}
	};
	
	private static final String[] engineRules = {
		"trident", "microsoft office word", "webkit", "gecko", "presto", "mozilla", "blink", "khtml", 
	};
	
	private static final Object[][] bowserRules_1 = {
		{"Internet Explorer", new String[] {"msie (([\\d]+))"}, null, new String[] {"msie"}, null},
		{"Firefox", new String[] {"firefox\\/(([\\d]+))"}, null, new String[] {"firefox"}, null},
		{"Opera Mini", new String[] {"opera mini\\/(([\\d]+)\\.([\\w]+))"}, "Mobile", new String[] {"msie"}, null},
		{"Opera", new String[] {"opera\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"opera mini"}, null},
		{"Konqueror", new String[] {"Konqueror\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"konqueror"}, null},
		{"Chrome移动版", new String[] {"crmo\\/(([\\d]+))"}, "Mobile", new String[] {"crmo"}, null},
		{"Chrome", new String[] {"chrome\\/(([\\d]+))"}, null, new String[] {"chrome"}, null},
		{"Safari移动版", new String[] {"mobile safari[\\ \\/](([\\d]+))"}, null, new String[] {"mobile safari"}, null},
		{"Safari", new String[] {"version\\/(([\\d]+))"}, null, new String[] {"safari"}, null},
		{"Dolfin", new String[] {"dolfin[ \\/]?(([\\d]+)\\.([\\w]+))"}, null, new String[] {"dolfin"}, null}
	};
	
	private static final Object[][] bowserRules_2 = {
		{"Outlook-Express", new String[] {"outlook-express\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"outlook-express"}, null},
		{"遨游浏览器", new String[] {"maxthon[ \\/](([\\d]+))"}, null, new String[] {"maxthon"}, null},
		{"QQ旋风", new String[] {"qqdownload (([\\d]+))"}, null, new String[] {"qqdownload"}, null},
		{"腾讯TT", new String[] {"tencenttraveler (([\\d]+)\\.([\\w]+))"}, null, new String[] {"tencenttraveler"}, null},
		{"QQ浏览器移动版", new String[] {"mqqbrowser\\/((mini)?([\\d]+)\\.([\\w]+))"}, "Mobile", new String[] {"mqqbrowser"}, null},
		{"QQ浏览器", new String[] {"qqbrowser\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"qqbrowser"}, null},
		{"世界之窗", null, null, new String[] {"theworld", "the world"}, null},
		{"猎豹浏览器", null, null, new String[] {"lbbrowser"}, null},
		{"360浏览器", null, null, new String[] {"360browser", "360SE", "360EE"}, null},
		{"爱帆浏览器", null, null, new String[] {"avant browser"}, null},
		{"百度浏览器", new String[] {"bidubrowser (([\\d]+)\\.([\\w]+))", "baidubrowser\\/([\\d]+)", "biduplayerbrowser\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"tencenttraveler"}, null},
		{"淘宝浏览器", new String[] {"taobrowser\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"taobrowser"}, null},
		{"小米浏览器", new String[] {"xiaomi\\/miuibrowser\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"xiaomi/miuibrowser"}, null},
		{"枫树浏览器", new String[] {"coolnovo\\/([\\d]+)"}, null, new String[] {"coolnovo"}, null},
		{"瑞影浏览器", null, null, new String[] {"ruibin"}, null},
		{"绿色浏览器", null, null, new String[] {"greenbrowser"}, null},
		{"淘米浏览器", new String[] {"taomeebrowser\\/([\\d]+)"}, null, new String[] {"taomeebrowser"}, null},
		{"闪游浏览器", null, null, new String[] {"saayaa"}, null},
		{"科摩多龙浏览器", new String[] {"comodo_dragon\\/([\\d]+)"}, null, new String[] {"comodo_dragon"}, null},
		{"彗星浏览器", new String[] {"cometbrowser\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"cometbrowser"}, null},
		{"塞班浏览器", new String[] {"s40ovibrowser\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"s40ovibrowser"}, null},
		{"UC浏览器", new String[] {"ucbrowser\\/(([0-9]+)\\.([\\w]+))", "ucweb(([0-9]+)\\.([\\w]+))"}, "Mobile", new String[] {"ucbrowser","ucweb"," uc "}, null},
		{"搜狗浏览器", new String[] {" se (([\\d]+)\\.([\\w]+))"}, null, new String[] {" se "}, null},
		{"微信", new String[] {"micromessenger\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"micromessenger"}, null},
		{"Camino", new String[] {"camino\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"camino"}, null},
		{"SeaMonkey", new String[] {"seamonkey\\/(([\\d]+)\\.([\\w]+))"}, null, new String[] {"seamonkey"}, null}
	};
	
	public static String[] parse(String ua) {
		if (ua == null || ua.length() == 0) {return null;};
		
		// 0,  1.      2,       , 3         , 4         , 5                , 6             , 7               , 8              , 9                , 10
		// ua, osName, osVersion, deviceType, deviceName, renderingEngine, bowserName(L2), bowserVersion(L2), bowserName(L1), bowserVersion(L1), ScreenWide*ScreenHeight
		String[] stats = new String[11]; 
		stats[0] = ua;

		for (Object[] rule : osRules) {
			int passFlag = 0;
			if (rule[3] != null) {
				passFlag = 1;
				for (String regex : (String[])rule[3]) {
					if (ua.indexOf(regex) != -1) {
						passFlag = 2;
						break;
					}
				}
			}
			if (passFlag != 1 && rule[4] != null) {
				passFlag = 1;
				boolean find = true;
				for (String regex : (String[])rule[4]) {
					if (ua.indexOf(regex) == -1) {
						find = false;
						break;
					}
				}
				if (find) {
					passFlag = 2;
				}
			}
			if (passFlag == 2) {
				stats[1] = (String) rule[0];
				if (rule[1] != null) {
					String[] osVerRules = (String[]) rule[1];
					if ("static".equalsIgnoreCase(osVerRules[0])) {
						stats[2] = osVerRules[1];
					} else if ("regex".equalsIgnoreCase(osVerRules[0])) {
						for (int i = 1; i < osVerRules.length; i++) {
							Pattern osVerRegex = patternMap.get((String) osVerRules[i]);
							if (osVerRegex == null) {
								osVerRegex = Pattern.compile((String) osVerRules[i]);
							}
							
//							Pattern osVerRegex = Pattern.compile((String) osVerRules[i]);
							Matcher osVerMatcher = osVerRegex.matcher(ua);
							if (osVerMatcher.find() && osVerMatcher.groupCount() >= 1) {
								stats[2] = osVerMatcher.group(1) != null ? osVerMatcher.group(1).replaceAll("_", "\\.") : null;
								break;
							}
						}
					}
				}
				stats[3] = (String) rule[2];
				break;
			}
		}
		
		int deviceNameEidx = ua.indexOf(" build/");
		if (deviceNameEidx == -1) {
			deviceNameEidx = ua.indexOf(" build ");
		}
		if (deviceNameEidx != -1) { // 
			Pattern buildDeviceRegex = patternMap.get("; ([\\ \\w_-]+) build[ \\/]\\w+(; (\\d+\\*\\d+))?\\)");
			if (buildDeviceRegex == null) {
				buildDeviceRegex = Pattern.compile("; ([\\ \\w_-]+) build[ \\/]\\w+(; (\\d+\\*\\d+))?\\)");
			}
			
//			Pattern buildDeviceRegex = Pattern.compile(" \\(([\\w_-]+)/");
			Matcher buildDeviceMatcher = buildDeviceRegex.matcher(ua);
			if (buildDeviceMatcher.find()) {
				if (buildDeviceMatcher.groupCount() > 0) {
					stats[4] = buildDeviceMatcher.group(0);
				} 
				if (buildDeviceMatcher.groupCount() > 2) {
					stats[10] = buildDeviceMatcher.group(3);
				}
			}
			
			int deviceNameSidx = ua.substring(0, deviceNameEidx).lastIndexOf(";");
			if (deviceNameSidx != -1) {
				stats[4] = ua.substring(deviceNameSidx + 1, deviceNameEidx).trim();
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // 
			deviceNameEidx = ua.indexOf(" ophone/");
			if (deviceNameEidx != -1) {
				deviceNameEidx = ua.indexOf("/");
				if (deviceNameEidx != -1) {
					stats[4] = ua.substring(0, deviceNameEidx).trim();
				}
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // MQQBrowser/Mini3.1 (SonyEricssonU10i/R7BA084)
			if (ua.indexOf("mqqbrowser/mini") != -1) {
				Pattern mqqbrowserMiniDeviceRegex = patternMap.get(" \\(([\\w_-]+)/");
				if (mqqbrowserMiniDeviceRegex == null) {
					mqqbrowserMiniDeviceRegex = Pattern.compile(" \\(([\\w_-]+)/");
				}
				
//				Pattern mqqbrowserMiniDeviceRegex = Pattern.compile(" \\(([\\w_-]+)/");
				Matcher mqqbrowserMiniDeviceMatcher = mqqbrowserMiniDeviceRegex.matcher(ua);
				if (mqqbrowserMiniDeviceMatcher.find() && mqqbrowserMiniDeviceMatcher.groupCount() > 0) {
					stats[4] = mqqbrowserMiniDeviceMatcher.group(1);
				}
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // Mozilla/5.0 (Series40; NokiaC2-06/06.98; Profile/MIDP-2.1 Configuration/CLDC-1.1) Gecko/20100401 S40OviBrowser/2.2.0.0.31
			if (ua.indexOf("s40ovibrowser/") != -1) {
				Pattern series40DeviceRegex = patternMap.get("; (nokia[\\w_-]+)\\/");
				if (series40DeviceRegex == null) {
					series40DeviceRegex = Pattern.compile("; (nokia[\\w_-]+)\\/");
				}
				
//				Pattern series40DeviceRegex = Pattern.compile(" \\(([\\w_-]+)/");
				Matcher series40DeviceMatcher = series40DeviceRegex.matcher(ua);
				if (series40DeviceMatcher.find() && series40DeviceMatcher.groupCount() > 0) {
					stats[1] = "Symbian";
					stats[3] = "Mobile";
					stats[4] = series40DeviceMatcher.group(1);
				}
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // JUC (Linux; U; 2.3.6; zh-cn; Lenovo_A366t; 320*480)
			Pattern jucDeviceRegex = patternMap.get("juc \\(linux; u; ([\\w\\.]+); [\\w-_]+; (.*); (\\d+\\*\\d+)\\)");
			if (jucDeviceRegex == null) {
				jucDeviceRegex = Pattern.compile("juc \\(linux; u; ([\\w\\.]+); [\\w-_]+; (.*); (\\d+\\*\\d+)\\)");
			}
			
//			Pattern jucDeviceRegex = Pattern.compile("JUC \\(Linux; U; ([\\w\\.]+); [\\w-_]+; (.*); (\\d+\\*\\d+)\\)");
			Matcher jucDeviceMatcher = jucDeviceRegex.matcher(ua);
			if (jucDeviceMatcher.find() && jucDeviceMatcher.groupCount() > 2) {
				stats[1] = "Android Mobile";
				stats[2] = jucDeviceMatcher.group(1);
				stats[3] = "Mobile";
				stats[4] = jucDeviceMatcher.group(2);
				stats[10] = jucDeviceMatcher.group(3);
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // NokiaN8-00
			Pattern nokiaDeviceRegex = patternMap.get("^(nokia[\\ \\w_-]+)");
			if (nokiaDeviceRegex == null) {
				nokiaDeviceRegex = Pattern.compile("^(nokia[\\ \\w_-]+)");
			}
			
//			Pattern nokiaDeviceRegex = Pattern.compile("^(nokia[\\w_-]*)$");
			Matcher nokiaDeviceMatcher = nokiaDeviceRegex.matcher(ua);
			if (nokiaDeviceMatcher.find() && nokiaDeviceMatcher.groupCount() > 0) {
				stats[1] = "Symbian";
				stats[3] = "Mobile";
				stats[4] = nokiaDeviceMatcher.group(1);
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // 
			Pattern sonyEricssonDeviceRegex = patternMap.get("^sonyericsson([\\ \\w_-]+)");
			if (sonyEricssonDeviceRegex == null) {
				sonyEricssonDeviceRegex = Pattern.compile("^sonyericsson([\\ \\w_-]+)");
			}
			
//			Pattern sonyEricssonDeviceRegex = Pattern.compile("^sonyericsson([\\w_-]+)");
			Matcher sonyEricssonDeviceMatcher = sonyEricssonDeviceRegex.matcher(ua);
			if (sonyEricssonDeviceMatcher.find() && sonyEricssonDeviceMatcher.groupCount() > 0) {
				stats[4] = sonyEricssonDeviceMatcher.group(0);
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // 
			Pattern samsungDeviceRegex = patternMap.get("^(samsung[ ]?[\\w_-]+)");
			if (samsungDeviceRegex == null) {
				samsungDeviceRegex = Pattern.compile("^(samsung[ ]?[\\w_-]+)");
			}
//			Pattern sonyEricssonDeviceRegex = Pattern.compile"^(samsung[\\w_-]*)$");
			Matcher samsungDeviceMatcher = samsungDeviceRegex.matcher(ua);
			if (samsungDeviceMatcher.find() && samsungDeviceMatcher.groupCount() > 0) {
				stats[3] = "Mobile";
				stats[4] = samsungDeviceMatcher.group(0);
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // SonyEricssonU5i/UCWEB8.8.0.212/50/800
			Pattern ucwebDeviceRegex = patternMap.get("(.*)/ucweb[\\d\\.]+/\\w+/\\w+$");
			if (ucwebDeviceRegex == null) {
				ucwebDeviceRegex = Pattern.compile("(.*)/ucweb[\\d\\.]+/\\w+/\\w+$");
			}
			
//			Pattern ucwebDeviceRegex = Pattern.compile("(.*)/ucweb[\\d\\.]+/\\w+/\\w+");
			Matcher ucwebDeviceMatcher = ucwebDeviceRegex.matcher(ua);
			if (ucwebDeviceMatcher.find() && ucwebDeviceMatcher.groupCount() > 0) {
				stats[4] = ucwebDeviceMatcher.group(1);
			}
		}
		if (stats[4] == null || stats[4].length() == 0) { // J2ME   // BlackBerry9900/5.1.0.546 Profile/MIDP-2.0 Configuration/CLDC-1.1
			deviceNameEidx = ua.indexOf("/midp-");
			if (deviceNameEidx != -1) {
				Pattern midpDeviceRegex = patternMap.get("ucweb/2\\.0[ ]?\\(java; u; midp\\-2\\.0; [\\w_-]+; ([\\ \\w_-]+)\\) u2");
				if (midpDeviceRegex == null) {
					midpDeviceRegex = Pattern.compile("ucweb/2\\.0[ ]?\\(java; u; midp\\-2\\.0; [\\w_-]+; ([\\ \\w_-]+)\\) u2");
				}
				Matcher midpDeviceMatcher = midpDeviceRegex.matcher(ua);
				if (midpDeviceMatcher.find()) {
					if (midpDeviceMatcher.groupCount() > 0) {
						stats[4] = midpDeviceMatcher.group(1);
					}
				}
				
				if (stats[4] == null || stats[4].length() == 0 && ua.indexOf("browserng/") != -1) {
					midpDeviceRegex = patternMap.get("(nokia[\\w-_]+)");
					if (midpDeviceRegex == null) {
						midpDeviceRegex = Pattern.compile("(nokia[\\w-_]+)");
					}
					
					midpDeviceMatcher = midpDeviceRegex.matcher(ua);
					if (midpDeviceMatcher.find()) {
						if (midpDeviceMatcher.groupCount() > 0) {
							stats[4] = midpDeviceMatcher.group(0);
						}
					}
				}
				
				if (stats[4] == null || stats[4].length() == 0) { // 
					midpDeviceRegex = patternMap.get("Mozilla\\/5\\.0[ ]?([\\w_-]+)");
					if (midpDeviceRegex == null) {
						midpDeviceRegex = Pattern.compile("Mozilla\\/5\\.0[ ]?([\\w_-]+)");
					}
					
					midpDeviceMatcher = midpDeviceRegex.matcher(ua);
					if (midpDeviceMatcher.find()) {
						if (midpDeviceMatcher.groupCount() > 0) {
							stats[4] = midpDeviceMatcher.group(1);
						}
					}
				}
				
				if (stats[4] == null || stats[4].length() == 0) { // [10]
					midpDeviceRegex = patternMap.get("^([\\ \\w-_]+)");
					if (midpDeviceRegex == null) {
						midpDeviceRegex = Pattern.compile("^([\\ \\w-_]+)");
					}
					
					midpDeviceMatcher = midpDeviceRegex.matcher(ua);
					if (midpDeviceMatcher.find()) {
						if (midpDeviceMatcher.groupCount() > 0) {
							stats[4] = midpDeviceMatcher.group(1);
						}
					}
				}
			}
		}
		
		for (String rule : engineRules) {
			if (ua.indexOf(rule) != -1) {
				stats[5] = rule;
				break;
			}
		}
		
		for (Object[] rule : bowserRules_2) {
			int passFlag = 0;
			if (rule[3] != null) {
				passFlag = 1;
				for (String regex : (String[])rule[3]) {
					if (ua.indexOf(regex) != -1) {
						passFlag = 2;
						break;
					}
				}
			}
			if (passFlag != 1 && rule[4] != null) {
				passFlag = 1;
				boolean find = true;
				for (String regex : (String[])rule[4]) {
					if (ua.indexOf(regex) == -1) {
						find = false;
						break;
					}
				}
				if (find) {
					passFlag = 2;
				}
			}
			if (passFlag == 2) {
				stats[6] = (String) rule[0];
				if (rule[1] != null) {
					String[] bVerRules = (String[]) rule[1];
					for (String bVerRule : bVerRules) {
						Pattern bVerRegex = patternMap.get(bVerRule);
						if (bVerRegex == null) {
							bVerRegex = Pattern.compile(bVerRule);
						}
						
//						Pattern bVerRegex = Pattern.compile(bVerRule);
						Matcher bVerMatcher = bVerRegex.matcher(ua);
						if (bVerMatcher.find() && bVerMatcher.groupCount() > 1) {
							stats[7] = bVerMatcher.group(1);
							break;
						}
					}
				}
				if (rule[2] != null) {
					stats[3] = (String) rule[2];
				}
				break;
			}
		}
		
		for (Object[] rule : bowserRules_1) {
			int passFlag = 0;
			if (rule[3] != null) {
				passFlag = 1;
				for (String regex : (String[])rule[3]) {
					if (ua.indexOf(regex) != -1) {
						passFlag = 2;
						break;
					}
				}
			}
			if (passFlag != 1 && rule[4] != null) {
				passFlag = 1;
				boolean find = true;
				for (String regex : (String[])rule[4]) {
					if (ua.indexOf(regex) == -1) {
						find = false;
						break;
					}
				}
				if (find) {
					passFlag = 2;
				}
			}
			if (passFlag == 2) {
				stats[8] = (String) rule[0];
				if (rule[1] != null) {
					String[] bVerRules = (String[]) rule[1];
					for (String bVerRule : bVerRules) {
						Pattern bVerRegex = patternMap.get(bVerRule);
						if (bVerRegex == null) {
							bVerRegex = Pattern.compile(bVerRule);
						}
						
//						Pattern bVerRegex = Pattern.compile(bVerRule);
						Matcher bVerMatcher = bVerRegex.matcher(ua);
						if (bVerMatcher.find() && bVerMatcher.groupCount() > 1) {
							stats[9] = bVerMatcher.group(1);
							break;
						}
					}
				}
				if (rule[2] != null) {
					stats[3] = (String) rule[2];
				}
				break;
			}
		}
		
		return stats;
	}
	
	
	public static void main(String[] args) throws SQLException {
//		DruidDataSource ds = new DruidDataSource();
//		ds.setUrl("jdbc:mysql://127.0.0.1:3306/analytics?useUnicode=true&characterEncoding=utf8&autoReconnect=true");;
//		ds.setUsername("root");
//		ds.setPassword("chiknin");
//		ds.init();
//		
//		JdbcTemplate jt = new JdbcTemplate(ds);
//		List<Map<String, Object>> uaList = jt.queryForList("select user_agent from t_access_log limit 900000, 100000");
//		String[] uas = new String[uaList.size()];
//		for (int i = 0; i < uaList.size(); i++) {
//			if (uaList.get(i).get("user_agent") != null) {
//				uas[i] = uaList.get(i).get("user_agent").toString().toLowerCase();
//			}
//		}
//		System.out.println(uas.length + "");
		
		String[] uas = {
			"Mozilla/5.0 (Linux; U; Android 4.0.4; zh-cn; T710P Build/IMM76D) UC AppleWebKit/530+ (KHTML, like Gecko) Mobile Safari/530".toLowerCase(),
			"Mozilla/5.0 (Linux; U; Android 4.0.3; zh-cn; Sony Tablet S Build/TISU0143) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30".toLowerCase(),
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)".toLowerCase(),
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Maxthon/4.0.0.2000 Chrome/22.0.1229.79 Safari/537.1".toLowerCase(),
			"Mozilla/5.0 (Linux; U; Android 2.2; zh-cn; ZTE-U_V880 Build/FRF91; 480*800) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1/UCWEB7.9.4.145/139/800".toLowerCase(),
			"MQQBrowser/2.6 Mozilla/5.0 (iPad; CPU OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Mobile/9B206 Safari/7534.48.3".toLowerCase(),
			"SonyEricssonA8i_TD/1.0 Ophone/2.0 (Linux; Android 2.1) Release/3.2.2010 Browser/WAP 2.0 AppleWebKit/530.17 Profile/MIDP-2.1 Configuration/CLDC-1.1".toLowerCase(),
			"SonyEricssonU5i/UCWEB8.8.0.212/50/800".toLowerCase(),
			"MQQBrowser/Mini3.1 (SonyEricssonU10i/R7BA084)".toLowerCase(),
			"MQQBrowser/Mini3.1 (NokiaX2-01/07.10)".toLowerCase(),
			"UCWEB/2.0 (Java; U; MIDP-2.0; zh-CN; gt-s5230c) U2/1.0.0 UCBrowser/9.0.0.261 U2/1.0.0 Mobile UNTRUSTED/1.0".toLowerCase(),
			"NOKIAN86 8MP/UCWEB8.7.0.187/28/800".toLowerCase(),
			"NokiaN8-00".toLowerCase(),
			"SonyEricssonU1".toLowerCase(),
			"SonyEricssonM1i/R1BA Browser/Mozilla/4.0 (compatible; MSIE 4.01; Windows CE; PPC)".toLowerCase(),
			"SAMSUNG-GT-S3778V/1.0 RTK-E/1.0 DF3G/1.0 Release/04.04.2011 Browser/NetFront4.1 Profile/MIDP-2.0 Configuration/CLDC-1.1".toLowerCase(),
			"SamsungI8910".toLowerCase(),
			"Mozilla/5.0 (Linux; U; Android 4.1.2; zh-cn; MI-ONE C1 Build/JZO54K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 XiaoMi/MiuiBrowser/1.0".toLowerCase(),
			"JUC (Linux; U; 4.0.3; zh-cn; HTC T328t; 480*800) UCWEB8.7.4.225/145/800".toLowerCase(),
			"Mozilla/5.0 (Series40; NokiaC2-06/06.98; Profile/MIDP-2.1 Configuration/CLDC-1.1) Gecko/20100401 S40OviBrowser/2.2.0.0.31".toLowerCase(),
			"Nokia3110/5.0 (05.92) Profile/MIDP-2.1 Configuration/CLDC-1.1 UCWEB/2.0 (Java; U; MIDP-2.0; zh-CN; nokia3110) U2/1.0.0 UCBrowser/9.0.0.261 U2/1.0.0 Mobile".toLowerCase()
		};
		
		long st = System.currentTimeMillis();
		for (int l = 0; l < 500; l++) {
		
		for (String ua : uas) {
			String[] stats = parse(ua);
			
			System.out.println(Arrays.asList(stats));
			
//			jt.update("insert into t_access_log_copy(user_agent,os_name,os_version,device_type,device_name,rendering_engine,bowser_name_l2,bowser_version_l2,bowser_name_l1,bowser_version_l1,screen) values(?,?,?,?,?,?,?,?,?,?,?)", stats);
			
		}
		
//		System.out.println(l);
		}
		long et = System.currentTimeMillis();
		System.out.println((et - st) + " ms");
	}
}