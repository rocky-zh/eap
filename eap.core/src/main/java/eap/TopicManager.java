package eap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class TopicManager {
	
	private Map<String, List<TopicListener>> listeneresMap = new ConcurrentHashMap<String, List<TopicListener>>();
	
	private ExecutorService executor = null;
	
	public TopicManager(boolean async) {
		if (async) {
			executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		}
	}
	
	public TopicManager() {
		this(false);
	}
	
	public void publish(String topic, Object data) {
		List<TopicListener> listeners = getListeners(topic);
		for (TopicListener listener : listeners) {
			dispatch(listener, topic, data);
		}
	}
	
	public void subscribe(String topic, TopicListener listener) {
		getListeners(topic).add(listener);
	}
	public void unsubscribe(String topic) {
		getListeners(topic).clear();
	}
	public void unsubscribe(String topic, TopicListener listener) {
		List<TopicListener> listeners = new ArrayList<TopicListener>(getListeners(topic));
		boolean changed = false;
		for (int i = 0; i < listeners.size(); i++) {
//			if (listeners.get(i).hashCode() == listener.hashCode()) {
			if (listeners.get(i) == listener) {
				listeners.remove(i);
				changed = true;
				break;
			}
		}
		if (changed) {
			listeneresMap.put(topic, listeners);
		}
	}
	
	private List<TopicListener> getListeners(String topic) {
		List<TopicListener> listeners = listeneresMap.get(topic);
		if (listeners == null) {
			listeners = new CopyOnWriteArrayList<TopicListener>();
			listeneresMap.put(topic, listeners);
		} 
		
		return listeners;
	}
	
	public void setListeneresMap(Map<String, List<TopicListener>> listeneresMap) {
		this.listeneresMap = listeneresMap;
	}
	
	private void dispatch(final TopicListener listener, final String topic, final Object data) {
		if (executor != null) {
			executor.execute(
				new Runnable() {
					@Override
					public void run() {
						listener.onPublish(topic, data);
					}
				}
			);
		} else {
			listener.onPublish(topic, data);
		}
	}
	
	public void shutdown() {
		if (executor != null && !executor.isShutdown()) {
			executor.shutdownNow();
		}
	}

	public static void main(String[] args) {
		TopicListener l1 = new L1();
		
		final TopicManager tm = new TopicManager();
		
		tm.subscribe("a", l1);
		
		for (int j = 0; j < 100; j++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10000; i++) {
//						if (i == 999) {
							tm.publish("a", i);
//						}
					}
				}
			}).start();
		}
		
//		TopicManager.unsubscribe("a", l1);
		tm.unsubscribe("a");
		
//		TopicManager.publish("a", 1);
		
	}
	
	static class L1 implements TopicListener {
		@Override
		public void onPublish(String topic, Object data) {
			System.out.println(data);
		}
	}
}