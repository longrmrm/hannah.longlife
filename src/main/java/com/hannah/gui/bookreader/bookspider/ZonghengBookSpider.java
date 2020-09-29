package com.hannah.gui.bookreader.bookspider;

import com.hannah.gui.bookreader.Chapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZonghengBookSpider extends AbstractBookSpider {

	@Override
	public String getSite() {
		return "www.zongheng.com";
	}

	@Override
	public String getBookUrl() {
		return "http://book.zongheng.com/book/@bookId.html";
	}

	@Override
	public String getIndexUrl() {
		return "http://book.zongheng.com/showchapter/@bookId.html";
	}

	@Override
	public String getChapterUrl() {
		return "http://book.zongheng.com/chapter/@bookId/@chapterId.html";
	}

	@Override
	public String getVipChapterUrl() {
//		return "http://book.zongheng.com/vip/book/@bookId/@chapterId.html";
		return getChapterUrl();
	}

	@Override
	public List<Chapter> searchChapters(String bookId, Document doc) throws IOException {
		List<Chapter> chapters = new ArrayList<Chapter>();
		Element element = doc.select("div.chapter").first();
		Elements tds = element.select("div.booklist").select("td");
		for (Element td : tds) {
			Element link = td.select("a[href]").first();
			Chapter chapter = createChapter(bookId, link);
			if (chapter != null) {
				chapter.setVip(td.html().indexOf("vip") != -1);
				chapters.add(chapter);
			}
		}
		return chapters;
	}

	@Override
	public String parseTitle(Document doc) {
		Element title = doc.select("div.title").first();
		return title.select("h1").first().text();
	}

	@Override
	public String parseAuthor(Document doc) {
		Element title = doc.select("div.title").first();
		return title.select("span.author").first().text();
	}

	@Override
	public String parseUpdateTime(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readChapterContent(Chapter chapter, String charsetName) throws IOException {
		String chapterUrl = getChapterUrl(chapter);
		Document doc = Jsoup.connect(chapterUrl).get();
		Elements contents = doc.select("#chapterContent");
		if (contents.size() > 0) {
			String content = contents.get(0).html();
			content = content.replaceAll("<span.*/span>", "").replaceAll("<p>", "\n    ").replace("</p>", "");
			return content + "\n\n\n----------------------------------------------\n" + chapterUrl;
		}
		return doc.text() + "\n\n\n----------------------------------------------\n" + chapterUrl;
	}

}
