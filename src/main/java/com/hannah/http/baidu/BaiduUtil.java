package com.hannah.http.baidu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Baidu Search Util
 * @author longrm
 * @date 2013-4-22
 */
public class BaiduUtil {

	public static String getSearchUrl(String searchWord) throws UnsupportedEncodingException {
		return getSearchUrl(searchWord, 1);
	}

	/**
	 * 获取百度搜索的url地址
	 * @param searchWord 查询关键字
	 * @param page 查询页
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getSearchUrl(String searchWord, int page) throws UnsupportedEncodingException {
		searchWord = URLEncoder.encode(searchWord, "utf-8");
		if (page == 1)
			return "http://www.baidu.com/s?ie=utf-8&bs=" + searchWord + "&f=8&rsv_bp=1&wd=" + searchWord;
		else
			return "http://www.baidu.com/s?wd=" + searchWord + "&pn=" + (page - 1) * 10 + "&ie=utf-8&rsv_page=1";
	}

	public static List<BaiduSearchResult> search(String searchWord, int page) throws IOException {
		return fetch(getSearchUrl(searchWord, page));
	}

	/**
	 * 返回查询结果
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static List<BaiduSearchResult> fetch(String url) throws IOException {
		List<BaiduSearchResult> results = new ArrayList<BaiduSearchResult>();
		Document doc = Jsoup.connect(url).get();
		Elements tables = doc.select("table[tpl]");
		for (Element table : tables) {
			// 判断是否是查询结果table
			if (table.attr("class").indexOf("result") == -1)
				continue;
			String tpl = table.attr("tpl");
			// 去掉微博、最新消息
			if (tpl.equals("se_sp_weibo") || tpl.equals("se_sp_realtime_static"))
				continue;

			// 获取标题和链接
			Element title = table.select("a[href]").first();
			BaiduSearchResult result = new BaiduSearchResult();
			result.setTitle(title.text());
			result.setUrl(title.attr("href"));
			// 判断结果类型
			Element brief = null; // 快照
			Element shortUrl = null; // 短链接
			if (tpl.equals("se_st_default") || tpl.equals("se_st_guanwang") || tpl.equals("se_com_default")) {
				brief = table.select("div.c-abstract").first();
				shortUrl = table.select("span.g").first();
			}
			// 百度百科
			else if (tpl.equals("baikespecial")) {
				brief = table.select("p").first();
				shortUrl = table.select("span.c-showurl").first();
			} else if (tpl.equals("se_st_baike")) {
				Elements p = table.select("p");
				brief = p.get(0);
				if (p.size() > 1)
					shortUrl = p.get(1);
			} else {
				Elements div = table.select("div");
				if (div.size() >= 3) {
					brief = div.get(0);
					if (div.size() > 2)
						shortUrl = div.get(2);
				} else
					brief = table.select("td").first();
			}
			// 设值
			result.setBrief(brief == null ? "" : brief.text());
			if (shortUrl != null) {
				String[] strs = shortUrl.text().trim().replace(' ', ' ').split(" ");
				if (strs.length >= 2) {
					result.setShortRealUrl(strs[0]);
					result.setUpdateDate(strs[1]);
				}
			}
			results.add(result);
		}
		return results;
	}

}
