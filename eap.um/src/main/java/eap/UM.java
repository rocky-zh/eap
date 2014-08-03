package eap;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Title: </p>
 * <p> Description: 
 * <pre>
 *   app.name: 应用名称
 *   app.id: 应用ID
 *   app.version: 应用版本
 *   app.server.ip=应用服务器IP
 *   app.server.port=应用服务器端口
 *   app.password=应用密码
 *   app.UMServer=ZooKeeper服务器连接地址
 *   app.UMServer.retryNum=与ZooKeeper重新连接次数
 *   app.UMServer.retryTimes=与ZooKeeper重新连接间隔时间
 *   app.UMServer.connectionTimeoutMs=与ZooKeeper连接超时时间
 *  
 * -Dapp.name="WebDemo" -Dapp.id="1" -Dapp.version="1.0.0" -Dapp.UMServer="node1:2181,node2:2182,node3:2183"
 * SAMPLE(Zookeeper Tree): 
 *  /EAP_UM/APP/WebDemo
 *     /config
 *         /1.0.0
 *             /env
 *               DATA: {}
 *        /1.0.1
 *            /env
 *     /runtime
 *         /leader
 *           DATA: 192.168.1.10:8080
 *         /server
 *             /192.168.1.10:8080
 *               DATA: {"app.id": "1", "app.version": "1.0.1", "app.startTime": "2014-05-21 21:10:07"}
 *             /192.168.1.11:8080
 *               DATA: {"app.id": "2", "app.version": "1.0.0", "app.startTime": "2014-04-23 04:20:13"}
 *         /cli
 *            /192.168.1.10:8080
 *            /192.168.1.11:8080
 *         /locking
 * </pre>
 * </p>
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
public class UM {
	private static final Logger logger = LoggerFactory.getLogger(UM.class);
	
	private static Properties envProps = new Properties();
	static {
		InputStream envInputStream = UM.class.getResourceAsStream("/env.properties");
		if (envInputStream != null) {
			try {
				envProps.load(envInputStream);
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			} finally {
				try {
					envInputStream.close();
				} catch (Exception e) {}
			}
		}
	}
	
	public static final byte[] EMPTY_DATE = new byte[0];
	
	private static CuratorFramework client;
	private static boolean started = false;
//	private static MultiValueMap<String, NodeListener> nodeListenersMap = CollectionUtils.toMultiValueMap(new ConcurrentHashMap<String, List<NodeListener>>());
	private static Map<String, Map<NodeListener, List<Closeable>>> cache = new ConcurrentHashMap<String, Map<NodeListener, List<Closeable>>>();
	
	public static String appName;
	public static Long appId;
	public static String appVersion;
	public static String umConfigNS;
	public static String envPath;
	public static String umRuntimeNS;
	public static String loaderPath;
	public static String serverPath;
	public static String lockingPath;
	public static String cliPath;
	
	public static String serverIp;
	public static String serverPort;
	public static String serverId;
	public static String serverIdPath;
	public static String serverCliPath;
	
	private static LeaderLatch leaderLatch;
	
