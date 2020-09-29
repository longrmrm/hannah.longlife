package com.hannah.swing.component.panel;

import com.hannah.common.util.FileUtil;
import com.hannah.common.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageShowPanel extends JPanel {

	private static final long serialVersionUID = 1538776482229715555L;

	private static final String SOLID = "●";
	private static final String HOLLOW = "○";

	private File[] images;
	private int index;
	private boolean autoZoom = true;

	private JLabel imageLabel = new JLabel();
	private JPanel buttonPanel = new JPanel();

	public ImageShowPanel() {
		initInterface();
	}

	public ImageShowPanel(String imagesPath) {
		initInterface();
		try {
			setImagePath(imagesPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initInterface() {
		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int part = imageLabel.getWidth() / 3;
				if (e.getX() <= part && index > 1)
					showImage(--index);
				else if (e.getX() >= part * 2 && index < images.length)
					showImage(++index);
			}
		});
		imageLabel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int part = imageLabel.getWidth() / 3;
				if (e.getX() <= part) {
					imageLabel.setToolTipText("上一张");
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else if (e.getX() >= part * 2) {
					imageLabel.setToolTipText("下一张");
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					imageLabel.setToolTipText(images[index - 1].getName());
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				showImage(index);
			}
		});
	}

	public void showImage(int index) {
		if (index < 1 || index > images.length)
			return;

		this.index = index;
		ImageIcon icon = new ImageIcon(images[index - 1].getPath());
		imageLabel.setToolTipText(images[index - 1].getName());
		if (autoZoom) {
			BufferedImage image = ImageUtil.getBufferedImage(icon);
			image = ImageUtil.zoom(image, imageLabel.getParent().getWidth(), imageLabel.getParent().getHeight());
			imageLabel.setIcon(new ImageIcon(image));
		} else
			imageLabel.setIcon(icon);

		Component[] comps = buttonPanel.getComponents();
		for (Component comp : comps) {
			if (comp instanceof JLabel) {
				JLabel label = (JLabel) comp;
				if (label.getName().equals("" + index)) {
					label.setText(SOLID);
					label.setForeground(Color.RED);
				} else {
					label.setText(HOLLOW);
					label.setForeground(Color.BLACK);
				}
			}
		}
	}

	private MouseAdapter listener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			int i = Integer.parseInt(label.getName());
			showImage(i);
		}
	};

	public void setImagePath(String imagePath) throws IOException {
		this.index = -1;
		images = new File[] {};
		buttonPanel.removeAll();

		File file = new File(imagePath);
		File[] files = null;
		if (file.isDirectory())
			files = file.listFiles();
		else
			files = new File[] { file };

		List<File> imageList = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			if (!FileUtil.isImage(files[i].getName()))
				continue;

			imageList.add(files[i]);
			JLabel label = new JLabel(HOLLOW);
			label.setFont(new Font("Courier New", Font.PLAIN, 12));
			label.setName("" + imageList.size());
			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			label.addMouseListener(listener);
			buttonPanel.add(label);
		}
		images = imageList.toArray(images);
		buttonPanel.repaint();
	}

	public File[] getImages() {
		return images;
	}

	public int getIndex() {
		return index;
	}

	public boolean isAutoZoom() {
		return autoZoom;
	}

	public void setAutoZoom(boolean autoZoom) {
		this.autoZoom = autoZoom;
		showImage(index);
	}

}
