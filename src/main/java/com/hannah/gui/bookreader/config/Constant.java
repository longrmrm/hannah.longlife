package com.hannah.gui.bookreader.config;

import com.hannah.common.util.FileUtil;

public class Constant {

	public static final String JBOOKREADER = "JBookReader";

	// 配置文件
	public final static String CURRENT_PATH = FileUtil.getExecuteDirectoryPath(Constant.class);

	public final static String CONFIG_FILE = "config.ini";

	public final static String LANGUAGE_FILE = "language.ini";

	public final static String HISTORY_FILE = "history.ini";

	// 全局
	public final static String GLOBAL_SETTING = "globalSetting";

	// 外观
	public final static String LOOK_AND_FEEL = "lookAndFeel";

	// 语言
	public final static String LANGUAGE = "language";

	// 字体
	public final static String TEXT_FONT = "textFont";

	// 颜色
	public final static String BACKGROUND_COLOR = "backgroundColor";
	public final static String FOREGROUND_COLOR = "foregroundColor";

	// 背景
	public final static String BACKGROUND_IMAGE = "backgroundImage";

	public final static String IS_LINE_WRAP = "isLineWrap";
	public final static String IS_EDITABLE = "isEditable";
	public final static String SHOW_LINE_NUMBER = "showLineNumber";
	public final static String SHOW_STATUS_BAR = "showStatusBar";

}
