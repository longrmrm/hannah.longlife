package com.hannah.swing.util;

import com.hannah.common.util.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VerifyCodeUtil {

	public static final int WIDTH = 120;
	public static final int HEIGHT = 30;

	public static final int RECT_WIDTH = 10;
	public static final int RECT_HEIGHT = 5;

	public static BufferedImage createVerifyCode(String strs) {
		return createVerifyCode(strs, new Font("VeraSansYuanTi", Font.BOLD, 24), Color.RED);
	}

	/**
	 * @param str
	 * @param font
	 * @param color
	 * @return
	 */
	public static BufferedImage createVerifyCode(String str, Font font, Color color) {
		// 创建缓存
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		// 获得画布
		Graphics g = bi.getGraphics();
		// 设置背影色
		setBackGround(g);
		// 设置边框
		setBorder(g);
		// 画干扰线
		g.setColor(color);
		drawRandomLine(g);
		// 设置字体和颜色
		g.setFont(font);
		g.setColor(color);
		// 画文字
		drawRandomNum((Graphics2D) g, str);
		return bi;
	}

	/**
	 * 设置背景色
	 * @param g
	 */
	private static void setBackGround(Graphics g) {
		// 填充颜色范围
		int ci = 0xFFFFFF;
		int cj = 0xFF8000;
		int ck = ci - cj;
		// 循环填充
		int startWidth = 0, startHeight = 0, toWidth = RECT_WIDTH, toHeight = RECT_HEIGHT;
		Random random = new Random();
		while (true) {
			int r = random.nextInt(ck) + 1;
			Color color = ColorUtil.getColor("#" + Integer.toHexString(cj + r));
			g.setColor(color);
			
			if (toWidth > WIDTH)
				toWidth = WIDTH;
			if (toHeight > HEIGHT)
				toHeight = HEIGHT;
			// 填充区域
			g.fillRect(startWidth, startHeight, toWidth, toHeight);
			
			if (toWidth == WIDTH) {
				if (toHeight == HEIGHT)
					break;
				startWidth = 0;
				startHeight = toHeight;
				toWidth = RECT_WIDTH;
				toHeight += RECT_HEIGHT;
			} else {
				startWidth = toWidth;
				toWidth += RECT_WIDTH;
			}
		}
//		g.setColor(Color.WHITE);
//		g.fillRect(0, 0, WIDTH, HEIGHT);
	}

	/**
	 * 设置边框
	 * @param g
	 */
	private static void setBorder(Graphics g) {
		// 设置边框颜色
		g.setColor(Color.BLUE);
		// 边框区域
		g.drawRect(1, 1, WIDTH - 2, HEIGHT - 2);
	}

	/**
	 * 画随机线条
	 * @param g
	 */
	private static void drawRandomLine(Graphics g) {
		// 设置线条个数并画线
		for (int i = 0; i < 5; i++) {
			int x1 = new Random().nextInt(WIDTH);
			int y1 = new Random().nextInt(HEIGHT);
			int x2 = new Random().nextInt(WIDTH);
			int y2 = new Random().nextInt(HEIGHT);
			g.drawLine(x1, y1, x2, y2);
		}
	}

	/**
	 * 画随机汉字
	 * @param g
	 * @param str
	 * @return
	 */
	private static void drawRandomNum(Graphics2D g, String str) {
		int x = 5;
		// 控制字数
		for (int i = 0; i < str.length(); i++) {
			// 设置字体旋转角度
			int degree = new Random().nextInt() % 30;
			// 截取汉字
			String ch = str.charAt(i) + "";
			// 正向角度
			g.rotate(degree * Math.PI / 180, x, 20);
			g.drawString(ch, x, 20);
			// 反向角度
			g.rotate(-degree * Math.PI / 180, x, 20);
			x += 30;
		}
	}

}
