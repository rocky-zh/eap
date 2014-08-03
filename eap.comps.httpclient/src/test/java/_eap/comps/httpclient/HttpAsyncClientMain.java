package _eap.comps.httpclient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;

public class HttpAsyncClientMain {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
//		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
//        try {
//            httpclient.start();
//            HttpGet request = new HttpGet("http://www.apache.org/");
//            Future<HttpResponse> future = httpclient.execute(request, null);
//            HttpResponse response = future.get();
//            System.out.println("Response: " + response.getStatusLine());
//            System.out.println("Shutting down");
//        } finally {
//            httpclient.close();
//        }
//        System.out.println("Done");
		
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		for (int i = 0; i < 10000; i++) {
			// Future<Response> f =
			 asyncHttpClient.prepareGet("http://www.ning.com/").execute(new AsyncHandler<String>() {
				@Override
				public com.ning.http.client.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
					System.out.println(Thread.currentThread().getName() + "   " + bodyPart.getBodyByteBuffer());
					return null;
				}

				@Override
				public void onThrowable(Throwable t) {
					System.out.println(Thread.currentThread().getName() + "  onThrowable ");
				}

				@Override
				public com.ning.http.client.AsyncHandler.STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
					System.out.println(Thread.currentThread().getName() + "  onStatusReceived ");
					return null;
				}

				@Override
				public com.ning.http.client.AsyncHandler.STATE onHeadersReceived(
						HttpResponseHeaders headers) throws Exception {
					System.out.println(Thread.currentThread().getName() + "  onHeadersReceived ");
					return null;
				}

				@Override
				public String onCompleted() throws Exception {
					System.out.println(Thread.currentThread().getName() + "  onCompleted ");
					return null;
				}
			});
		}
//		Response r = f.get();
//		System.out.println(r.getResponseBody());
		System.out.println("123");
//		asyncHttpClient.closeAsynchronously();
		
	}
}
