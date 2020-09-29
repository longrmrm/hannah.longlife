package com.hannah.http.util;

import com.hannah.common.util.GzipUtil;
import com.hannah.common.util.StreamUtil;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author longrm
 * @date 2012-3-31
 */
public class HttpRequestUtil {

	/**
	 * 创建一个通用的SSL认证Client
	 * @param client
	 * @return
	 */
	public static DefaultHttpClient createSslClient(DefaultHttpClient client) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
			return new DefaultHttpClient(ccm, client.getParams());

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static HttpResponse sendGetRequest(HttpClient client, String httpUrl, Map<String, String> headers)
			throws IOException {
		return sendGetRequest(client, httpUrl, headers, null, null);
	}

	public static HttpResponse sendGetRequest(HttpClient client, String httpUrl, Map<String, String> headers, Map<String, String> params)
			throws IOException {
		return sendGetRequest(client, httpUrl, headers, params, null);
	}

	public static HttpResponse sendGetRequest(HttpClient client, String httpUrl, Map<String, String> headers,
			Map<String, String> params, String charset) throws IOException {
		if (charset == null)
			charset = "UTF-8";
		return sendGetRequest(client, null, httpUrl, headers, params, charset);
	}

	/**
	 * @param client
	 * @param context
	 * @param httpUrl
	 * @param headers
	 * @param params
	 * @param charset
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse sendGetRequest(HttpClient client, HttpContext context, String httpUrl,
			Map<String, String> headers, Map<String, String> params, String charset) throws IOException {
		// 如果有参数的就拼装起来
		httpUrl = httpUrl + (null == params ? "" : assembleParameter(params, charset));
		// 这是实例化一个get请求
		HttpGet hp = new HttpGet(httpUrl);
		// 如果需要头部就组装起来
		if (null != headers)
			hp.setHeaders(assembleHeader(headers));
		// 执行请求后返回一个HttpResponse
		HttpResponse response = client.execute(hp, context);
		return response;
	}

	public static HttpResponse sendGetRequest(HttpClient client, String httpUrl) throws IOException {
		return sendGetRequest(client, httpUrl, null, null, null);
	}

	public static HttpResponse sendPostRequest(HttpClient client, String httpUrl, Map<String, String> headers)
			throws IOException {
		return sendPostRequest(client, httpUrl, headers, null, null);
	}

	public static HttpResponse sendPostRequest(HttpClient client, String httpUrl, Map<String, String> headers, Map<String, String> params)
			throws IOException {
		return sendPostRequest(client, httpUrl, headers, params, null);
	}

	public static HttpResponse sendPostRequest(HttpClient client, String httpUrl, Map<String, String> headers,
			Map<String, String> params, String charset) throws IOException {
		if (charset == null)
			charset = "UTF-8";
		return sendPostRequest(client, null, httpUrl, headers, params, charset);
	}

	/**
	 * @param client
	 * @param context
	 * @param httpUrl
	 * @param headers
	 * @param params
	 * @param chasert
	 * @return
	 * @throws IOException
	 */
	public static HttpResponse sendPostRequest(HttpClient client, HttpContext context, String httpUrl,
			Map<String, String> headers, Map<String, String> params, String chasert) throws IOException {
		// 实例化一个post请求
		HttpPost post = new HttpPost(httpUrl);

		// 设置需要提交的参数
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (params != null) {
			for (String key : params.keySet())
				list.add(new BasicNameValuePair(key, params.get(key)));
		}
		post.setEntity(new UrlEncodedFormEntity(list, chasert));

		// 设置头部
		if (null != headers)
			post.setHeaders(assembleHeader(headers));

		// 实行请求并返回
		HttpResponse response = client.execute(post, context);
		return response;
	}

