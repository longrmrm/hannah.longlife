package com.hannah.gui.filemakeup;

// TitleFetch.java
// Version 1.0
// longrm  2006-12-15

import java.io.*;
import java.util.Scanner;

public class TitleFetch {
	public TitleFetch(String fileName, String key, String output) throws Exception {
		fetch(fileName, key, output);
	}

	public void fetch(String fileName, String key, String output) throws Exception {
		FileReader readConnToFile = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(readConnToFile);

		FileWriter writerConnToFile = new FileWriter(output + "title.txt");
		PrintWriter printer = new PrintWriter(new BufferedWriter(writerConnToFile));
		FileWriter writer = new FileWriter(output + "index.htm");
		PrintWriter indexPrinter = new PrintWriter(new BufferedWriter(writer));

		int index = 0;
		String line = reader.readLine();
		String head = "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />"
				+ "</HEAD><body bgcolor=darkgray><PRE>";
		indexPrinter.println(head);
		while (line != null) {
			if (line.trim() != "") {
				if (Help.newChapter(line, key)) {
					printer.println(line);
					indexPrinter.println(Help.createLink(line, ++index, "000"));
				}
			}
			line = reader.readLine();
		}
		indexPrinter.println("</PRE></body></html>");

		reader.close();
		printer.close();
		indexPrinter.close();
	}

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		String[] str = sc.nextLine().split(" ");
		if (str.length < 3) {
			System.out.println("Please Enter fileName, key and output.\n");
			return;
		}
		new TitleFetch(args[0], args[1], args[2]);
	}
}