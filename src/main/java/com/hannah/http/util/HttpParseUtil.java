package com.hannah.http.util;

import com.hannah.common.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpParseUtil {

	/**
	 * 从contentType中获取charset
	 * @param contentType Content-Type: text/html;charset=UTF-8
	 * @return
	 */
	public static String getCharsetFromContentType(String contentType) {
		if (contentType == null)
			return null;

		String[] values = contentType.split(";");
		for (String value : values) {
			int index = value.indexOf("charset=");
			if (index != -1)
				return value.substring(index + "charset=".length());
		}
		return null;
	}

	/**
	 * 从html源码中的meta元素中获取charset
	 * @param html
	 * @return
	 */
	public static String getCharsetFromHtml(String html) {
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("meta");
		for (Element meta : elements) {
			String charset = getCharsetFromContentType(meta.attr("content"));
			if (charset != null)
				return charset;
		}
		return null;
	}

	/**
	 * 获取服务器host地址
	 * @param url
	 * @return
	 */
	public static String getServerHost(String url) {
		if (!StringUtil.isHttpUrl(url))
			return null;

		int index = url.indexOf("//");
		if (index == -1)
			return null;

		String host = url.substring(0, index + 2);
		url = url.substring(index + 2);
		index = url.indexOf("/");
		if (index == -1)
			return host + url;
		else
			return host + url.substring(0, index);
	}

	public static Map<String, String> getParamMap(String url) throws UnsupportedEncodingException {
		return getParamMap(url, "UTF-8");
	}

	/**
	 * 获取参数对
	 * @param url
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> getParamMap(String url, String charset) throws UnsupportedEncodingException {
		Map<String, String> paramMap = new HashMap<String, String>();
		url = URLDecoder.decode(url, charset);
		String[] params = url.substring(url.indexOf("?") + 1).split("&");
		for (String param : params) {
			int index = param.indexOf("=");
			if (index > 0)
				paramMap.put(param.substring(0, index), param.substring(index + 1));
		}
		return paramMap;
	}

}
