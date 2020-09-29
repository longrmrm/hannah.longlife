package com.hannah.gui.bookreader.config;

import com.hannah.common.file.AbstractConfig;
import com.hannah.common.util.ColorUtil;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * BookReader配置文件
 * @author longrm
 * @date 2013-12-29
 */
public class BookReaderConfig extends AbstractConfig {

	public BookReaderConfig() {
		setDefaultSetting();
		try {
			saveAllSettings();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getFilePath() {
		return Constant.CURRENT_PATH + File.separator + Constant.CONFIG_FILE;
	}

	private void setDefaultSetting() {
		getSetting(Constant.GLOBAL_SETTING, Constant.LOOK_AND_FEEL, NimbusLookAndFeel.class.getName());
		getSetting(Constant.GLOBAL_SETTING, Constant.LANGUAGE, "Chinese");
		getSettingToFont(Constant.GLOBAL_SETTING, Constant.TEXT_FONT, new Font("微软雅黑", Font.PLAIN, 14));
		getSetting(Constant.GLOBAL_SETTING, Constant.BACKGROUND_COLOR,
				ColorUtil.getHexColorValue(ColorUtil.getSoftColor("豆沙绿")));
		getSetting(Constant.GLOBAL_SETTING, Constant.FOREGROUND_COLOR, ColorUtil.getHexColorValue(Color.BLACK));
		getSettingToBoolean(Constant.GLOBAL_SETTING, Constant.IS_LINE_WRAP, true);
		getSettingToBoolean(Constant.GLOBAL_SETTING, Constant.IS_EDITABLE, false);
		getSettingToBoolean(Constant.GLOBAL_SETTING, Constant.SHOW_LINE_NUMBER, true);
		getSettingToBoolean(Constant.GLOBAL_SETTING, Constant.SHOW_STATUS_BAR, true);
	}

}
