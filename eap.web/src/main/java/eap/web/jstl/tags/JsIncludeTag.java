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
import eap.Env;
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
public class JsIncludeTag extends RequestContextAwareTag implements ParamUrlAware {
	
	private static final Map<String, String> alreadyMergeVersions = new ConcurrentHashMap<String, String>();
	private static String paramNameVersion = "_v";
	
	private String src;
	private String type;
	private String charset;
	private Boolean defer;
	private Boolean async;
	
	private String jsSrcSeparator;
	
	private Boolean mergeOutput;
	private String output;
	private String domain;
	
	private List<String> srcUrls;
	@Override
	public void addParamUrl(String url) {
		this.srcUrls.add(url);
	}
	
	@Override
	protected int doStartTagInternal() throws Exception {
		srcUrls = new ArrayList<String>();
		return EVAL_PAGE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		WebEnv env = (WebEnv) EapContext.getEnv();
		String domainUrl = env.getDomainUrl(this.getDomain());
		
		List<String> jsSrcList = new ArrayList<String>();
		jsSrcList.addAll(srcUrls);
		if (StringUtils.isNotBlank(this.getSrc())) {
			String[] jsSrcs = StringUtils.split(this.getSrc(), this.getJsSrcSeparator());
			for (String jsSrc : jsSrcs) {
//				jsSrcList.add(UrlUtil.fixUrl(jsSrc));
				jsSrcList.add(domainUrl + jsSrc);
			}
		}
		
		List<String> outputUrls =new ArrayList<String>();
		if (this.getMergeOutput() && StringUtils.isNotBlank(this.getOutput())) {
			String o = this.getOutput();
			
			String lastVersion = alreadyMergeVersions.get(o);
			if (StringUtil.isBlank(lastVersion)) { // 不存在时， 合并JS
				lastVersion = String.valueOf(DateUtil.format(DateUtil.currDate(), "yyyyMMddHHmmss"));
				alreadyMergeVersions.put(o, lastVersion);
				
				String outputFilename = env.getDomainPath(this.getDomain()) + o; // UrlUtil.getPathInWebRoot(o);
				List<String> inputFilenames = new ArrayList<String>();
				for (String jsSrc : jsSrcList) {
					inputFilenames.add(UrlUtil.getPathInWebRoot(jsSrc));
				}
				
				FileUtil.mkdirs(outputFilename);
				
				YuiCompressor.compressJavaScript(outputFilename, inputFilenames.toArray(new String[0]));
			}
			
			String mergeOutputPath = UrlUtil.appendParam((domainUrl + o), paramNameVersion, lastVersion);
			outputUrls.add(mergeOutputPath);
		} else {
//			outputUrls.addAll(srcUrls);
//			if (StringUtils.isNotBlank(this.getSrc())) {
//				String[] jsSrcs = StringUtils.split(this.getSrc(), this.getJsSrcSeparator());
//				for (String jsSrc : jsSrcs) {
//					outputUrls.add(UrlUtil.fixUrl(jsSrc));
//				}
//			}
			outputUrls.addAll(jsSrcList);
		}
		
		StringBuilder html = new StringBuilder();
		for (String outputUrl : outputUrls) {
			html.append("<script ");
			html.append("src=\"" + outputUrl + "\" ");
			html.append("type=\"" + this.getType() + "\" ");
			String cs = this.getCharset();
			if (StringUtils.isNotBlank(cs)) {
				html.append("charset=\"" + cs + "\" ");
			}
			if (this.getDefer()) {
				html.append("defer=\"true\" ");
			}
			if (this.getAsync()) {
				html.append("async=\"true\" ");
			}
			html.append("></script>").append(System.getProperty("line.separator"));
		}
		
		try {
			this.pageContext.getOut().write(html.toString());
		} catch (IOException e) {
			throw new JspException(e.getMessage(), e);
		}
		
		return EVAL_PAGE;
	}
	
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		Assert.hasText(src, "'src' must not be empty");
		this.src = src;
	}
	public String getType() {
		return type != null ? type : "text/javascript";
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCharset() {
//		return charset != null ? charset : env.getEncoding();
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public Boolean getDefer() {
		return defer != null ? defer : false;
	}
	public void setDefer(Boolean defer) {
		this.defer = defer;
	}
	public Boolean getAsync() {
		return async != null ? async : false;
	}
	public void setAsync(Boolean async) {
		this.async = async;
	}
	public String getJsSrcSeparator() {
		return jsSrcSeparator != null ? jsSrcSeparator : " |,|;";
	}
	public void setJsSrcSeparator(String jsSrcSeparator) {
		this.jsSrcSeparator = jsSrcSeparator;
	}

	public Boolean getMergeOutput() {
		if (mergeOutput != null) {
			return mergeOutput;
		} else {
			WebEnv env = (WebEnv) EapContext.getEnv();
			return env.isProMode();
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
			WebEnv env = (WebEnv) EapContext.getEnv();
			return env.getDomain();
		}
	}
}