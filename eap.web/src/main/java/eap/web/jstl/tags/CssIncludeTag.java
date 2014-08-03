package eap.web.jstl.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import eap.EapContext;
import eap.WebEnv;
import eap.util.DateUtil;
import eap.util.FileUtil;
import eap.util.StringUtil;
import eap.util.UrlUtil;
import eap.util.YuiCompressor;

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
public class CssIncludeTag extends RequestContextAwareTag implements ParamUrlAware {
	
	private static final Map<String, String> alreadyMergeVersions = new ConcurrentHashMap<String, String>();
	private static String paramNameVersion = "_v";
	
	private String href;
	private String rel;
	private String media;
	private String charset;
	private String type;
	
	private String cssHrefSeparator;
	
	private Boolean mergeOutput;
	private String output;
	private String domain;
	
	private List<String> hrefUrls;
	@Override
	public void addParamUrl(String url) {
		this.hrefUrls.add(url);
	}

	@Override
	protected int doStartTagInternal() throws Exception {
		hrefUrls = new ArrayList<String>();
		return EVAL_PAGE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		WebEnv env = (WebEnv) EapContext.getEnv();
		
		String domainUrl = env.getDomainUrl(this.getDomain());
		
		List<String> hrefUrlList = new ArrayList<String>();
		hrefUrlList.addAll(hrefUrls);
		if (StringUtils.isNotBlank(this.getHref())) {
			String[] cssHrefs = StringUtils.split(this.getHref(), this.getCssHrefSeparator());
			for (String cssHref : cssHrefs) {
//				hrefUrlList.add(UrlUtil.fixUrl(cssHref));
				hrefUrlList.add(domainUrl + cssHref);
			}
		}
		
		List<String> outputUrls =new ArrayList<String>();
		if (this.getMergeOutput() && StringUtils.isNotBlank(this.getOutput())) {
			String o = this.getOutput();
			
			String lastVersion = alreadyMergeVersions.get(o);
			if (StringUtil.isBlank(lastVersion)) { // 不存在时， 合并CSS
				lastVersion = String.valueOf(DateUtil.format(DateUtil.currDate(), "yyyyMMddHHmmss"));
				alreadyMergeVersions.put(o, lastVersion);
				
				String outputFilename = env.getDomainPath(this.getDomain()) + o; //UrlUtil.getPathInWebRoot(o);
				List<String> inputFilenames = new ArrayList<String>();
				for (String hrefUrl : hrefUrlList) {
					inputFilenames.add(UrlUtil.getPathInWebRoot(hrefUrl));
				}
				
				FileUtil.mkdirs(outputFilename);
				
				YuiCompressor.compressCss(outputFilename, inputFilenames.toArray(new String[0]));
			}
			
			String mergeOutputPath = UrlUtil.appendParam((domainUrl + o), paramNameVersion, lastVersion);
			outputUrls.add(mergeOutputPath);
		} else {
//			outputUrls.addAll(hrefUrls);
//			if (StringUtils.isNotBlank(this.getHref())) {
//				String[] cssHrefs = StringUtils.split(this.getHref(), this.getCssHrefSeparator());
//				for (String cssHref : cssHrefs) {
//					outputUrls.add(UrlUtil.fixUrl(cssHref));
//				}
//			}
			outputUrls.addAll(hrefUrlList);
		}
		
		StringBuilder html = new StringBuilder();
		for (String outputUrl : outputUrls) {
			html.append("<link ");
			html.append("href=\"" + outputUrl + "\" ");
			html.append("rel=\"" + this.getRel() + "\" ");
			String t = this.getType();
			if (StringUtils.isNotBlank(t)) {
				html.append("type=\"" + t + "\" ");
			}
			String cs = this.getCharset();
			if (StringUtils.isNotBlank(cs)) {
				html.append("charset=\"" + cs + "\" ");
			}
			String m = this.getMedia();
			if (StringUtils.isNotBlank(m)) {
				html.append("media=\"" + m + "\" ");
			}
			
			html.append("/>").append(System.getProperty("line.separator"));
		}
		
		try {
			this.pageContext.getOut().write(html.toString());
		} catch (IOException e) {
			throw new JspException(e.getMessage(), e);
		}
		
		return EVAL_PAGE;
	}

	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		Assert.hasText(href, "'href' must not be empty");
		this.href = href;
	}
	public String getRel() {
		return rel != null ? rel : "stylesheet";
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getType() {
		return type != null ? type : "text/css";
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCssHrefSeparator() {
		return cssHrefSeparator != null ? cssHrefSeparator : " |,|;";
	}
	public void setCssHrefSeparator(String cssHrefSeparator) {
		this.cssHrefSeparator = cssHrefSeparator;
	}
	public Boolean getMergeOutput() {
		if (mergeOutput != null) {
			return mergeOutput;
		} else {
			return EapContext.getEnv().isProMode();
		}
	}
	public void setMergeOutput(Boolean mergeOutput) {
		this.mergeOutput = mergeOutput;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getDomain() {
		if (domain != null) {
			return domain;
		} else {
			return ((WebEnv)EapContext.getEnv()).getDomain();
		}
	}
}