package com.hannah.http;

import com.hannah.common.util.FileUtil;
import com.hannah.common.util.StringUtil;
import com.hannah.http.util.HttpClientFactory;
import com.hannah.http.util.HttpFileUtil;
import com.hannah.http.util.HttpParseUtil;
import com.hannah.http.util.HttpRequestUtil;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HttpSeeker {

	private DefaultHttpClient client;

	// 文件保存路径
	private String directory;

	private String seekUrl;
	private String seekHost;

	private List<String> links;
	private List<String> imports;
	private Map<String, List<String>> medias;

	public HttpSeeker() {
		this(HttpRequestUtil.createSslClient(HttpClientFactory.getHttpClient()));
	}

	public HttpSeeker(DefaultHttpClient client) {
		this.client = client;
		links = new ArrayList<String>();
		imports = new ArrayList<String>();
		medias = new HashMap<String, List<String>>();
	}

	public DefaultHttpClient getClient() {
		return client;
	}

	public void setClient(DefaultHttpClient client) {
		this.client = client;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public List<String> getLinks() {
		return links;
	}

	public List<String> getImports() {
		return imports;
	}

	public Map<String, List<String>> getMedias() {
		return medias;
	}

	public void clear() {
		links.clear();
		imports.clear();
		medias.clear();
	}

	public void seek(String url, int depth) throws IOException {
		seek(url, depth, null);
	}

	public void seek(String url, int depth, String regex) throws IOException {
		this.seekUrl = url;
		this.seekHost = HttpParseUtil.getServerHost(url);
		_seek(url, depth, regex == null ? StringUtil.escapeRegex(seekHost) + ".*" : regex);
	}

	private void _seek(String url, int depth, String regex) throws IOException {
		if (!StringUtil.hasText(url))
			return;

		HttpResponse response = HttpRequestUtil.sendGetRequest(client, url);
		String htmlText = HttpRequestUtil.getContentText(response);
		if (htmlText == null)
			return;

		links.add(url);
		// 静态化到本地
		if (directory != null)
			staticize(url, htmlText);
		// 解析
		Document doc = Jsoup.parse(htmlText); // Jsoup.connect(url).get();
		// 媒体（js、img...）
		Elements tmpMedia = doc.select("[src]");
		for (Element src : tmpMedia) {
			String tagName = src.tagName();
			String srcUrl = src.attr("abs:src");
			if (!StringUtil.hasText(srcUrl))
				continue;
			List<String> media = medias.get(tagName);
			if (media == null) {
				media = new ArrayList<String>();
				medias.put(tagName, media);
			}
			if (!media.contains(srcUrl))
				media.add(srcUrl);
		}
		// 引入（css）
		Elements tmpImports = doc.select("link[href]");
		for (Element link : tmpImports) {
			String hrefUrl = link.attr("abs:href");
			if (!StringUtil.hasText(hrefUrl))
				continue;
			if (!imports.contains(hrefUrl))
				imports.add(hrefUrl);
		}
		// 深度
		if (depth <= 0)
			return;
		// 网页
		Elements tmpLinks = doc.select("a[href]");
		for (Element link : tmpLinks) {
			if (link.attr("href").startsWith("#"))
				continue;
			String hrefUrl = link.attr("abs:href");
			if (!StringUtil.hasText(hrefUrl))
				hrefUrl = link.attr("href");
			if (hrefUrl.endsWith("/"))
				hrefUrl = hrefUrl.substring(0, hrefUrl.length() - 1);
			// 匹配
			if (!links.contains(hrefUrl) && hrefUrl.matches(regex))
				_seek(hrefUrl, depth - 1, regex);
		}
	}

	public void staticize(String url, String htmlText) throws IOException {
		String filepath = getDownloadFilepath(url);
		if (filepath.endsWith(File.separator))
			filepath += "index.html";
		File file = FileUtil.createNewFile(filepath);
		FileUtil.writeText(file, htmlText);
	}

	private String getDownloadFilepath(String url) {
		String host = HttpParseUtil.getServerHost(url);
		String filepath = directory;
		if (!host.equals(seekHost)) {
			int index = host.lastIndexOf("/");
			filepath = directory + File.separator + host.substring(index + 1);
		}
		if (url.equals(host))
			filepath += File.separator;
		else {
			String suffixUrl = url.substring(host.length() + 1);
			filepath += File.separator + suffixUrl;
		}
		return filepath;
	}

	public void downloadAll() throws IOException {
		downloadAll(null);
	}

	public void downloadAll(String regex) throws IOException {
		download(imports, regex);
		Iterator<List<String>> it = medias.values().iterator();
		while (it.hasNext())
			download(it.next(), regex);
	}

	public void download(List<String> urls, String regex) throws IOException {
		if (regex == null)
			regex = StringUtil.escapeRegex(seekHost) + ".*";
		for (String url : urls) {
			if (url.matches(regex))
				download(url);
		}
	}

	public void download(String url) throws IOException {
		String filepath = getDownloadFilepath(url);
		if (filepath.endsWith(File.separator))
			return;
		HttpFileUtil.downloadFileByHttpGet(filepath, url);
	}

}
