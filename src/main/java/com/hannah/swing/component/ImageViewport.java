package com.hannah.swing.component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * 带水印（背景图片）的JViewport
 * @author longrm
 * @date 2013-4-16
 */
public class ImageViewport extends JViewport {

	private static final long serialVersionUID = 3723871147330177768L;

	private TexturePaint texture;

	public ImageViewport() {
	}

	public ImageViewport(URL url) {
		try {
			setBackground(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ImageViewport(JComponent view) {
		setView(view);
	}

	public ImageViewport(JComponent view, URL url) {
		this(url);
		setView(view);
	}

	/**
	 * 设置背景图片
	 * @param url
	 * @throws IOException
	 */
	public void setBackground(URL url) throws IOException {
		BufferedImage bgImg = ImageIO.read(url);
		Rectangle rect = new Rectangle(0, 0, bgImg.getWidth(), bgImg.getHeight());
		texture = new TexturePaint(bgImg, rect);

		Component view = getView();
		if (view != null && view instanceof JComponent)
			((JComponent) view).setOpaque(false);
	}

	/**
	 * 先将要放进来的视图组件的opaque属性设置为false
	 * @param view
	 */
	public void setView(JComponent view) {
		view.setOpaque(false);
		super.setView(view);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// 用TexturePaint画组件
		if (texture != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(texture);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}

}