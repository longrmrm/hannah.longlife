package com.hannah.gui.bookreader;

import java.io.Serializable;
import java.util.List;

/**
 * 书籍bean
 * @author longrm
 * @date 2013-6-13
 */
public class Book implements Serializable {

	private static final long serialVersionUID = 392064331388144783L;

	private String site; // 书籍站点
	private String bookId; // 书籍id
	private String title; // 书名 or 文件位置
	private String author; // 作者
	private String updateTime; // 最后更新时间
	private String charsetName; // 文件编码格式
	private List<Chapter> chapters; // 章节列表
	private int chapterIndex = 0; // 当前章节

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	public int getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}

	@Override
	public String toString() {
		return site == null ? title : title + " by " + author + " " + updateTime;
	}

}
