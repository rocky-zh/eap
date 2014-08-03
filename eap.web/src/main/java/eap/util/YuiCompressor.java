package eap.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

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
public class YuiCompressor {
	private static final Logger logger = LoggerFactory.getLogger(YuiCompressor.class);
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static void main(String[] args) throws IOException {
		String[] in = {"D:/QDDT4J/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp11/wtpwebapps/com.dor.www/static/views/index/index.js"}; //  "index1.js"
		String out = "index-min.js";
		compressJavaScript(out, in);
		
		in = new String[] {"global.css", "index.css"};
		out = "index-min.css";
		compressCss(out, in);
	}
	
	public static void compress(Options o, String outputFilename, String[] inputFilenames) {
		if (StringUtil.isBlank(outputFilename) || inputFilenames == null || inputFilenames.length == 0 || inputFilenames[0] == null) {
			return;
		}
		
		Reader in = null;
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
			for (int i = 0; i < inputFilenames.length; i++) {
				if (i > 0) {
					out.write(LINE_SEPARATOR);
				}
				
				String inputFilename = inputFilenames[i];
				try {
					in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);
					
					if (o.debug) {
						IOUtils.write(IOUtils.toByteArray(in), out);
					} 
					else {
						if ("css".equalsIgnoreCase(o.type)) {
							CssCompressor compressor = new CssCompressor(in);
							in.close(); in = null;
							
							compressor.compress(out, o.lineBreakPos);
						} else {
							JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
							in.close(); in = null;
							
							compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);
						}
					}
				} catch (FileNotFoundException e) {
					logger.error(e.getMessage(), e);
					//TODO NOT HANDLE
				} finally {
					IOUtils.closeQuietly(in);
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	public static void compressJavaScript(String outputFilename, String... inputFilenames) {
		compress(new Options("js"), outputFilename, inputFilenames);
	}
	public static void compressJavaScript(String charset, String outputFilename, String... inputFilenames) {
		compress(new Options("js", charset), outputFilename, inputFilenames);
	}
	public static void compressCss(String outputFilename, String... inputFilenames) {
		compress(new Options("css"), outputFilename, inputFilenames);
	}
	public static void compressCss(String charset, String outputFilename, String... inputFilenames) {
		compress(new Options("css", charset), outputFilename, inputFilenames);
	}
	
	public static class Options {
		public String type; // css | js
		public String charset = "UTF-8"; // "UTF-8";
		public int lineBreakPos = -1;
		public boolean munge = true;
		public boolean verbose = false;
		public boolean preserveAllSemiColons = false;
		public boolean disableOptimizations = false;
		
		public boolean debug = false;
		
		public Options(String type) {
			this.type = type;
		}
		public Options(String type, String charset) {
			this.type = type;
			this.charset = charset;
		}
	}
	
	private static class YuiCompressorErrorReporter implements ErrorReporter {
		public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
			if (line < 0) {
				logger.warn(message);
			} else {
				logger.warn(line + ':' + lineOffset + ':' + message);
			}
		}
		
		public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
			if (line < 0) {
				logger.error(message);
			} else {
				logger.error(line + ':' + lineOffset + ':' + message);
			}
		}
		
		public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
			error(message, sourceName, line, lineSource, lineOffset);
			return new EvaluatorException(message);
		}
	}
}