	public static synchronized void start() throws Exception {
		String appUMServer = System.getProperty("app.UMServer", envProps.getProperty("app.UMServer")); // TODO , "localhost:2181"
		if (appUMServer != null && appUMServer.length() > 0) {
			Integer retryNum = new Integer(System.getProperty("app.UMServer.retryNum", envProps.getProperty("app.UMServer.retryNum", Integer.MAX_VALUE + "")));
			Integer retryTimes = new Integer(System.getProperty("app.UMServer.retryTimes", envProps.getProperty("app.UMServer.retryTimes", 3000 + "" )));
			Integer connectionTimeoutMs = new Integer(System.getProperty("app.UMServer.connectionTimeoutMs", envProps.getProperty("app.UMServer.connectionTimeoutMs", 10000 + "")));
			
			final CountDownLatch connectedLatch = new CountDownLatch(1);
			
			client = CuratorFrameworkFactory.builder()
				.connectString(appUMServer)
				.retryPolicy(new RetryNTimes(retryNum, retryTimes))
				.connectionTimeoutMs(connectionTimeoutMs).build();
			client.getCuratorListenable().addListener(new CuratorListener() {
				public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
					if (connectedLatch.getCount() > 0 && event.getWatchedEvent() != null && event.getWatchedEvent().getState() == KeeperState.SyncConnected) {
						connectedLatch.countDown();
					}
				}
			});
			client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
				public void unhandledError(String message, Throwable e) {
					e.printStackTrace();
					logger.error(message, e);
				}
			});
			client.start();
			connectedLatch.await();
			
			init();
			
			started = true;
			
			addListener(serverCliPath, new CliNodeListener()); // TODO
		}
	}
	
	private static void init() throws Exception {
		appName = envProps.getProperty("app.name", System.getProperty("app.name"));
		if (appName == null || appName.length() == 0) {
			throw new IllegalArgumentException("system property 'app.name' must not be empty");
		}
		String appIdStr = System.getProperty("app.id");
		if (appIdStr == null || appIdStr.length() == 0) {
			throw new IllegalArgumentException("system property 'app.id' must not be empty");
		}
		try {
			appId = new Long(appIdStr);
		} catch (Exception e) {
			throw new IllegalArgumentException("system property 'app.id' must be of type long");
		}
		appVersion = envProps.getProperty("app.version", System.getProperty("app.version", "0"));
		serverIp = System.getProperty("app.server.ip", InetAddress.getLocalHost().getHostAddress());
		serverPort = System.getProperty("app.server.port", "80");
		serverId = serverIp + ":" + serverPort;
		
		umConfigNS = String.format("/EAP_UM/APP/%s/config/%s", appName, appVersion);
		envPath = umConfigNS + "/env";
		umRuntimeNS = String.format("/EAP_UM/APP/%s/runtime", appName);
		serverPath = umRuntimeNS + "/server";
		lockingPath = umRuntimeNS + "/locking";
		cliPath = umRuntimeNS + "/cli";
		loaderPath = umRuntimeNS + "/loader";
		
		for (String p : new String[] {umConfigNS, envPath, umRuntimeNS, serverPath, lockingPath, cliPath}) {
			if (client.checkExists().forPath(p) == null) {
				client.create()
					.creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT)
					.withACL(Ids.OPEN_ACL_UNSAFE)
					.forPath(p, EMPTY_DATE);
			}
		}
		
		serverIdPath = serverPath + "/" + serverId;
		if (client.checkExists().forPath(serverIdPath) != null) {
			client.delete().forPath(serverIdPath);
		}
		client.create()
			.creatingParentsIfNeeded()
			.withMode(CreateMode.EPHEMERAL)
			.withACL(Ids.OPEN_ACL_UNSAFE)
			.forPath(serverIdPath, String.format("{\"app.id\": %d, \"app.version\": \"%s\", \"app.startTime\": \"%s\"}", appId, appVersion, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).getBytes());
		
		serverCliPath = cliPath + "/" + serverId;
		if (client.checkExists().forPath(serverCliPath) != null) {
			client.delete().forPath(serverCliPath);
		}
		client.create()
			.creatingParentsIfNeeded()
			.withMode(CreateMode.EPHEMERAL)
			.withACL(Ids.OPEN_ACL_UNSAFE)
			.forPath(serverCliPath, "".getBytes());
		
		leaderLatch = new LeaderLatch(client, loaderPath, serverId);
		leaderLatch.start();
	}
	
	public static void addListener(final String path, final NodeListener listener) throws Exception {
		if (isStarted()) {
			if (cache.get(path) != null && cache.get(path).get(listener) != null) { // listener exists 
				return;
			}
			
			final NodeCache nodeCache = new NodeCache(client, path);
			nodeCache.getListenable().addListener(new NodeCacheListener() {
				@Override
				public void nodeChanged() throws Exception {
					listener.nodeChanged(client, nodeCache.getCurrentData());
				}
			});
			nodeCache.start();
			
			final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, false);
			pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					listener.childEvent(client, event);
				}
			});
			pathChildrenCache.start();
			
			putToCache(path, listener, nodeCache, pathChildrenCache);
		}
	}
	private static void putToCache(String path, NodeListener listener, Closeable... closeables) {
		Map<NodeListener, List<Closeable>> m = cache.get(path);
		if (m == null) {
			m = new ConcurrentHashMap<NodeListener, List<Closeable>>();
			cache.put(path, m);
		}
		m.put(listener, Arrays.asList(closeables));
	}
	
	public static void removeListener(String path, NodeListener listener) throws Exception {
		Map<NodeListener, List<Closeable>> m = cache.get(path);
		if (m != null && m.size() > 0 && m.containsKey(listener)) {
			for (Closeable closeable : m.get(listener)) {
				try {
					closeable.close();
				} catch (IOException e) {}
			}
		}
	}
	public static void removeListener(String path) throws Exception {
		Map<NodeListener, List<Closeable>> m = cache.get(path);
		if (m != null && m.size() > 0) {
			for (NodeListener listener : m.keySet()) {
				removeListener(path, listener);
			}
		}
	}
	public static void removeAllListener() throws Exception {
		for (String path : cache.keySet()) {
			removeListener(path);
		}
	}
	
	public static boolean isLeader() {
		if (isStarted() && leaderLatch != null) {
			try {
				return serverId.equals(leaderLatch.getLeader().getId());
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
				return false;
			}
		} else {
			return true;
		}
	}
	
	public static synchronized void stop() {
		if (isStarted() && client != null && client.getState() == CuratorFrameworkState.STARTED) {
			if (leaderLatch != null) {
				try {
					leaderLatch.close();
				} catch (IOException e) {}
			}
			
			client.close();
			
			started = false;
		}
	}
	
	
	public static boolean isStarted() {
		return started;
	}
	
	public static boolean isEnabled() {
		String appUMServer = System.getProperty("app.UMServer", envProps.getProperty("app.UMServer")); // TODO , "localhost:2181"
		return (appUMServer != null && appUMServer.length() > 0) ? true : false;
	}
	
	public static class NodeListener {
		public void nodeChanged(CuratorFramework client, ChildData childData) throws Exception {
		}
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
		}
	}
	
	private static class CliNodeListener extends NodeListener {
		public void nodeChanged(CuratorFramework client, ChildData childData) throws Exception {
			byte[] data = childData.getData();
			if (data != null && data.length > 0) {
				String dataStr = new String(data).trim();
				logger.info("execute cli: " + dataStr);
				
				String[] cli = dataStr.split(" ");
				String cmd = cli[0];
				String[] args = cli.length >= 2 ? Arrays.copyOfRange(cli, 1, cli.length) : null;
				
				try {
					if ("invoke".equalsIgnoreCase(cmd)) {
						invokeMethod(args);
					} else {
						throw new UnsupportedOperationException("cmd " + cmd + " unsupported");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		
		private void invokeMethod(String[] args) throws Exception {
			if (args == null || args.length < 2) {
				throw new IllegalArgumentException("cli args error. format:\"className method arg1 arg2 ...\"");
			}
			
			String className = args[0];
			String methodName = args[1];
			String[] methodArgs = args.length >= 3 ? Arrays.copyOfRange(args, 2, args.length) : null;
			
			Class clazz = Class.forName(className);
			Method method = methodArgs != null ? clazz.getMethod(methodName, String[].class) : clazz.getMethod(methodName, null);
			Object obj = null;
			if ((method.getModifiers() & Modifier.STATIC) != 0) {
				obj = clazz;
			} else {
				obj = clazz.newInstance();
			}
			
			if (methodArgs != null) {
				method.invoke(obj, methodArgs);
			} else {
				method.invoke(obj);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		UM.start();
		
		if (UM.isStarted()) {
			System.out.println("Started");
		}
//		for (int i = 1; i < 100; i++) {
//			System.out.println(UM.isLeader());
			
//			NodeListener l1 = new NodeListener() {
//				@Override
//				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
//					System.out.println("childEvent : " + new String(event.getData().getData()));
//				}
//				@Override
//				public void nodeChanged(CuratorFramework client, ChildData childData) throws Exception {
//					System.out.println("nodeChanged : " + new String(childData.getData()));
//				}
//			};
//			UM.addListener(UM.envPath, l1);
//			UM.addListener(UM.envPath, l1);
//			UM.addListener(UM.cliPath, l1);
			
//			System.out.println("is loader: " + UM.isLeader());
			
//			UM.removeListener(UM.envPath, l1);
//			
//			Thread.sleep(5000);
//			
//			UM.addListener(UM.envPath, l1);
			
//			UM.removeListener(UM.envPath);
			
			Thread.sleep(10000000);
//		}
		
		
		UM.stop();
	}
}