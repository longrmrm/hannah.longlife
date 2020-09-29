package com.hannah.gui.filemakeup;

// FileDivide.java
// Version 1.0
// longrm  2006-12-15

import java.io.*;

public class FileDivide {
	private final String head = "<head>"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />"
			+ "</HEAD><script language=\"javascript\">function changeColor(color){"
			+ "document.bgColor=color;}</script><body bgcolor=darkgray>"
			+ "字体颜色<select onChange=\"changeColor(this.options[this.selectedIndex].value);\">"
			+ "<option value=darkgray>默认</option><option value=Red>红色</option>"
			+ "<option value=Green>绿色</option><option value=Blue>蓝色</option>"
			+ "<option value=Purple>紫色</option><option value=#FCEFFF>粉红</option>"
			+ "<option value=Orange>橙色</option><option value=Brown>褐色</option>"
			+ "<option value=Teal>墨绿</option><option value=\"#00FFFF\">粉绿</option>"
			+ "<option value=\"#FFE4C4\">黄灰</option><option value=\"#7FFF00\">翠绿</option>"
			+ "<option value=\"#FF7F50\">砖红</option><option value=\"#6495ED\">淡蓝</option>"
			+ "<option value=\"#FF1493\">玫瑰</option><option value=\"#FF00FF\">紫红</option>"
			+ "<option value=\"#FFD700\">桔黄</option><option value=\"#DAA520\">军黄</option>"
			+ "<option value=\"#808080\">烟灰</option><option value=\"#778899\">深灰</option>"
			+ "</select><PRE>";
	private final String foot = "</PRE></body></html>";

	private int index;
	private String model;

	public FileDivide(String fileName, String key, String output, int index, String model)
			throws Exception {
		this.index = index;
		this.model = model;
		divide(fileName, key, output);
	}

	public String getLink() {
		// <a href="a.htm">上一章</a> <a href="a.htm">目录</a> <a
		// href="a.htm">下一章</a>
		String link = "";
		link += "<a href=\"" + Help.formatName(index - 1, model) + ".htm\">上一章</a>  ";
		link += "<a href=\"index.htm\">目录</a>  ";
		link += "<a href=\"" + Help.formatName(index + 1, model) + ".htm\">下一章</a>  ";
		return link;
	}

	public void divide(String fileName, String key, String output) throws Exception {
		FileReader readConnToFile = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(readConnToFile);

		FileWriter writerConnToFile = new FileWriter(output + Help.formatName(index, model)
				+ ".htm");
		PrintWriter printer = new PrintWriter(new BufferedWriter(writerConnToFile));

		String line = reader.readLine();
		printer.println(line);
		line = reader.readLine();

		printer.println(head);
		printer.println(getLink());
		while (line != null) {
			if (Help.newChapter(line, key)) {
				printer.println(getLink());
				printer.println(foot);
				printer.close();
				index++;
				writerConnToFile = new FileWriter(output + Help.formatName(index, model) + ".htm");
				printer = new PrintWriter(new BufferedWriter(writerConnToFile));

				printer.println(head);
				printer.println(getLink());
				printer.println(line);
				line = reader.readLine();
				continue;
			}
			// if( Help.extra(line, "起x文") || Help.extra(line, "(x点") ||
			// Help.extra(line, "cxf") )
			// line = reader.readLine();
			else
				printer.println(line);
			line = reader.readLine();
		}
		printer.println(getLink());
		printer.println(foot);

		reader.close();
		printer.close();
	}

	public static void main(String[] args) throws Exception {
	}
}