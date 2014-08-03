package eap.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

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
public class UrlUtil { // AntPathMatcher
	
	private static String webRootPath = "";
	private static String webContextPath = "";
	
	public static String ENCODE_UTF8_REGEX = "^(?:[\\x00-\\x7f]|[\\xfc-\\xff][\\x80-\\xbf]{5}|[\\xf8-\\xfb][\\x80-\\xbf]{4}|[\\xf0-\\xf7][\\x80-\\xbf]{3}|[\\xe0-\\xef][\\x80-\\xbf]{2}|[\\xc0-\\xdf][\\x80-\\xbf])+$";
	public static String ENCODE_GBK_REGEX = "^(?:[\\u4E00-\\u9FA5])+$";
//	/[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi;
	public static String SE_REGEX = null;
	static {
		List<String[]> serList = new ArrayList<String[]>();
		serList.add(new String[] {"www.baidu.com", "wd"});
		serList.add(new String[] {"(zhidao|news|baike|wenku|m|image|video).baidu.com", "word"});
		serList.add(new String[] {"tieba.baidu.com", "kw"});
		serList.add(new String[] {"google", "q"});
		serList.add(new String[] {"(www|news|image).yahoo.cn", "q"});
		serList.add(new String[] {"(search|blog.search|news.search|finance.search|sports.search).yahoo.com", "[\\?|&]p"});
		serList.add(new String[] {"(www|news|image|video).youdao.com", "q"});
		serList.add(new String[] {"(www|news|image|video).soso.com", "w"});
		serList.add(new String[] {"(wenwen|baike).soso.com", "sp"});
		serList.add(new String[] {"sogou.com", "query"});
		serList.add(new String[] {"cn.bing.com", "q"});
		serList.add(new String[] {"qihoo.com", "kw"});
		serList.add(new String[] {"zg3721.com", "keyword"});
		serList.add(new String[] {"search.sina.com.cn", "q"});
		serList.add(new String[] {"iask.sina.com.cn", "key"});
		serList.add(new String[] {"zhongsou.com", "w"});
		serList.add(new String[] {"www.yisou.com", "q"});
		serList.add(new String[] {"i.easou.com", "q"});
		serList.add(new String[] {"(search|news).lycos.com", "q"});
		serList.add(new String[] {"www.onsee.info", "search"});
		serList.add(new String[] {"search.tom", "w"});
		serList.add(new String[] {"(news.so|so).360.cn", "q"});
		
		StringBuilder keywordReg = new StringBuilder();
		keywordReg.append("(?:");
		for (String[] ser : serList) {
			keywordReg.append(String.format("(%s).+?%s=|", // "http:\\/\\/((news\\.so|search|so)\\.?360).+?q=" +
//				ser.length >= 3 ? ser[2] : "www",
				StringUtil.replace(ser[0], ".", "\\."),
				ser[1]
			));
		}
		keywordReg.deleteCharAt(keywordReg.length() - 1);
		keywordReg.append(")([^&]*)");
		SE_REGEX = keywordReg.toString();
	}
	
	public static String getFileName(String url) {
		if (StringUtils.isBlank(url)) {
			return "";
		}
		
		int endIndex = url.lastIndexOf("?");
		if (endIndex == -1) {
			endIndex = url.length();
		}
		int startIndex = url.lastIndexOf("/", endIndex);
		if (endIndex > (startIndex + 1)) {
			return url.substring(startIndex + 1, endIndex);
		}
		
		return "";
	}
	
	public static String getFilePostfixName(String url) {
		String fileName = getFileName(url);
		if (StringUtils.isNotBlank(fileName)) {
			int startIdx = fileName.lastIndexOf(".");
			if (startIdx > -1) {
				return fileName.substring(startIdx + 1);
			}
		}
		
		return "";
	}
	
	public static Map<String, String> getUrlQueryStringAsMap(String url) {
		if (StringUtil.isBlank(url)) {
			return Collections.EMPTY_MAP;
		}
		
		int startIdx = url.lastIndexOf("?");
		if (startIdx >= 0) {
			String[] params = StringUtil.split(url.substring(startIdx + 1), "&");
			if (params != null && params.length > 0) {
				Map<String, String> paramMap = new HashMap<String, String>();
				String[] paramPairArr = null;
				for (String paramPair : params) {
					paramPairArr = StringUtil.split(paramPair, "=");
					paramMap.put(paramPairArr[0], (paramPairArr.length >=2 ? paramPairArr[1] : null));
				}
				
				return paramMap;
			}
		}
		
		return Collections.EMPTY_MAP;
	}
	
