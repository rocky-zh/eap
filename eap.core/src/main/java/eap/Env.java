package eap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;

import eap.util.ResourceUtil;

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
public class Env extends StandardEnvironment implements Map<String, Object> {
	
	public static final String DEFAULT_ENV_CONFIG_PATH = "classpath*:eap_env.properties";
	public static final String ENV_PROPERTY_SOURCE_NAME = "env";
	public static final String ENV_CONFIG_PATH = "classpath:env.properties";
	
	public static final String ENV_POPS_APP_MODE_PRO = "pro"; // production
//	public static final String ENV_POPS_APP_MODE_DEV = "dev"; // development
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected String sourceName = ENV_PROPERTY_SOURCE_NAME;
	protected String configPath = ENV_CONFIG_PATH;
	
	protected String propPrefix;
	
	protected IdWorker idWorker;
	
	public Env() {
		String appConfigPath = super.getProperty("app.configPath"); // from jvm args 
		if (StringUtils.isNotBlank(appConfigPath)) {
			configPath = appConfigPath;
		}
	}
	
	public void refresh(Map<String, String> overwriteEnvMap) {
		synchronized (this.getPropertySources()) {
			try {
				MapPropertySource envProperties = this.loadEnvPropertiesPropertySource(sourceName, configPath);
				
				if (overwriteEnvMap != null && overwriteEnvMap.size() > 0) {
					envProperties.getSource().putAll(overwriteEnvMap);
					logger.info("overwrite env data from envMap");
					
					store(overwriteEnvMap);
				}
				
				MutablePropertySources propertySources = this.getPropertySources();
				propertySources.remove(sourceName);
//				propertySources.addFirst(envProperties);
				propertySources.addAfter("systemProperties", envProperties); // systemProperties -> env -> systemEnvironment
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			
			propPrefix = super.getProperty("app.mode", "");
			idWorker = new IdWorker(getAppId());
		}
	}
	private MapPropertySource loadEnvPropertiesPropertySource(String name, String path) throws IOException {
		MapPropertySource envProperties = this.loadDefaultEnvPropertiesPropertySource(name);
		logger.info("loaded env file: " + DEFAULT_ENV_CONFIG_PATH);
		
		if (StringUtils.isNotBlank(path)) {
			try {
				envProperties.getSource().putAll(new ResourcePropertySource(ResourceUtil.getResource(path)).getSource());
				logger.info("loaded env file: " + path);
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
		}
		
//		String importFiles = (String) envProperties.getProperty("import.files");
//		if (StringUtils.isNotBlank(importFiles)) {
//			String fileSeparator = StringUtils.defaultIfBlank((String) envProperties.getProperty("import.separator"), ",");;
//			String[] importFileArray = StringUtils.split(importFiles, fileSeparator);
//			for (String importFile : importFileArray) {
//				try {
//					envProperties.getSource().putAll(new ResourcePropertySource(new ClassPathResource(importFile)).getSource());
//				} catch (Exception e) {
//					e.printStackTrace();
//					logger.error(e.getMessage(), e);
//					continue;
//				}
//				
//				logger.info("loaded env file(sub): " + importFile);
//			}
//		}
		
		return envProperties;
	}
	private MapPropertySource loadDefaultEnvPropertiesPropertySource(String name) throws IOException {
		Resource[] resources = ResourceUtil.getResources(DEFAULT_ENV_CONFIG_PATH);
		
		if (resources != null && resources.length > 0) {
			MapPropertySource envProperties = null;
//			for (int i = resources.length - 1; i >= 0; i--) {
			for (int i = 0; i < resources.length; i++) {
				if (envProperties == null) {
					envProperties = new ResourcePropertySource(name, resources[i]);
				} else {
					envProperties.getSource().putAll(new ResourcePropertySource(name, resources[i]).getSource());
				}
			}
			return envProperties;
		}
		
		return null;
	}
	private void store(Map<String, String> overwriteEnvMap) throws IOException {
		Resource envResource = ResourceUtil.getResource(configPath);
		Properties newEnvProperties = new Properties();
		newEnvProperties.putAll(new ResourcePropertySource(envResource).getSource());
		newEnvProperties.putAll(overwriteEnvMap);
		FileOutputStream newEnvPropertiesIO = new FileOutputStream(envResource.getFile());
		try {
			newEnvProperties.store(newEnvPropertiesIO, "");
		} finally {
			if (newEnvPropertiesIO != null) {
				try {
					newEnvPropertiesIO.close();
				} catch (Exception e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}
		
		logger.info("restore env data from envMap");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getEnvProperties() {
		return (Map<String, Object>) this.getPropertySources().get(sourceName).getSource();
	}
	
	public Map<String, Object> getProperties() {
		Map<String, Object> pops = new LinkedHashMap<String, Object>();
		pops.putAll(this.getSystemEnvironment());
		pops.putAll(this.getEnvProperties());
		pops.putAll(this.getSystemProperties());
		
		return pops;
	}
	
	public Map<String, Object> filterForPrefix(String prefix) {
		if (StringUtils.isBlank(prefix)) {
			return this.getEnvProperties();
		}
		
		Map<String, Object> srcMap = this.getEnvProperties();
		Map<String, Object> descMap = new LinkedHashMap<String, Object>();
		for (Object keyObj : srcMap.keySet()) {
			String key = (String) keyObj;
			int prefixLen = prefix.length();
			if (key.startsWith(prefix)) {
				descMap.put(key.substring(prefixLen), srcMap.get(key));
			}
		}
		
		return descMap;
	}
	
	public String getProperty(String key) {
		String propValue = null;
		if (StringUtils.isNotBlank(propPrefix)) {
			propValue = super.getProperty(propPrefix + "." + key);
		}
		
		return StringUtils.isNotBlank(propValue) ? propValue : super.getProperty(key);
	}
	public String getProperty(String key, String defaultValue) {
		String propValue = null;
		if (StringUtils.isNotBlank(propPrefix)) {
			propValue = super.getProperty(propPrefix + "." + key);
		}
		
		return StringUtils.isNotBlank(propValue) ? propValue : super.getProperty(key, defaultValue);
	}
	public <T> T getProperty(String key, Class<T> targetType) {
		T propValue = null;
		if (StringUtils.isNotBlank(propPrefix)) {
			propValue = super.getProperty(propPrefix + "." + key, targetType);
		}
		
		return propValue != null ? propValue : super.getProperty(key, targetType);
	}
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		T propValue = null;
		if (StringUtils.isNotBlank(propPrefix)) {
			propValue = super.getProperty(propPrefix + "." + key, targetType);
		}
		
		return propValue != null ? propValue : super.getProperty(key, targetType, defaultValue);
	}
	public <T> Class<T> getPropertyAsClass(String key, Class<T> targetType) {
		Class<T> propValue = null;
		if (StringUtils.isNotBlank(propPrefix)) {
			propValue = super.getPropertyAsClass(propPrefix + "." + key, targetType);
		}
		
		return propValue != null ? propValue : super.getPropertyAsClass(key, targetType);
	}
	public String getRequiredProperty(String key) throws IllegalStateException {
		String propValue = null;
		if (StringUtils.isNotBlank(propPrefix)) {
			propValue = super.getProperty(propPrefix + "." + key); // getRequiredProperty
		}
		
		return StringUtils.isNotBlank(propValue) ? propValue : super.getRequiredProperty(key);
	}
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		T propValue = null;
		if (StringUtils.isNotBlank(propPrefix)) {
			propValue = super.getProperty(propPrefix + "." + key, targetType);
		}
		
		return propValue != null ? propValue : super.getRequiredProperty(key, targetType);
	}
	
	public String getAppName() {
		String appName =  this.getProperty("app.name");
		if (StringUtils.isBlank(appName)) {
			throw new IllegalArgumentException("app.name must not be empty");
		}
		return appName;
	}
	
	public Long getAppId() {
		String appId =  this.getProperty("app.id");
		if (StringUtils.isBlank(appId)) {
			throw new IllegalArgumentException("app.id must not be empty");
		}
		
		try {
			return new Long(appId);
		} catch (Exception e) {
			throw new IllegalArgumentException("app.id must be of type long");
		}
	}
	
	public String getAppMode() {
		return this.getProperty("app.mode", ENV_POPS_APP_MODE_PRO);
	}
//	public boolean isDevMode() {
//		return ENV_POPS_APP_MODE_DEV.equalsIgnoreCase(this.getAppMode());
//	}
	public boolean isProMode() {
		String mode = this.getAppMode();
		return StringUtils.isBlank(mode) || ENV_POPS_APP_MODE_PRO.equalsIgnoreCase(mode);
	}
	
	public String getEncoding() {
		return this.getProperty("app.encoding", "UTF-8");
	}
	
	public long nextId() {
		return idWorker.nextId();
	}
	
	// =============================
	// START: Map interfaces
	// =============================

	@Override
	public int size() {
		return this.getEnvProperties().size();
	}
	@Override
	public boolean isEmpty() {
		return this.getEnvProperties().isEmpty();
	}
	@Override
	public boolean containsKey(Object key) {
		return this.getProperty((String) key) != null;
	}
	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}
	@Override
	public String get(Object key) {
		return this.getProperty((String) key);
	}
	@Override
	public String put(String key, Object value) {
		throw new UnsupportedOperationException();
	}
	@Override
	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	@Override
	public Set<String> keySet() {
		return this.getEnvProperties().keySet();
	}
	@Override
	public Collection<Object> values() {
		return this.getEnvProperties().values();
	}
	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return this.getEnvProperties().entrySet();
	}
	// =============================
	// END: Map interfaces
	// =============================
	
	////////////////////////////////////////////////////
	/////// 实现有序 properties 读取
	/////// @see org.springframework.core.io.support.ResourcePropertySource
	/////// @see java.util.Properties
	////////////////////////////////////////////////////
	public static class ResourcePropertySource extends MapPropertySource {

		public ResourcePropertySource(String name, Resource resource) throws IOException {
			super(name, loadPropertiesForResource(resource));
		}
		
		public ResourcePropertySource(Resource resource) throws IOException {
			this(getNameForResource(resource), resource);
		}
		private static String getNameForResource(Resource resource) {
			String name = resource.getDescription();
			if (StringUtils.isBlank(name)) {
				name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
			}
			return name;
		}

		private static Map<String, Object> loadPropertiesForResource(Resource resource) throws IOException {
			Map<String, Object> props = new LinkedHashMap<String, Object>();
			InputStream is = resource.getInputStream();
			load0(props, new LineReader(is));
			try {
				is.close();
			} catch (IOException ex) {
				// ignore
			}
			return props;
		}
		
		private static void load0 (Map<String, Object> props, LineReader lr) throws IOException {
			char[] convtBuf = new char[1024];
			int limit;
			int keyLen;
			int valueStart;
			char c;
			boolean hasSep;
			boolean precedingBackslash;

			while ((limit = lr.readLine()) >= 0) {
				c = 0;
				keyLen = 0;
				valueStart = limit;
				hasSep = false;

				//System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
				precedingBackslash = false;
				while (keyLen < limit) {
					c = lr.lineBuf[keyLen];
					//need check if escaped.
					if ((c == '=' ||  c == ':') && !precedingBackslash) {
						valueStart = keyLen + 1;
						hasSep = true;
						break;
					} else if ((c == ' ' || c == '\t' ||  c == '\f') && !precedingBackslash) {
						valueStart = keyLen + 1;
						break;
					}
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
					keyLen++;
				}
				while (valueStart < limit) {
					c = lr.lineBuf[valueStart];
					if (c != ' ' && c != '\t' &&  c != '\f') {
						if (!hasSep && (c == '=' ||  c == ':')) {
							hasSep = true;
						} else {
							break;
						}
					}
					valueStart++;
				}
				String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
				String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
				props.put(key, value);
			}
		}
		
		private static String loadConvert (char[] in, int off, int len, char[] convtBuf) {
			if (convtBuf.length < len) {
				int newLen = len * 2;
				if (newLen < 0) {
					newLen = Integer.MAX_VALUE;
				}
				convtBuf = new char[newLen];
			}
			char aChar;
			char[] out = convtBuf;
			int outLen = 0;
			int end = off + len;

			while (off < end) {
				aChar = in[off++];
				if (aChar == '\\') {
					aChar = in[off++];
					if(aChar == 'u') {
						// Read the xxxx
						int value=0;
						for (int i=0; i<4; i++) {
							aChar = in[off++];
							switch (aChar) {
							  case '0': case '1': case '2': case '3': case '4':
							  case '5': case '6': case '7': case '8': case '9':
								 value = (value << 4) + aChar - '0';
								 break;
							  case 'a': case 'b': case 'c':
							  case 'd': case 'e': case 'f':
								 value = (value << 4) + 10 + aChar - 'a';
								 break;
							  case 'A': case 'B': case 'C':
							  case 'D': case 'E': case 'F':
								 value = (value << 4) + 10 + aChar - 'A';
								 break;
							  default:
								  throw new IllegalArgumentException(
											   "Malformed \\uxxxx encoding.");
							}
						 }
						out[outLen++] = (char)value;
					} else {
						if (aChar == 't') aChar = '\t';
						else if (aChar == 'r') aChar = '\r';
						else if (aChar == 'n') aChar = '\n';
						else if (aChar == 'f') aChar = '\f';
						out[outLen++] = aChar;
					}
				} else {
					out[outLen++] = aChar;
				}
			}
			return new String (out, 0, outLen);
		}
		
		static class LineReader {
			public LineReader(InputStream inStream) {
				this.inStream = inStream;
				inByteBuf = new byte[8192];
			}

			public LineReader(Reader reader) {
				this.reader = reader;
				inCharBuf = new char[8192];
			}

			byte[] inByteBuf;
			char[] inCharBuf;
			char[] lineBuf = new char[1024];
			int inLimit = 0;
			int inOff = 0;
			InputStream inStream;
			Reader reader;

			int readLine() throws IOException {
				int len = 0;
				char c = 0;

				boolean skipWhiteSpace = true;
				boolean isCommentLine = false;
				boolean isNewLine = true;
				boolean appendedLineBegin = false;
				boolean precedingBackslash = false;
				boolean skipLF = false;

				while (true) {
					if (inOff >= inLimit) {
						inLimit = (inStream==null)?reader.read(inCharBuf)
												  :inStream.read(inByteBuf);
						inOff = 0;
						if (inLimit <= 0) {
							if (len == 0 || isCommentLine) {
								return -1;
							}
							return len;
						}
					}
					if (inStream != null) {
						//The line below is equivalent to calling a
						//ISO8859-1 decoder.
						c = (char) (0xff & inByteBuf[inOff++]);
					} else {
						c = inCharBuf[inOff++];
					}
					if (skipLF) {
						skipLF = false;
						if (c == '\n') {
							continue;
						}
					}
					if (skipWhiteSpace) {
						if (c == ' ' || c == '\t' || c == '\f') {
							continue;
						}
						if (!appendedLineBegin && (c == '\r' || c == '\n')) {
							continue;
						}
						skipWhiteSpace = false;
						appendedLineBegin = false;
					}
					if (isNewLine) {
						isNewLine = false;
						if (c == '#' || c == '!') {
							isCommentLine = true;
							continue;
						}
					}

					if (c != '\n' && c != '\r') {
						lineBuf[len++] = c;
						if (len == lineBuf.length) {
							int newLength = lineBuf.length * 2;
							if (newLength < 0) {
								newLength = Integer.MAX_VALUE;
							}
							char[] buf = new char[newLength];
							System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
							lineBuf = buf;
						}
						//flip the preceding backslash flag
						if (c == '\\') {
							precedingBackslash = !precedingBackslash;
						} else {
							precedingBackslash = false;
						}
					}
					else {
						// reached EOL
						if (isCommentLine || len == 0) {
							isCommentLine = false;
							isNewLine = true;
							skipWhiteSpace = true;
							len = 0;
							continue;
						}
						if (inOff >= inLimit) {
							inLimit = (inStream==null)
									  ?reader.read(inCharBuf)
									  :inStream.read(inByteBuf);
							inOff = 0;
							if (inLimit <= 0) {
								return len;
							}
						}
						if (precedingBackslash) {
							len -= 1;
							//skip the leading whitespace characters in following line
							skipWhiteSpace = true;
							appendedLineBegin = true;
							precedingBackslash = false;
							if (c == '\r') {
								skipLF = true;
							}
						} else {
							return len;
						}
					}
				}
			}
		}
	}
}