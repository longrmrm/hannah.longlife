package com.hannah.swing.component.menuitem;

import com.hannah.swing.component.panel.ImageShowPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ImageShowMenuItem extends JMenuItem {

	private static final long serialVersionUID = -96451611327381163L;

	private ActionListener listener;
	private Dimension imageSize = new Dimension(800, 500);

	public ImageShowMenuItem(String imagePath) {
		this(null, null, imagePath);
	}

	public ImageShowMenuItem(Icon icon, String imagePath) {
		this(null, icon, imagePath);
	}

	public ImageShowMenuItem(String text, String imagePath) {
		this(text, null, imagePath);
	}

	public ImageShowMenuItem(String text, Icon icon, String imagePath) {
		super(text, icon);
		setImagePath(imagePath);
	}

	public void setImagePath(final String imagePath) {
		if (listener != null)
			this.removeActionListener(listener);

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ImageShowPanel imagePanel = new ImageShowPanel();
				imagePanel.setAutoZoom(false);
				imagePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				imagePanel.setBackground(Color.DARK_GRAY);
				try {
					imagePanel.setImagePath(imagePath);
					imagePanel.showImage(1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				JFrame f = new JFrame(e.getActionCommand());
				f.add(imagePanel);
				f.setSize(imageSize.width + 32, imageSize.height + 78);
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		};
		this.addActionListener(listener);
	}

	public Dimension getImageSize() {
		return imageSize;
	}

	public void setImageSize(Dimension imageSize) {
		this.imageSize = imageSize;
	}

}
