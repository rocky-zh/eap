package eap.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

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
public class SystemInfoUtil {
	
	private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	private static RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	
	private static List<String> PATH_RELATED_KEYS = new ArrayList<String>();
	private static List<String> IGNORE_THESE_KEYS = new ArrayList<String>();
	static {
		PATH_RELATED_KEYS.add("sun.boot.class.path");
		PATH_RELATED_KEYS.add("com.ibm.oti.vm.bootstrap.library.path");
		PATH_RELATED_KEYS.add("java.library.path");
		PATH_RELATED_KEYS.add("java.endorsed.dirs");
		PATH_RELATED_KEYS.add("java.ext.dirs");
		PATH_RELATED_KEYS.add("java.class.path");
		
		IGNORE_THESE_KEYS.addAll(PATH_RELATED_KEYS);
		IGNORE_THESE_KEYS.addAll(PATH_RELATED_KEYS);
		IGNORE_THESE_KEYS.add("line.separator");
		IGNORE_THESE_KEYS.add("path.separator");
		IGNORE_THESE_KEYS.add("file.separator");
	}
	
	
	public static float getJvmVersion()  {
		String property = System.getProperty("java.specification.version");
		try {
			return Float.valueOf(property).floatValue();
		} catch (Exception e) {
			throw new IllegalStateException("Invalid JVM version: '" + property + "'. " + e.getMessage());
		}
	}

	public static long getTotalHeapMemoryInit() {
		return memoryMXBean.getHeapMemoryUsage().getInit();
	}
	public static long getTotalHeapMemoryMax() {
		return memoryMXBean.getHeapMemoryUsage().getMax();
	}
	public static long getTotalHeapMemoryCommitted() {
		return memoryMXBean.getHeapMemoryUsage().getCommitted();
	}
	public static long getTotalHeapMemoryUsed() {
		return memoryMXBean.getHeapMemoryUsage().getUsed();
	}
	public static long getTotalHeapMemoryFree() {
		return getTotalHeapMemoryCommitted() - getTotalHeapMemoryUsed();
	}
	
	public static List<String[]> getMemoryPools() {
		List<String[]> pools = new ArrayList<String[]>();
		List<MemoryPoolMXBean> mxBeans = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean mxBean : mxBeans) {
			pools.add(new String[] {mxBean.getName(), mxBean.getUsage().toString()});
		}
		return pools;
	}
	
	public static Map<String, String> getSystemProperties() {
		Properties props = System.getProperties();
		Map<String, String> properties = new TreeMap(props);
		properties.keySet().removeAll(IGNORE_THESE_KEYS);
		
		for (String key : properties.keySet()) {
			if (key.indexOf("password") != -1) {
				properties.put(key, "********");
			}
		}
		
		return properties;
	}
	
	public static String getLocalAddress() {
		try {
			// 遍历网卡，查找一个非回路ip地址并返回
			Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
			ArrayList<String> ipv4Result = new ArrayList<String>();
			ArrayList<String> ipv6Result = new ArrayList<String>();
			while (enumeration.hasMoreElements()) {
				final NetworkInterface networkInterface = enumeration.nextElement();
				final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
				while (en.hasMoreElements()) {
					final InetAddress address = en.nextElement();
					if (!address.isLoopbackAddress()) {
						if (address instanceof Inet6Address) {
							ipv6Result.add(normalizeHostAddress(address));
						}
						else {
							ipv4Result.add(normalizeHostAddress(address));
						}
					}
				}
			}

			// 优先使用ipv4
			if (!ipv4Result.isEmpty()) {
				for (String ip : ipv4Result) {
					if (ip.startsWith("127.0") || ip.startsWith("192.168")) {
						continue;
					}

					return ip;
				}
				
				// 取最后一个
				return ipv4Result.get(ipv4Result.size() - 1);
			}
			// 然后使用ipv6
			else if (!ipv6Result.isEmpty()) {
				return ipv6Result.get(0);
			}
			// 然后使用本地ip
			final InetAddress localHost = InetAddress.getLocalHost();
			return normalizeHostAddress(localHost);
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static String normalizeHostAddress(final InetAddress localHost) {
		if (localHost instanceof Inet6Address) {
			return "[" + localHost.getHostAddress() + "]";
		}
		else {
			return localHost.getHostAddress();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(getSystemProperties());
	}
}