package com.hannah.gui.bookreader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 章节工具类：解析小说文本中的章节目录
 * @author longrm
 * @date 2013-4-11
 */
public class ChapterUtil {

	/**
	 * Random Access性能过慢
	 * @param file
	 * @param charsetName
	 * @param titleRegex
	 * @return
	 * @throws IOException
	 */
	public static List<Chapter> parseFileByRandomAccess(File file, String charsetName, String titleRegex)
			throws IOException {
		List<Chapter> chapterList = new ArrayList<Chapter>();
		Chapter currentChapter = null;
		long filePointer = 0;
		RandomAccessFile randomFile = new RandomAccessFile(file, "r");
		String line = randomFile.readLine();
		while (line != null) {
			line = new String(line.getBytes("ISO-8859-1"), charsetName);
			if (chapterList.size() == 0 || line.matches(titleRegex)) {
				if (currentChapter != null)
					currentChapter.setEndFilePointer(filePointer);

				currentChapter = new Chapter(line);
				currentChapter.setStartFilePointer(filePointer);
				chapterList.add(currentChapter);
			}
			filePointer = randomFile.getFilePointer();
			line = randomFile.readLine();
		}
		currentChapter.setEndFilePointer(filePointer);
		randomFile.close();
		return chapterList;
	}

	/**
	 * 解析小说章节列表
	 * @param file
	 * @param charsetName
	 * @param titleRegex
	 * @return
	 * @throws IOException
	 */
	public static List<Chapter> parseFile(File file, String charsetName, String titleRegex) throws IOException {
		List<Chapter> chapterList = new ArrayList<Chapter>();
		int separatorLength = System.getProperty("line.separator").length();
		Chapter currentChapter = null;
		long filePointer = 0;
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), charsetName);
		BufferedReader reader = new BufferedReader(isr);
		String line = reader.readLine();
		while (line != null) {
			if (chapterList.size() == 0 || line.matches(titleRegex)) {
				if (currentChapter != null)
					currentChapter.setEndFilePointer(filePointer);

				currentChapter = new Chapter(line);
				currentChapter.setStartFilePointer(filePointer);
				chapterList.add(currentChapter);
			}
			// 获取实际filePointer：ISO编码格式的line长度 + 换行符长度
			filePointer += new String(line.getBytes(charsetName), "ISO-8859-1").length() + separatorLength;
			line = reader.readLine();
		}
		currentChapter.setEndFilePointer(filePointer);
		reader.close();
		return chapterList;
	}

}
