package com.hannah.swing.component.panel;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -2902761569828762796L;

	private ImageIcon image = null;

	public ImagePanel(ImageIcon image) {
		this.image = image;
		this.setLayout(new BorderLayout());
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		setOpaque(true);
		super.paintComponent(g);
		Dimension d = getSize();
		for (int x = 0; x < d.width; x += image.getIconWidth())
			for (int y = 0; y < d.height; y += image.getIconHeight())
				g.drawImage(image.getImage(), x, y, null, null);
	}

}
