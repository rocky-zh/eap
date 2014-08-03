package eap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eap.util.SystemInfoUtil;

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
public class EapStartupLogger {
	
	private static final Logger logger = LoggerFactory.getLogger(EapStartupLogger.class);
	
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	public static void printStartingMessage() { // ServletContext context
		LogMsgBuilder logMsg = new LogMsgBuilder();
		logMsg.add("EAP Starting");
		
		logMsg.outputHeader("Environment");
//		if (context != null) {
//			logMsg.outputProperty("Application Server", context.getServerInfo() + " - Servlet API " + context.getMajorVersion() + "." + context.getMinorVersion());
//		}
		logMsg.outputProperty("Java Version", System.getProperty("java.version", "unknown??") + " - " + System.getProperty("java.vendor", "unknown??"));
		logMsg.outputProperty("Current Working Directory", System.getProperty("user.dir", "unknown??"));
		logMsg.outputProperty("Heap Memory", String.format("Max: %dMB, Committed: %dMB, Free: %dMB, Used: %dMB", (SystemInfoUtil.getTotalHeapMemoryMax() / 1048576), (SystemInfoUtil.getTotalHeapMemoryCommitted() / 1048576), (SystemInfoUtil.getTotalHeapMemoryFree() / 1048576), (SystemInfoUtil.getTotalHeapMemoryUsed() / 1048576)));
		for (String[] memoryPool : SystemInfoUtil.getMemoryPools()) {
			logMsg.outputProperty("Memory Pool " + memoryPool[0], memoryPool[1]);
		}
		
		logMsg.outputHeader("Java System Properties");
		for (Map.Entry<String, String> sysProp : SystemInfoUtil.getSystemProperties().entrySet()) {
			logMsg.outputProperty(sysProp.getKey(), sysProp.getValue());
		}
		
		logger.info(logMsg.toString());
	}
	
	private static class LogMsgBuilder {
		StringBuilder logMsg = new StringBuilder();
		
		public LogMsgBuilder add(String msg) {
			logMsg.append(NEW_LINE)
				.append("/////////////////////////////////////////////////////").append(NEW_LINE)
				.append("\t\t" + msg).append(NEW_LINE)
				.append("/////////////////////////////////////////////////////");
			return this;
		}
		
		public LogMsgBuilder outputHeader(String header) {
			logMsg.append(NEW_LINE).append("___  ").append(header).append("  ___").append(NEW_LINE);
			return this;
		}
		
		public LogMsgBuilder outputProperty(String name, String value) {
			logMsg.append(" ").append(name).append(" -> ").append(value).append(NEW_LINE);
			return this;
		}
		
		public String toString() {
			return logMsg.toString();
		}
	}
	
	public static void main(String[] args) {
		EapStartupLogger l = new EapStartupLogger();
		l.printStartingMessage();
	}
}
