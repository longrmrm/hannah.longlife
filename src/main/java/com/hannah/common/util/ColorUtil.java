package com.hannah.common.util;

import java.awt.*;

/**
 * @author longrm
 * @date 2012-10-15
 */
public class ColorUtil {

	public static final Color[] COLORS = new Color[] { Color.white, Color.lightGray, Color.gray, Color.darkGray,
			Color.black, Color.red, Color.pink, Color.orange, Color.yellow, Color.green, Color.magenta, Color.cyan,
			Color.blue };

	public static final String[] COLOR_NAMES = new String[] { "white", "lightGray", "gray", "darkGray", "black", "red",
			"pink", "orange", "yellow", "green", "magenta", "cyan", "blue" };

	public static final Color[] SOFT_COLORS = new Color[] { new Color(199, 237, 204), new Color(255, 255, 255),
			new Color(250, 249, 222), new Color(255, 242, 226), new Color(253, 230, 224), new Color(227, 237, 205),
			new Color(220, 226, 241), new Color(233, 235, 254), new Color(234, 234, 239) };

	public static final String[] SOFT_COLOR_NAMES = new String[] { "豆沙绿", "银河白", "杏仁黄", "秋叶褐", "胭脂红", "青草绿", "海天蓝",
			"葛巾紫", "极光灰" };

	public static Color getSoftColor(String softColorName) {
		for (int i = 0; i < SOFT_COLORS.length; i++) {
			if (SOFT_COLOR_NAMES[i].equals(softColorName))
				return SOFT_COLORS[i];
		}
		return null;
	}

	public static String getHexColorValue(Color c) {
		return getHexColorValue(c.getRed(), c.getGreen(), c.getBlue());
	}

	/**
	 * @param r
	 * @param g
	 * @param b
	 * @return hex color value
	 */
	public static String getHexColorValue(int r, int g, int b) {
		String rHex = Integer.toHexString(r).toUpperCase();
		String gHex = Integer.toHexString(g).toUpperCase();
		String bHex = Integer.toHexString(b).toUpperCase();

		rHex = rHex.length() == 1 ? "0" + rHex : rHex;
		gHex = gHex.length() == 1 ? "0" + gHex : gHex;
		bHex = bHex.length() == 1 ? "0" + bHex : bHex;
		return "#" + rHex + gHex + bHex;
	}

	/**
	 * @param hexColorValue
	 * @return
	 */
	public static Color getColor(String hexColorValue) {
		if (hexColorValue.startsWith("#"))
			hexColorValue = hexColorValue.substring(1);
		if (hexColorValue.length() != 6)
			return null;

		int r = Integer.parseInt(hexColorValue.substring(0, 2), 16);
		int g = Integer.parseInt(hexColorValue.substring(2, 4), 16);
		int b = Integer.parseInt(hexColorValue.substring(4, 6), 16);
		return new Color(r, g, b);
	}

}
