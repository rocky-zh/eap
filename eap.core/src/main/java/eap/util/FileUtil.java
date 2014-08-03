package eap.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.FileSystemUtils;

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
public class FileUtil extends FileUtils {
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String SEPARATOR = File.separator;
	public static final String WRITING_TEMP_FILE_PREFIX = ".writing";
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	public static boolean mkdirs(String path) {
		File f = new File(path);
		
		File dir = null;
		if (f.isDirectory()) {
			dir = f;
		} else {
			dir = f.getParentFile();
		}
		
		return dir.mkdirs();
	}
	
	public static void write(String path, byte[] data, Integer bufferSize) {
		 write(path, new ByteArrayInputStream(data), bufferSize);
	}
	
	public static void write(String path, InputStream is, Integer bufferSize) { // TODO APPEND
		mkdirs(path);
		File outFile = new File(path);
		
		File tmpOutFile = new File(outFile.getAbsolutePath() + WRITING_TEMP_FILE_PREFIX);
		
		OutputStream os = null;
		try {
			os = new FileOutputStream(tmpOutFile);
			
			int i = -1;
			byte[] buf = new byte[(bufferSize != null ? bufferSize : DEFAULT_BUFFER_SIZE)];
			while ((i = is.read(buf)) != -1) {
				os.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
//					throw new IllegalStateException(e.getMessage(), e);
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
//					throw new IllegalStateException(e.getMessage(), e);
				}
			}
		}
		
		if (outFile.exists()) {
			outFile.delete();
		}
		tmpOutFile.renameTo(outFile);
	}
	
	public static byte[] readBytes(String path) {
		InputStream is = null;
		try {
			is = read(path);
			return IOUtils.toByteArray(is);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
//					throw new IllegalStateException(e.getMessage(), e);
				}
			}
		}
	}
	
	public static InputStream read(String path) {
		try {
			return new FileInputStream(new File(path));
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static boolean delete(String path) {
		File root = new File(path);
		return FileSystemUtils.deleteRecursively(root);
	}
	
	public static String getFileName(String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}
		
		int i = path.lastIndexOf(SEPARATOR);
		if (i > 0) {
			return path.substring(i + 1);
		}
		
		return null;
	}
	
	public static String getFileNameWithoutSuffix(String path) {
		String fileName = StringUtils.defaultIfBlank(getFileName(path), path);
		if (StringUtils.isNotBlank(fileName)) {
			int i = fileName.lastIndexOf(".");
			if (i > 0) {
				return fileName.substring(0, i);
			}
		}
		
		return null;
	}
	
	public static String getFileNameSuffix(String path) {
		String fileName = StringUtils.defaultIfBlank(getFileName(path), path);
		if (StringUtils.isNotBlank(fileName)) {
			int i = fileName.lastIndexOf(".");
			if (i > 0) {
				return fileName.substring(i + 1);
			}
		}
		
		return null;
	}
	
	public static String appendFileName(String path, String appendText) {
		if (StringUtils.isBlank(path)) {
			return null;
		}
		
		int i = path.lastIndexOf(".");
		if (i > 0) {
			return path.substring(0, i) + appendText + path.substring(i);
		} else {
			return path + appendText;
		}
	}
	
	public static String renameFileNameSubifx(String path, String newSubfix) {
		if (StringUtils.isBlank(path)) {
			return null;
		}
		
		int i = path.lastIndexOf(".");
		if (i > 0) {
			return path.substring(0, i) + newSubfix;
		} else {
			return path + newSubfix;
		}
	}
	
	public static String cleanPathTraversal(String path) {
		if (path == null) {
			return null;
		}
		if ("".equals(path)) {
			return "";
		}
		
		// https://www.owasp.org/index.php/Path_Traversal
		/*
		 * %2e%2e%2f represents ../
		 * %2e%2e/ represents ../
		 * ..%2f represents ../ 
		 * %2e%2e%5c represents ..\
		 * %2e%2e\ represents ..\ 
		 * ..%5c represents ..\ 
		 * %252e%252e%255c represents ..\ 
		 * ..%255c represents ..\ and so on. 
		 * ..%c0%af represents ../ 
		 * ..%c1%9c represents ..\ 
		 */
		
		return path.replaceAll("%2e|%2f|%5c|%252e|%255c|%c0%af|%c1\\%9c|\\.\\.|%00", "");
//		return StringUtil.replaceEach(path, new String[] {
//			"%"
//		}, new String[] {""});
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		String f = "c:/tmp/xxx.log";
//		System.out.println(getFileName(f));
//		System.out.println(getFileNameWithoutSuffix(f));
//		System.out.println(getFileNameWithoutSuffix("xxx.log"));
//		System.out.println(getFileNameSuffix(f));
		System.out.println(getFileNameSuffix("xxx.log"));
//		
//		String img = "c:/tmp/img/xxx.jpg";
//		System.out.println(appendFileName(img, "_200x200"));
//		System.out.println(appendFileName("c:/tmp/img/xxx", "_200x200"));
//		System.out.println(renameFileNameSubifx(img, ".bmp"));
//		System.out.println(renameFileNameSubifx("c:/tmp/img/xxx", ".bmp"));
		
//		String p = "../";//%2e%2e%2f%2e%2e/..%2f%2e%2e%5c%2e%2e\\\\..%5c%252e%252e%255c..%255c";
		String p = "/..///../%252e%252e%255ctmp14/wtpwebapps";
//		p = cleanPathTraversal(p);
//		System.out.println(p);
		
		System.out.println(File.separator);
		
		
		String pathname=URLDecoder.decode("/pdfdown?WEB-INF/classes/jdbc.properties.pdf&download=.xls", "utf-8");
		System.out.println("11111 " + pathname);
		if(StringUtils.isNotBlank(pathname) && pathname.indexOf("download") != -1 && pathname.indexOf("..") == -1){
			String fuffix = pathname.substring(pathname.lastIndexOf(".") , pathname.length()) ;
			System.out.println(fuffix);
		}	  
		
	}
}