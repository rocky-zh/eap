package eap.web.servlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eap.EapContext;
import eap.WebEnv;
import eap.base.BaseController;
import eap.util.BeanUtil;
import eap.util.EDcodeUtil;
import eap.util.FileUtil;
import eap.util.StringUtil;
import eap.util.UrlUtil;
import eap.util.YuiCompressor;
import eap.util.YuiCompressor.Options;

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
@Controller
public class CombineServlet extends BaseController {
	
	private Map<String, String> cachedResources = new ConcurrentHashMap(new HashMap<String, String>()); // TODO 
	
	public static final String CACHE_DIR = "/static/_COMBINE_CACHE";
	private String cacheDir;
	
	/*
	 * /combine?root=/static/styles&files=/a.css,/b.css
	 * /combine?files=/static/styles/a.css,/static/styles/b.css
	 */
	@RequestMapping("/combine")
	public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		WebEnv env = (WebEnv) EapContext.getEnv();
//		this.getParameter("type");
		String root = this.getParameter("root");
		String files = UrlUtil.decode(this.getParameter("files"));
		boolean debug = !env.isProMode();// this.getParameter("minify");
		
		if (StringUtil.isNotBlank(files)) {
			String resKey = request.getQueryString();
			String resPath = cachedResources.get(resKey);
			if (resPath == null || !new File(this.getFullResPath(resPath)).exists()) {
				String[] fileArr = BeanUtil.removeElement(files.split(","), "");
				if (fileArr.length > 0) {
					String fileSuffix = FileUtil.getFileNameSuffix(fileArr[0]).toLowerCase();
					String[] _fileArr = new String[fileArr.length];
					for (int i = 0; i < fileArr.length; i++) {
						_fileArr[i] = this.getWebRoot() + FileUtil.cleanPathTraversal(StringUtil.defaultIfBlank(root, "") + fileArr[i]);
					}
					
					if ("js".equals(fileSuffix) || "css".equals(fileSuffix)) {
						mkCacheDir();
						
						resPath = CACHE_DIR + "/" + EDcodeUtil.md5(resKey) + "." + fileSuffix;
						Options opts = new Options(fileSuffix);
						opts.debug = debug;
						
						YuiCompressor.compress(opts, this.getFullResPath(resPath), _fileArr);
						cachedResources.put(resKey, resPath);
					} 
				}
			}
			
			if (StringUtil.isNotBlank(resPath)) {
				request.getRequestDispatcher(resPath).forward(request, response);
			}
		}
	}
	
	private void mkCacheDir() {
		if (cacheDir == null) {
			new File(cacheDir).mkdirs();
			cacheDir = this.getWebRoot() + CACHE_DIR;
		}
	}
	
	private String getWebRoot() {
		WebEnv env = (WebEnv) EapContext.getEnv();
		return env.getWebRootPath();
	}
	
	private String getFullResPath(String resPath) {
		return this.getWebRoot() + resPath;
	}
}