package com.hannah.common.file;

public class FileConstants {

	public static final String SECTION_REGEX = "\\[[^\\[\\]]+\\]";

	public static final String COMMENT_PREFIX = "#";
	
	public static final String EQUAL_KEY = " = ";
	
	public static final String COLON_KEY = " : ";

	public static String getSectionName(String sectionLine) {
		return sectionLine.substring(1, sectionLine.length() - 1);
	}

}
