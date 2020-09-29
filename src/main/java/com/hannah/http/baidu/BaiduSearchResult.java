package com.hannah.http.baidu;

/**
 * Baidu Search Result
 * @author longrm
 * @date 2013-4-22
 */
public class BaiduSearchResult {

	private String title;
	private String url;
	private String brief;
	private String shortRealUrl;
	private String updateDate;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getShortRealUrl() {
		return shortRealUrl;
	}

	public void setShortRealUrl(String shortRealUrl) {
		this.shortRealUrl = shortRealUrl;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	@Override
	public String toString() {
		return title;
	}

}