	public static String appendParam(String url, String name, String value) {
		return appendParams(url, new String[][] {{name, value}});
	}
	public static String appendParams(String url, String[][] params) {
		if (params == null || params.length == 0) {
			return url;
		}
		
		StringBuilder buf = new StringBuilder(url);
		buf.append((buf.lastIndexOf("?") == -1) ? "?" : "&");
		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				buf.append("&");
			}
			buf.append(params[i][0] + "=" + params[i][1]);
		}
		
		return buf.toString();
	}
	
	public static String formatUrl(String url, String paramStr) {
		if (StringUtils.isBlank(paramStr)) {
			return url;
		}
		StringBuilder result = new StringBuilder();
		
		Object params = null;
		if (paramStr.charAt(0) == '{' && paramStr.charAt(paramStr.length() - 1) == '}') {
			params = JsonUtil.parseJson(paramStr, HashMap.class);
		} else {
			params = StringUtils.split(paramStr, ",");
		}
		
		int paramIdx = 0;
		char[] chars = url.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '{') {
				int idx_s = i;
				while (chars[++i] != '}') {}
				int idx_e = i;
				
				if (params instanceof Object[]) {
					result.append(((Object[])params)[paramIdx++]);
				} else if (params instanceof Map) {
					String name = url.substring(idx_s + 1, idx_e);
					result.append(((Map)params).get(name));
				}
			} else {
				result.append(c);
			}
		}
		
		return result.toString();
	}
	
	public static String encode(String text, String encoding) {
		if (StringUtils.isBlank(text)) {
			return "";
		}
		
		try {
			return URLEncoder.encode(text, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	public static String encode(String text) {
		return encode(text, "UTF-8");
	}
	public static String decode(String text, String encoding) {
		if (StringUtils.isBlank(text)) {
			return "";
		}
		
		try {
			return URLDecoder.decode(text, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	public static String decode(String text) {
		return decode(text, "UTF-8");
	}
	public static String decodeForUnknowCharset(String text) {
		String unescapeString = EDcodeUtil.unescape(text);
		Pattern encodePattGBK = Pattern.compile(ENCODE_GBK_REGEX);
		Matcher encodeMatGBK = encodePattGBK.matcher(unescapeString);
		if (encodeMatGBK.matches()) {
			return unescapeString;
		} else {
			String encodeString = "GBK";
			Pattern encodePattUTF = Pattern.compile(ENCODE_UTF8_REGEX);
			Matcher encodeMatUTF = encodePattUTF.matcher(unescapeString);
			if (encodeMatUTF.matches()) {
				encodeString = "UTF-8";
			}
			try {
				return URLDecoder.decode(text, encodeString);
			} catch (UnsupportedEncodingException e) {
				return text;
			}
		}
	}
	
	/**
	 * 根据URL解析搜索引擎信息
	 * @param url
	 * @return 接触成功返回 [搜索引擎提供商, 搜索引擎关键字] ；否之 null
	 */
	public static String[] parseSe(String url) {
		if (StringUtil.isBlank(url)) {
			return null;
		}
		
		Pattern sePatt = Pattern.compile(SE_REGEX);
		Matcher seMat = sePatt.matcher(url);
		String sep = null; // 搜索引擎提供商
		String seWd = null; // 搜索引擎关键字
		if (seMat.find()) {
			int c = seMat.groupCount();
			for (int i = 1; i < c; i++) {
				sep = seMat.group(i);
				if (StringUtil.isNotBlank(sep)) {
					break;
				}
			}
			seWd = seMat.group(c);
		}
		if (StringUtil.isNotBlank(seWd)) {
			String keywordsTmp = seWd.replace("http://", "");
			seWd = decodeForUnknowCharset(keywordsTmp);
//			String unescapeString = EDcodeUtil.unescape(keywordsTmp);
//			Pattern encodePattGBK = Pattern.compile(ENCODE_GBK_REGEX);
//			Matcher encodeMatGBK = encodePattGBK.matcher(unescapeString);
//			if (encodeMatGBK.matches()) {
//				seWd = unescapeString;
//			} else {
//				String encodeString = "GBK";
//				Pattern encodePattUTF = Pattern.compile(ENCODE_UTF8_REGEX);
//				Matcher encodeMatUTF = encodePattUTF.matcher(unescapeString);
//				if (encodeMatUTF.matches()) {
//					encodeString = "UTF-8";
//				}
//				try {
//					seWd = URLDecoder.decode(keywordsTmp, encodeString);
//				} catch (UnsupportedEncodingException e) {
//					seWd = keywordsTmp;
//				}
//			}
		}
		
		return StringUtil.isNotBlank(seWd) ? new String[] {sep, seWd} : null;
	}
	
	public static String fixUrl(String url) {
		if (url == null) {
			return null;
		}
		
		if (url.contains("://")) {
			return url;
		}
		else if (url.startsWith("/")) {
			return webContextPath + url;
		}
		else {
			return url;
		}
	}
	
	public static String getRelativePath(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		
		int fromIndex = -1;
		if ((fromIndex = url.indexOf("://")) != -1) {
			fromIndex = url.indexOf("/", fromIndex + 3);
		} else {
			fromIndex = 0;
		}
		
		if (StringUtils.isNotBlank(webContextPath) && url.startsWith(webContextPath, fromIndex)) {
			fromIndex += webContextPath.length();
		}
		
		int endIndex = -1;
		if ((endIndex = url.indexOf("?", fromIndex + 1)) != -1) {
		} else {
			endIndex = url.length();
		}
		
		return url.substring(fromIndex, endIndex);
	}
	public static String getPathInWebRoot(String url) {
		Assert.hasText(webRootPath, "webRootPath must not be empty");
		return webRootPath + StringUtil.defaultIfBlank(getRelativePath(url), "/");
	}
	
	public static String getDomain(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		String startFlag = "://";
		int len = url.indexOf("/", url.indexOf(startFlag) + startFlag.length());
		if (len == -1) len = url.length();
		
		String domain = url.substring(url.indexOf(startFlag) + startFlag.length(), len);
		int portIdx = domain.lastIndexOf(":");
		if (portIdx != -1) {
			domain = domain.substring(0, portIdx);
		}
		
		return domain;
	}
	public static String getTopDomain(String url) {
		String domain = getDomain(url);
		if (StringUtil.isNotBlank(domain)) {
			String[] domainSubfixs = {
					".com",".edu",".gov",".int",".net",".biz",".info",".pro",".name",".museum",".coop",".aero",".idv",
					".cc",".tv",
					".cn",".hk",".mo",".tw"
				};
				for (String domainSubfix : domainSubfixs) {
					int sIdx = -1;
					if ((sIdx = domain.indexOf(domainSubfix)) != -1) {
						if ((sIdx = domain.lastIndexOf(".", sIdx -1)) != -1) {
							return domain.substring(sIdx + 1);
						}
					}
				}
				return domain;
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		
//		String uuuu = "http://x.caidor.com:8080/tmp/stats.jsp";
//		System.out.println(getTopDomain(uuuu));
//		Map m = getUrlQueryStringAsMap(uuuu);
//		System.out.println(m);
//		Env.webContextPath = "/www";
//		String s111 = "http://analytics2.caidor.com/r.js";
//		String urlll = "http://analytics2.caidor.com/www/a.js?dddd";
//		System.out.println(getRelativePath(s111));
//		System.out.println(getRelativePath(urlll));
//		
//		if (true) return;
		
		List<String> list=new ArrayList<String>();
		list.add("http://www.baidu.com/s?wd=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%95%99%E7%A8%8B&rsv_spt=1&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg&rsv_sug3=1&rsv_sug1=1&rsv_sug4=458&oq=%E6%AD%A3%E5%88%99&rsp=6&f=3&rsv_sug5=0");
		list.add("http://news.baidu.com/ns?cl=2&rn=20&tn=news&word=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%95%99%E7%A8%8B&ie=utf-8");
		list.add("http://baike.baidu.com/search/none?word=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD%BD%CC%B3%CC&convertword=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD%BD%CC%B3%CC&flag=strong");
		list.add("http://wenku.baidu.com/search?fr=bk&word=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD%BD%CC%B3%CC&lm=0&od=0");
		list.add("http://zhidao.baidu.com/search?pn=0&&rn=10&word=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD%BD%CC%B3%CC");
		list.add("http://tieba.baidu.com/f?kw=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&fr=fenter&prequery=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD%BD%CC%B3%CC");
		list.add("http://image.baidu.com/i?tn=baiduimage&ct=201326592&lm=-1&cl=2&word=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD%BD%CC%B3%CC&t=3");
		list.add("http://video.baidu.com/v?ct=301989888&s=25&ie=utf-8&word=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%95%99%E7%A8%8B");
		list.add("http://www.youdao.com/search?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&lq=%E6%90%9C%E7%B4%A2%E5%BC%95%E6%93%8Eurl+%E5%8F%82%E6%95%B0%E8%8E%B7%E5%8F%96&ue=utf8&T1=1355292998933&keyfrom=web.top.suggest");
		list.add("http://image.youdao.com/search?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&keyfrom=web.top");
		list.add("http://video.youdao.com/search?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&keyfrom=image.top");
		list.add("http://news.youdao.com/search?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&start=0&length=10&ue=utf8&s=&tl=&keyfrom=news.index");
		list.add("http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&tbo=d&site=&source=hp&q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%95%99%E7%A8%8B&btnG=Google+%E6%90%9C%E7%B4%A2");
		list.add("http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%95%99%E7%A8%8B&um=1&ie=UTF-8&tbm=isch&source=og&sa=N&tab=wi&ei=WCXIUP2NPImviQfOx4GACw&biw=1366&bih=664&sei=ciXIUMKvCaqdiAf0q4Bg");
		list.add("http://www.google.com.hk/search?hl=zh-CN&gl=cn&tbm=nws&q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&oq=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&gs_l=news-cc.3...815.815.0.1059.1.1.0.0.0.0.0.0..0.0...0.0...1ac.");
		list.add("http://www.soso.com/q?pid=s.idx&cid=s.idx.se&w=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD");
		list.add("http://news.soso.com/n.q?sc=news&ty=c&ie=utf-8&w=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://image.soso.com/image.cgi?sc=img&pid=web.img&ie=utf-8&w=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://video.soso.com/search/?ie=utf-8&w=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://wenwen.soso.com/z/Search.e?sp=S%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://baike.soso.com/Search.e?sp=S%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://www.sogou.com/web?query=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&_asf=www.sogou.com&_ast=1355294256&w=01019900&p=40040100&sut=2384&sst0=1355294255610");
		list.add("http://news.sogou.com/news?p=40230447&interV=kKIOkrELjbgPmLkElbkTkKIRmLkEkL8TkKIMkbELjbgQmLkElbcTkKIJmbELjbgRmLkEkLYTkKIM%0AlrGKCzXlNjYElKJ7z%2BhEzO1Lj%2BlHzrGIOzzgEl%2BXJ6IPjeh5yuF9j%2Bh5yupNj%2BlHzrGIOzzgLEqX%0AQ6IPjf19z%2BFNj%2Bh5yupNj%2BlHzo%3D%3D_-1852159547&query=%u6B63%u5219%u8868%u8FBE%u5F0F");
		list.add("http://zhishi.sogou.com/zhishi?query=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&p=&w=03021800");
		list.add("http://pic.sogou.com/pics?p=40230504&interV=kKIOkrELjbgPmLkElbkTkKIRmLkEkL8TkKIMkbELjbgQmLkElbcTkKIJmbELjbgRmLkEkLYTkKIM%0AlrGIOzzgNCCNIaIPjfBAyup5zKR6wu981qR7zOMTKVfjM0TiLEAElKJPxuRPxuQG0OVLzKR7zOM%3D_917565096&query=%u6B63%u5219%u8868%u8FBE%u5F0F");
		list.add("http://v.sogou.com/v?query=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&p=&w=&interV=kKIOkrELjbgPmLkElbkTkKIRmLkEkL8TkKIMkbELjbgQmLkElbcTkKIJmbELjbgRmLkEkLYTkKIM%250Alo%253D%253D_925327140");
		list.add("http://cn.bing.com/search?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&qs=n&form=QBLH&pq=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&sc=8-2&sp=-1&sk=");
		list.add("http://cn.bing.com/news/search?q=%e6%ad%a3%e5%88%99%e8%a1%a8%e8%be%be%e5%bc%8f&FORM=HDRSC6");
		list.add("http://cn.bing.com/images/search?q=%e6%ad%a3%e5%88%99%e8%a1%a8%e8%be%be%e5%bc%8f&FORM=HDRSC2");
		list.add("http://cn.bing.com/videos/search?q=%e6%ad%a3%e5%88%99%e8%a1%a8%e8%be%be%e5%bc%8f&FORM=HDRSC3");
		list.add("http://www.yahoo.cn/s?src=8003&vendor=100101&source=ycnhp_search_button&q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://news.yahoo.cn/s?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://image.yahoo.cn/s?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://search.yahoo.com/search;_ylt=Alun50Il1GK.v4Mg2_2k3sObvZx4?p=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-701");
		list.add("http://blog.search.yahoo.com/search;_ylt=A0oGdd6rK8hQdCQAq81XNyoA?p=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&fr=yfp-t-701&fr2=piv-web");
		list.add("http://news.search.yahoo.com/search;_ylt=A2KJjbzFK8hQOHMAateaxAt.?&p=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&fr2=piv-blog&fr=yfp-t-701");
		list.add("http://finance.search.yahoo.com/search;_ylt=A2KJjbzTK8hQlm4AbuXQtDMD?&p=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&fr2=piv-news&fr=yfp-t-701");
		list.add("http://sports.search.yahoo.com/search;_ylt=A2KJjbzmK8hQgyEALwOTmYlQ?&p=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&fr2=piv-finance&fr=yfp-t-701");
		list.add("http://www.qihoo.com/wenda.php?kw=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&ff=1&do=search&noq=q&src=nindex");
		list.add("http://www.qihoo.com/wenda.php?kw=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&do=search&area=2&src=bbs");
		list.add("http://www.qihoo.com/wenda.php?r=search/index&kw=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&do=search&area=2&src=wenda_tab&type=blog");
		list.add("http://www.zg3721.com/search_views_index.asp?keyword=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD");
		list.add("http://search.sina.com.cn/?from=home&range=all&c=news&q=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD");
		list.add("http://search.sina.com.cn/?q=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&c=zt&range=title");
		list.add("http://search.sina.com.cn/?q=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&c=blog");
		list.add("http://search.sina.com.cn/?q=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&c=product");
		list.add("http://iask.sina.com.cn/search_engine/search_knowledge_engine.php?title=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&search=&key=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD&classid=0&gjss=0&type=0");
		list.add("http://p.zhongsou.com/p?v=%CD%F8%D2%B3&y=9&k=com&netid=&w=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD");
		list.add("http://www.zhongsou.com/third.cgi?v=%D7%DB%BA%CF&y=5&k=com&netid=&w=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD");
		list.add("http://zixun.zhongsou.com/n?v=%D7%CA%D1%B6&y=4&k=com&netid=&w=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD");
		list.add("http://bbs.zhongsou.com/b?w=%D5%FD%D4%F2%B1%ED%B4%EF%CA%BD");
		list.add("http://www.yisou.com/s?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&ty=w&sr=1");
		list.add("http://i.easou.com/s.m?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F&actType=1&esid=OnMaHQRTuHb&wver=c");
		list.add("http://search.lycos.com/web?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://news.lycos.com/search?q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://www.seekon.info/index.php?search=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://so.360.cn/s?ie=utf-8&src=hao_phome&q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		list.add("http://news.so.360.cn/ns?ie=utf-8&tn=news&q=%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F");
		
		Set<String> s = new HashSet<String>();
		for (int i = 0; i < 1; i++) {
		for (String url : list) {
			String[] p=parseSe(url);
			System.out.println(url + " ===> " + (p != null ? p[0] + " : " + p[1] : ""));
//			String u = shortUrl(RandomStringUtils.random(6), url)[0];
//			if (s.contains(u)) {
//				System.out.println(u);
//			} else {
//				s.add(u);
//			}
		}
		}
	}
}