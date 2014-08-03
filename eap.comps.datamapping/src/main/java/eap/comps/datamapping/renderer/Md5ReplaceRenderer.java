package eap.comps.datamapping.renderer;

import org.apache.commons.lang.StringEscapeUtils;

import eap.comps.datamapping.api.IRenderer;
import eap.comps.datamapping.definition.Definition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.util.EDcodeUtil;
import eap.util.StringUtil;

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
public class Md5ReplaceRenderer implements IRenderer {
	
	public static final String STYLE_ENCODING = "encoding";
	public static final String STYLE_SRC_START = "srcStart";
	public static final String STYLE_SRC_END = "srcEnd";
	public static final String STYLE_TARGET = "target";
	
	@Override
	public Object render(Object dataObj, Definition definition, RendererDefinition rd) {
		if (dataObj == null) return null;
		
		String encoding = StringUtil.defaultIfBlank(rd.getStyleValue(STYLE_ENCODING), "UTF-8");
		String srcStart = StringEscapeUtils.unescapeXml(rd.getStyleValue(STYLE_SRC_START));
		String srcEnd = StringEscapeUtils.unescapeXml(rd.getStyleValue(STYLE_SRC_END));
		String target = StringEscapeUtils.unescapeXml(rd.getStyleValue(STYLE_TARGET));
		
		String data = dataObj.toString();
		String text = StringUtil.substrBA(data, srcStart, srcEnd);
		String textEncoded = EDcodeUtil.md5Encode(text, encoding);
		
		int targetIndex = data.indexOf(target);
		if (targetIndex != -1) {
			return data.substring(0, targetIndex + target.length()) + textEncoded + data.substring(targetIndex + target.length());  
		}
		
		return data;
	}
	
	public static void main(String[] args) {
		
		String dataObj = "<xml><head><hash></hash></head><data>abc</data></xml>";
		String encoding = "UTF-8";
		String srcStart = "</head>";
		String srcEnd = "</xml>";
		String target = "<hash>";
		
		String data = dataObj.toString();
		String text = StringUtil.substrBA(data, srcStart, srcEnd).trim();
		String textEncoded = EDcodeUtil.md5Encode(text, encoding);
		
		System.out.println(text);
		int targetIndex = data.indexOf(target);
		System.out.println(targetIndex);
		if (targetIndex != -1) {
			String s = data.substring(0, targetIndex + target.length()) + textEncoded + data.substring(targetIndex + target.length());  
			System.out.println(s);
		}
	}
}
