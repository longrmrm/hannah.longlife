package com.hannah.gui.bookreader.bookspider;

public class BookSpiderFactory {

	private static BookSpiderFactory instance;

	private BookSpiderFactory() {
	}

	public static BookSpiderFactory getInstance() {
		if (instance == null)
			instance = new BookSpiderFactory();
		return instance;
	}

	public AbstractBookSpider getBookSpider(String site) {
		if ("www.qidian.com".equals(site))
			return new QidianBookSpider();
		else if("www.zongheng.com".equals(site))
			return new ZonghengBookSpider();
		return null;
	}

}
