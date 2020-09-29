package com.hannah.gui.filemakeup;

// Help.java
// Version 1.0
// longrm 2006-12-15

public class Help {
	public static boolean newChapter(String line, String key) {
		/*
		 * if(line.length()<3) return false; if(line.charAt(0)!='0') return
		 * false; return isNumber(line.substring(0, 3));
		 */
		int i = key.indexOf("x");
		String first = key.substring(0, i);
		String last = key.substring(i + 1);

		i = line.indexOf(first);
		if (i != -1) {
			if (line.substring(i + first.length()).indexOf(last) != -1)
				return true;
		}
		return false;
		/*
		 * char c; int len = line.length();
		 * 
		 * for(int i=0; i<len; i++) { c = line.charAt(i); if( c == key.charAt(0)
		 * ) { for(int j=0; j<10 && (i+j)<len; j++) { c = line.charAt(i+j); if(c
		 * == key.charAt(key.length()-1)) return true; } } }
		 */

		/*
		 * try{ i = Integer.parseInt( line.substring(0,1) ); if( i>0 ) return
		 * true; }catch(Exception e){ return false; };
		 */
	}

	private static boolean isNumber(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < '0' || str.charAt(i) > '9')
				return false;
		}
		return true;
	}

	public static boolean extra(String line, String key) {
		char c;
		int len = line.length();

		for (int i = 0; i < len; i++) {
			c = line.charAt(i);
			if (c == key.charAt(0)) {
				for (int j = 0; j < 5 && (i + j) < len; j++) {
					c = line.charAt(i + j);
					if (c == key.charAt(key.length() - 1))
						return true;
				}
			}
		}
		return false;
	}

	public static String formatName(int index, String model) {
		String name = "";
		if (index == 0)
			name = model;
		else if (index > 0) {
			for (int i = 0; i < model.length(); i++) {
				// 前面加
				if (index / Math.pow(10, model.length() - 1 - i) < 1)
					name += model.charAt(i);
			}
			name += index;
		} else if (index < 0) {
			name += "-";
			index = -index;
			for (int i = 0; i < model.length(); i++) {
				// 前面加
				if (index / Math.pow(10, model.length() - 1 - i) < 1)
					name += model.charAt(i);
			}
			name += index;
		}
		return name;
	}

	public static String createLink(String line, int index, String model) {
		String link = "";
		String fileName = formatName(index, model) + ".htm";
		link += "<a href=\"" + fileName + "\">" + line.trim() + "</a>";
		return link;
	}

	public static void main(String[] args) {
		System.out.println(Help.formatName(-1, "000"));
	}
}