package com.hannah.common.file;

import com.hannah.common.util.ColorUtil;
import com.hannah.common.util.DateUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理抽象类
 * @author longrm
 * @date 2013-8-22
 */
public abstract class AbstractConfig {

	private Ini ini;

	private boolean changed = false;

	/**
	 * 当前section位置，简化get、update、delete操作
	 */
	private String sectionName;

	protected abstract String getFilePath();

	public Ini getIni() {
		if (ini == null) {
			ini = new Ini();
			File file = new File(getFilePath());
			try {
				if (file.exists() || file.createNewFile())
					ini.load(file);
				else
					throw new RuntimeException(getFilePath() + " create failed!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ini;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * 获取所有设置
	 * @return
	 */
	public Map<String, Map<String, String>> getAllSettings() {
		return getIni().getSectionMap();
	}

	/**
	 * 将所有设置保存到文件
	 * @throws IOException
	 */
	public void saveAllSettings() throws IOException {
		if (changed) {
			getIni().save(new File(getFilePath()));
			changed = false;
		}
	}

	/**
	 * 获取某个section
	 * @param sectionName
	 * @return
	 */
	public Map<String, String> getSection(String sectionName) {
		Map<String, String> section = getIni().getSectionContext(sectionName);
		if (section == null) {
			section = new HashMap<String, String>();
			getIni().addSection(sectionName, section);
			changed = true;
		}
		return section;
	}

	/**
	 * 设置某个section
	 * @param sectionName
	 * @param section
	 */
	public void setSection(String sectionName, Map<String, String> section) {
		getIni().addSection(sectionName, section);
		changed = true;
	}

	/**
	 * 清空某个section
	 * @param sectionName
	 * @return
	 */
	public void clearSection(String sectionName) {
		getSection(sectionName).clear();
		changed = true;
	}

	/**
	 * 删除某个section
	 * @param sectionName
	 * @return
	 */
	public void deleteSection(String sectionName) {
		getIni().deleteSection(sectionName);
		changed = true;
	}

	/**
	 * 切换到某个section
	 * @param sectionName
	 */
	public void switchSection(String sectionName) {
		this.sectionName = sectionName;
	}

	/**
	 * 获取当前section的设置
	 * @param key
	 * @return
	 */
	public String getSetting(String key) {
		return getSection(sectionName).get(key);
	}

	/**
	 * 获取某个section的设置
	 * @param sectionName
	 * @param key
	 * @return
	 */
	public String getSetting(String sectionName, String key) {
		return getSection(sectionName).get(key);
	}

	/**
	 * 获取某个section的设置
	 * @param sectionName
	 * @param key
	 * @param defaultValue 默认值
	 * @return
	 */
	public String getSetting(String sectionName, String key, String defaultValue) {
		String value = getSection(sectionName).get(key);
		if (value != null || defaultValue == null)
			return value;

		value = defaultValue;
		updateSetting(sectionName, key, value);
		return value;
	}

	public Boolean getSettingToBoolean(String key) {
		return Boolean.valueOf(getSetting(key));
	}

	public Boolean getSettingToBoolean(String sectionName, String key) {
		return getSettingToBoolean(sectionName, key, false);
	}

	public Boolean getSettingToBoolean(String sectionName, String key, Boolean defaultValue) {
		String value = getSetting(sectionName, key, String.valueOf(defaultValue));
		return Boolean.valueOf(value);
	}

	public Integer getSettingToInt(String key) {
		return Integer.valueOf(getSetting(key));
	}

	public Integer getSettingToInt(String sectionName, String key) {
		return getSettingToInt(sectionName, key, null);
	}

	public Integer getSettingToInt(String sectionName, String key, Integer defaultValue) {
		String value = getSetting(sectionName, key, String.valueOf(defaultValue));
		return Integer.valueOf(value);
	}

	public Date getSettingToDate(String key) {
		return DateUtil.ddStringToDate(getSetting(key));
	}

	public Date getSettingToDate(String sectionName, String key) {
		return getSettingToDate(sectionName, key, new Date());
	}

	/**
	 * 获取某个section的日期设置
	 * @param sectionName
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Date getSettingToDate(String sectionName, String key, Date defaultValue) {
		String value = getSetting(sectionName, key, DateUtil.dateToDdString(defaultValue));
		return DateUtil.ddStringToDate(value);
	}

	public Font getSettingToFont(String key) {
		return getSettingToFont(sectionName, key, null);
	}

	public Font getSettingToFont(String sectionName, String key) {
		return getSettingToFont(sectionName, key, null);
	}

	/**
	 * 获取某个section的字体设置
	 * @param sectionName
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Font getSettingToFont(String sectionName, String key, Font defaultValue) {
		String value = getSetting(sectionName, key, getFontSetting(defaultValue));
		String[] fonts = value.split(",");
		return fonts.length == 3 ? new Font(fonts[0], Integer.parseInt(fonts[1].trim()), Integer.parseInt(fonts[2]
				.trim())) : null;
	}

	public String getFontSetting(Font font) {
		if (font == null)
			return "";
		return font.getName() + ", " + font.getStyle() + ", " + font.getSize();
	}

	public Color getSettingToColor(String key) {
		return getSettingToColor(sectionName, key);
	}

	public Color getSettingToColor(String sectionName, String key) {
		return getSettingToColor(sectionName, key, Color.BLACK);
	}

	/**
	 * 获取某个section的颜色设置
	 * @param sectionName
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Color getSettingToColor(String sectionName, String key, Color defaultValue) {
		String value = getSetting(sectionName, key, ColorUtil.getHexColorValue(defaultValue));
		return ColorUtil.getColor(value);
	}

	/**
	 * 更新当前section的设置
	 * @param key
	 * @param value
	 */
	public void updateSetting(String key, String value) {
		updateSetting(sectionName, key, value);
	}

	/**
	 * 更新某个section的设置
	 * @param sectionName
	 * @param key
	 * @param value
	 */
	public void updateSetting(String sectionName, String key, String value) {
		getIni().addSectionProperty(sectionName, key, value);
		changed = true;
	}

	public void updateSetting(String key, Boolean value) {
		updateSetting(sectionName, key, value);
	}

	public void updateSetting(String sectionName, String key, Boolean value) {
		getIni().addSectionProperty(sectionName, key, String.valueOf(value));
		changed = true;
	}

	public void updateSetting(String key, Integer value) {
		updateSetting(sectionName, key, value);
	}

	public void updateSetting(String sectionName, String key, Integer value) {
		getIni().addSectionProperty(sectionName, key, String.valueOf(value));
		changed = true;
	}

	public void updateSetting(String key, Date value) {
		updateSetting(sectionName, key, value);
	}

	public void updateSetting(String sectionName, String key, Date value) {
		getIni().addSectionProperty(sectionName, key, DateUtil.dateToDdString(value));
		changed = true;
	}

	public void updateSetting(String key, Font value) {
		updateSetting(sectionName, key, value);
	}

	public void updateSetting(String sectionName, String key, Font value) {
		getIni().addSectionProperty(sectionName, key, getFontSetting(value));
		changed = true;
	}

	public void updateSetting(String key, Color value) {
		updateSetting(sectionName, key, value);
	}

	public void updateSetting(String sectionName, String key, Color value) {
		getIni().addSectionProperty(sectionName, key, ColorUtil.getHexColorValue(value));
		changed = true;
	}

	/**
	 * 删除当前section的设置
	 * @param key
	 */
	public void deleteSetting(String key) {
		deleteSetting(key, key);
	}

	/**
	 * 删除某个section的设置
	 * @param key 键
	 */
	public void deleteSetting(String sectionName, String key) {
		getIni().deleteSectionProperty(sectionName, key);
		changed = true;
	}

}
