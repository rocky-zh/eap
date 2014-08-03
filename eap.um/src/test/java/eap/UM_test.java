package eap;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.Watcher.Event.KeeperState;


public class UM_test {
	public static void main(String[] args) throws Exception {
		System.setProperty("app.name", "com.enci.open");
		System.setProperty("app.id", "1");
		System.setProperty("app.version", "1.0.0");
		System.setProperty("app.UMServer", "node1:2181,node1:2182,node1:2183");
		
		CuratorFramework client = null;
		String appUMServer = System.getProperty("app.UMServer"); // TODO , "localhost:2181"
		if (appUMServer != null && appUMServer.length() > 0) {
			Integer retryNum = Integer.getInteger("app.UMServer.retryNum", Integer.MAX_VALUE);
			Integer retryTimes = Integer.getInteger("app.UMServer.retryTimes", 3000);
			Integer connectionTimeoutMs = Integer.getInteger("app.UMServer.connectionTimeoutMs", 10000);
			
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
				}
			});
			client.start();
			connectedLatch.await();
		}
		
//		QueueBuilder.builder(client, consumer, serializer, "/queue");
//		DistributedQueue<String> cli = 
		
//		Map<String, String> env = new HashMap<String, String>();
//		env.put("a", "123");
		
		client.setData().forPath("/UM_CONFIG/APP/com.enci.open/1.0.0/cli", "invoke com.enci.open.management.TransDetailsManager refresh".getBytes());
		
	}
}
