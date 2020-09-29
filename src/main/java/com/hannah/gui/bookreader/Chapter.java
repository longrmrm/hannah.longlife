package com.hannah.gui.bookreader;

import com.hannah.common.util.StringUtil;

import java.io.Serializable;

/**
 * 章节bean
 * @author longrm
 * @date 2013-4-11
 */
public class Chapter implements Serializable {

	private static final long serialVersionUID = 2565867877541824070L;

	private String title; // 章节标题
	private long startFilePointer; // 章节开始的文件位置
	private long endFilePointer; // 章节结束的文件位置
	private String bookId; // 书籍id
	private String chapterId; // 章节id
	private String updateTime; // 更新时间
	private boolean vip = false; // 是否vip章节

	public Chapter() {
	}

	public Chapter(String title) {
		setTitle(title);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getStartFilePointer() {
		return startFilePointer;
	}

	public void setStartFilePointer(long startFilePointer) {
		this.startFilePointer = startFilePointer;
	}

	public long getEndFilePointer() {
		return endFilePointer;
	}

	public void setEndFilePointer(long endFilePointer) {
		this.endFilePointer = endFilePointer;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getChapterId() {
		return chapterId;
	}

	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	/**
	 * bookId为空，为本地文件章节
	 * @return
	 */
	public boolean isLocal() {
		return bookId == null;
	}

	@Override
	public String toString() {
		return (vip ? "Vip " : "") + StringUtil.toEllipsis(title, 20);
	}

}
