package com.hannah.gui.bookreader.bookspider;

import com.hannah.gui.bookreader.Chapter;
import com.hannah.http.util.HttpRequestUtil;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;

public class QidianBookSpider extends AbstractBookSpider {

	@Override
	public String getSite() {
		return "www.qidian.com";
	}

	@Override
	public String getBookUrl() {
		return "http://www.qidian.com/Book/@bookId.aspx";
	}

	@Override
	public String getIndexUrl() {
		return "http://read.qidian.com/BookReader/@bookId.aspx";
	}

	@Override
	public String getChapterUrl() {
		return "http://read.qidian.com/BookReader/@bookId,@chapterId.aspx";
	}

	@Override
	public String getVipChapterUrl() {
		return "http://vipreader.qidian.com/BookReader/vip,@bookId,@chapterId.aspx";
	}

	@Override
	public String parseTitle(Document doc) {
		Element title = doc.select("div.booktitle").first();
		Element h1 = title.select("h1").first();
		Node node = h1.childNode(0);
		// 取文本
		String text = null;
		if (node instanceof TextNode)
			text = ((TextNode) node).text();
		else if (node instanceof Element)
			text = ((Element) node).text();
		else
			text = node.toString();
		return text;
	}

	@Override
	public String parseAuthor(Document doc) {
		Element title = doc.select("div.booktitle").first();
		return title.select("a[href]").first().text();
	}

	@Override
	public String parseUpdateTime(Document doc) {
		Element title = doc.select("div.booktitle").first();
		return title.select("i").first().text();
	}

	@Override
	public String readChapterContent(Chapter chapter, String charsetName) throws IOException {
		String chapterUrl = getChapterUrl(chapter);
		Document doc = Jsoup.connect(chapterUrl).get();
		Elements scripts = doc.select("script");
		String contentUrl = null;
		for (Element script : scripts) {
			if (script.attr("abs:src").matches("http://files.qidian.com/Author\\d+/\\d*/\\d*.txt")) {
				contentUrl = script.attr("src");
				if (charsetName == null)
					charsetName = script.attr("charset");
				break;
			}
		}

		// gzip流，使用httpclient处理
//		doc = Jsoup.parse(new URL(contentUrl).openStream(), charsetName, contentUrl);
		HttpResponse response = HttpRequestUtil.sendGetRequest(new DefaultHttpClient(), contentUrl);
		String contentText = HttpRequestUtil.getContentText(response, charsetName);
		doc = Jsoup.parse(contentText);
		Elements contents = doc.select("body");
		if (contents.size() > 0) {
			String content = contents.get(0).html();
			content = content.replace("document.write('", "").replaceAll("<p> *", "\n    ").replace("</p>", "");
			return "    " + content + "\n\n\n----------------------------------------------\n" + chapterUrl;
		}
		return doc.text() + "\n\n\n----------------------------------------------\n" + chapterUrl;
	}

}
