package com.hannah.http.util;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

/**
 * @author longrm
 * @date 2013-3-28
 */
public class HttpClientFactory {

	private static final int TIMEOUT = 3000;// 连接超时时间
	private static final int SO_TIMEOUT = 10000;// 数据传输超时
	private static String UA = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1"
			+ " (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";
	
	private HttpClientFactory() {
	}
	
	public static DefaultHttpClient getHttpClient() {
		return getHttpClient(TIMEOUT, SO_TIMEOUT);
	}

	public static DefaultHttpClient getHttpClient(int timeout, int soTimeout) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
		cm.setMaxTotal(500);
		cm.setDefaultMaxPerRoute(200);

		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);
		params.setParameter(CoreProtocolPNames.USER_AGENT, UA);

		DefaultHttpClient client = new DefaultHttpClient(cm, params);
		return client;
	}

}