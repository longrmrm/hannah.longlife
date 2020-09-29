package com.hannah.gui.bookreader.bookspider;

import com.hannah.http.baidu.BaiduSearchResult;
import com.hannah.http.baidu.BaiduUtil;
import com.hannah.gui.bookreader.Book;
import com.hannah.gui.bookreader.Chapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抽象BookSpider类
 * @author longrm
 * @date 2013-4-21
 */
public abstract class AbstractBookSpider {

	private String site;

	private String bookUrl;
	private String indexUrl;
	private String chapterUrl;
	private String vipChapterUrl;

	public AbstractBookSpider() {
		this.site = getSite();
		this.bookUrl = getBookUrl();
		this.indexUrl = getIndexUrl();
		this.chapterUrl = getChapterUrl();
		this.vipChapterUrl = getVipChapterUrl();
	}

	public abstract String getSite();

	public abstract String getBookUrl();

	public abstract String getIndexUrl();

	public abstract String getChapterUrl();

	public abstract String getVipChapterUrl();

	public String getBookUrl(String bookId) {
		return bookUrl.replace("@bookId", bookId);
	}

	public String getIndexUrl(String bookId) {
		return indexUrl.replace("@bookId", bookId);
	}

	public String getChapterUrl(Chapter chapter) {
		if (!chapter.isVip())
			return chapterUrl.replace("@bookId", chapter.getBookId()).replace("@chapterId", chapter.getChapterId());
		else
			return vipChapterUrl.replace("@bookId", chapter.getBookId()).replace("@chapterId", chapter.getChapterId());
	}

	/**
	 * 通过百度搜索，查找bookId
	 * @param searchWord
	 * @return
	 * @throws IOException
	 */
	public String searchBookId(String searchWord) throws IOException {
		// 只查第一页
		List<BaiduSearchResult> results = BaiduUtil.search(searchWord + " site:" + site, 1);
		for (BaiduSearchResult result : results) {
			if (result.getShortRealUrl().indexOf(site) != -1) {
				Document doc = Jsoup.connect(result.getUrl()).get();
				String realUrl = doc.baseUri();
				String bookId = getBookId(realUrl);
				if (bookId != null)
					return bookId;
			}
		}
		return null;
	}

	/**
	 * 获取bookId
	 * @param url
	 * @return
	 */
	public String getBookId(String url) {
		// 匹配bookUrl
		Pattern p = Pattern.compile(bookUrl.replace("@bookId", "(\\d*)"));
		Matcher m = p.matcher(url);
		if (m.find())
			return m.group(1);
		// 匹配indexUrl
		p = Pattern.compile(indexUrl.replace("@bookId", "(\\d*)"));
		m = p.matcher(url);
		if (m.find())
			return m.group(1);
		// 匹配chapterUrl
		p = Pattern.compile(chapterUrl.replace("@bookId", "(\\d*)").replace("@chapterId", "\\d*"));
		m = p.matcher(url);
		if (m.find())
			return m.group(1);
		// 匹配vipChapterUrl
		p = Pattern.compile(vipChapterUrl.replace("@bookId", "(\\d*)").replace("@chapterId", "\\d*"));
		m = p.matcher(url);
		if (m.find())
			return m.group(1);

		return null;
	}

	/**
	 * 获取chapterId
	 * @param url
	 * @return
	 */
	public String getChapterId(String url) {
		// 匹配chapterUrl
		Pattern p = Pattern.compile(chapterUrl.replace("@bookId", "\\d*").replace("@chapterId", "(\\d*)"));
		Matcher m = p.matcher(url);
		if (m.find())
			return m.group(1);
		// 匹配vipChapterUrl
		p = Pattern.compile(vipChapterUrl.replace("@bookId", "\\d*").replace("@chapterId", "(\\d*)"));
		m = p.matcher(url);
		if (m.find())
			return m.group(1);

		return null;
	}

	public List<Chapter> searchChapters(String bookId) throws IOException {
		String url = indexUrl.replace("@bookId", bookId);
		Document doc = Jsoup.connect(url).get();
		return searchChapters(bookId, doc);
	}

	/**
	 * 查找章节列表
	 * @param bookId
	 * @param doc
	 * @return
	 * @throws IOException
	 */
	public List<Chapter> searchChapters(String bookId, Document doc) throws IOException {
		List<Chapter> chapters = new ArrayList<Chapter>();
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			Chapter chapter = createChapter(bookId, link);
			if (chapter != null)
				chapters.add(chapter);
		}
		return chapters;
	}

	/**
	 * 从链接元素中创建一个章节对象
	 * @param bookId
	 * @param link
	 * @return
	 */
	public Chapter createChapter(String bookId, Element link) {
		String chapterUrl = link.attr("abs:href");
		// 大众章节 || Vip章节
		String chapterRegex = chapterUrl.replace("@bookId", bookId).replace("@chapterId", "\\d*");
		String vipChapterRegex = vipChapterUrl.replace("@bookId", bookId).replace("@chapterId", "\\d*");
		if (chapterUrl.matches(chapterRegex) || chapterUrl.matches(vipChapterRegex)) {
			Chapter chapter = new Chapter();
			chapter.setTitle(link.text());
			chapter.setBookId(bookId);
			chapter.setChapterId(getChapterId(chapterUrl));
			chapter.setVip(chapterUrl.matches(vipChapterRegex));
			// 匹配更新时间
			Pattern p = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
			Matcher m = p.matcher(link.attr("title"));
			if (m.find())
				chapter.setUpdateTime(m.group(0));
			return chapter;
		}
		return null;
	}

	/**
	 * 查找书籍
	 * @param bookId
	 * @return
	 * @throws IOException
	 */
	public Book searchBook(String bookId) throws IOException {
		String url = indexUrl.replace("@bookId", bookId);
		Document doc = Jsoup.connect(url).get();

		Book book = new Book();
		book.setSite(site);
		book.setBookId(bookId);
		book.setTitle(parseTitle(doc));
		book.setAuthor(parseAuthor(doc));
		book.setUpdateTime(parseUpdateTime(doc));

		List<Chapter> chapters = searchChapters(bookId, doc);
		book.setChapters(chapters);
		if (book.getUpdateTime() == null)
			book.setUpdateTime(chapters.get(chapters.size() - 1).getUpdateTime());
		return book;
	}

	public abstract String parseTitle(Document doc);

	public abstract String parseAuthor(Document doc);

	public abstract String parseUpdateTime(Document doc);

	public String readChapterContent(Chapter chapter) throws IOException {
		return readChapterContent(chapter, null);
	}

	/**
	 * 读取章节内容
	 * @param chapter
	 * @param charsetName
	 * @return
	 * @throws IOException
	 */
	public abstract String readChapterContent(Chapter chapter, String charsetName) throws IOException;

}
