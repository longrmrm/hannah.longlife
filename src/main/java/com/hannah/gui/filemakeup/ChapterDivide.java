package com.hannah.gui.filemakeup;

// ChapterDivide.java
// Version 1.0
// longrm  2006-12-15

import java.io.*;

public class ChapterDivide {

	public ChapterDivide(String fileName, String key, String output) throws Exception {
		divide(fileName, key, output);
	}

	public void divide(String fileName, String key, String output) throws Exception {
		FileReader readConnToFile = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(readConnToFile);

		char c;
		int i, j = fileName.length();
		for (i = j - 1; i >= 0; i--) {
			c = fileName.charAt(i);
			if (c == '.')
				j = i;
			if (c == '\\')
				break;
		}

		FileWriter writerConnToFile = new FileWriter(output + fileName.substring(++i, j)
				+ "_Edited.txt");
		PrintWriter printer = new PrintWriter(new BufferedWriter(writerConnToFile));

		String line = reader.readLine();
		while (line != null) {
			if (line.trim() != "") {
				if (Help.newChapter(line, key)) {
					// add two blank lines
					printer.println();
					printer.println();

					printer.println(line);
					line = reader.readLine();
					continue;
				}
			}
			printer.println(line);
			line = reader.readLine();
		}

		reader.close();
		printer.close();
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Please Enter fileName, key and output.\n");
			return;
		}
		new ChapterDivide(args[0], args[1], args[2]);
	}
}