	/**
	 * 参数不做url编码处理
	 * @param client
	 * @param context
	 * @param httpUrl
	 * @param headers
	 * @param params
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static HttpResponse sendPostRequestNoUrlEncode(HttpClient client, HttpContext context, String httpUrl,
			Map<String, String> headers, Map<String, String> params, String charset) throws IOException {
		// 实例化一个post请求
		HttpPost post = new HttpPost(httpUrl);

		// 设置需要提交的参数
		String para = "";
		for (String key : params.keySet()) {
			para += key + "=" + params.get(key) + "&";
		}
		String paramsUri = para.substring(0, para.length() - 1);
		post.setEntity(new StringEntity(paramsUri, ContentType.create("application/x-www-form-urlencoded", charset)));
		post.setHeaders(HttpRequestUtil.assembleHeader(headers));

		// 设置头部
		if (null != headers)
			post.setHeaders(assembleHeader(headers));

		// 实行请求并返回
		HttpResponse response = client.execute(post, context);
		return response;
	}

	// 组装头部
	public static Header[] assembleHeader(Map<String, String> headers) {
		Header[] allHeader = new BasicHeader[headers.size()];
		int i = 0;
		for (String str : headers.keySet())
			allHeader[i++] = new BasicHeader(str, headers.get(str));
		return allHeader;
	}

	// 组装cookie
	public static String assembleCookie(List<Cookie> cookies) {
		StringBuffer sbu = new StringBuffer();
		for (Cookie cookie : cookies) {
			sbu.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
		}
		if (sbu.length() > 0)
			sbu.deleteCharAt(sbu.length() - 1);
		return sbu.toString();
	}

	// 组装参数
	public static String assembleParameter(Map<String, String> parameters, String charset)
			throws UnsupportedEncodingException {
		String para = "?";
		for (String key : parameters.keySet()) {
			para += key + "=" + URLEncoder.encode(parameters.get(key), charset) + "&";
		}
		String paramsUri = para.substring(0, para.length() - 1);
		return paramsUri;
	}

	/**
	 * get sumbit params from html form
	 * @param form
	 * @return
	 */
	public static Map<String, String> getParams(Element form) {
		Map<String, String> params = new HashMap<String, String>();
		// 获取<input>参数
		Elements inputs = form.select("input");
		for (int i = 0; i < inputs.size(); i++) {
			Element input = inputs.get(i);
			String name = input.attr("name");
			if (name != null && name.length() > 0)
				params.put(name, input.attr("value"));
		}
		// 获取<select>参数
		Elements selects = form.select("select");
		for (int i = 0; i < selects.size(); i++) {
			Element select = selects.get(i);
			Element selectedOpt = select.select("option[selected=selected]").first();
			String name = select.attr("name");
			if (name != null && name.length() > 0)
				params.put(name, selectedOpt == null ? "" : selectedOpt.attr("value"));
		}
		return params;
	}

	/**
	 * get content text from response, auto judge charset from headers
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static String getContentText(HttpResponse response) throws IOException {
		String charset = null;
		Header contentType = response.getEntity().getContentType();
		if (contentType != null) {
			HeaderElement values[] = contentType.getElements();
			for (HeaderElement value : values) {
				NameValuePair param = value.getParameterByName("charset");
				if (param != null)
					charset = param.getValue();
			}
		}
		return getContentText(response, charset);
	}

	public static String getContentText(HttpResponse response, String charset) throws IOException {
		String contentText = null;
		// 获取Content-Encoding
		String contentEncoding = null;
		Header[] headers = response.getHeaders("Content-Encoding");
		if (headers.length > 0) {
			contentEncoding = headers[0].getValue();
		}
		// 获取编码格式
		HttpEntity entity = response.getEntity();
		Header contentType = entity.getContentType();
		if (contentType != null) {
			HeaderElement values[] = contentType.getElements();
			for (HeaderElement value : values) {
				// 判断是否下载链接：text/html、text/plain 、text/xml、text/json、application/x-gzip、application/json
				if (!value.getName().startsWith("text") && !value.getName().startsWith("application"))
					return null;
			}
		}
		// gzip压缩数据
		if ("gzip".equals(contentEncoding))
			contentText = GzipUtil.unZip(entity.getContent(), (charset == null) ? "UTF-8" : charset);
		else if (charset != null)
			contentText = EntityUtils.toString(entity, charset);
		else {
			System.out.println("Warning: charset is null, use 'UTF-8' as default!");
			byte[] bytes = StreamUtil.getBytes(entity.getContent());
			charset = HttpParseUtil.getCharsetFromHtml(new String(bytes));
			contentText = new String(bytes, (charset == null) ? "UTF-8" : charset);
		}
		// 释放httpEntity
		EntityUtils.consume(entity);
		return contentText;
	}

	/**
	 * 复制cookie：从client复制到newClient
	 * @param newClient
	 * @param client
	 */
	public static void copyCookies(AbstractHttpClient newClient, AbstractHttpClient client) {
		List<Cookie> cookies = client.getCookieStore().getCookies();
		for (Cookie cookie : cookies) {
			newClient.getCookieStore().addCookie(cookie);
		}
	}

	/**
	 * 根据名字获取cookie
	 * @param client
	 * @param cookieName
	 * @return
	 */
	public static Cookie getCookie(AbstractHttpClient client, String cookieName) {
		List<Cookie> cookies = client.getCookieStore().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName))
				return cookie;
		}
		return null;
	}

}
