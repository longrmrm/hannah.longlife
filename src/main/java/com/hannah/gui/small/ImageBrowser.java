package com.hannah.gui.small;

import com.hannah.swing.component.panel.ImageShowPanel;
import com.hannah.swing.util.FileChooserUtil;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ImageBrowser extends JPanel {

	private static final long serialVersionUID = 679662685572255037L;

	private JToolBar toolBar = new JToolBar();
	private JButton chooseFileBut;
	private JButton chooseFolderBut;
	private JCheckBox zoomCb;
	
	private ImageShowPanel imagePanel = new ImageShowPanel();

	public ImageBrowser() {
		initToolBar();
		this.setLayout(new BorderLayout());
		this.add(toolBar, BorderLayout.NORTH);
		this.add(imagePanel, BorderLayout.CENTER);
	}

	private void initToolBar() {
		chooseFileBut = new JButton("选择图片");
		chooseFolderBut = new JButton("选择图片目录");
		zoomCb = new JCheckBox("自动缩放", true);
		
		toolBar.add(chooseFileBut);
		toolBar.add(chooseFolderBut);
		toolBar.add(zoomCb);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = null;
				if (e.getSource() == chooseFileBut) {
					FileFilter filter = new FileNameExtensionFilter("image file", "jpg", "jpeg",
							"gif", "png", "bmp", "icon");
					file = FileChooserUtil.showFileChooser(ImageBrowser.this, filter);
				} else if (e.getSource() == chooseFolderBut)
					file = FileChooserUtil.showDirectoryChooser(ImageBrowser.this);

				if (file == null)
					return;
				try {
					imagePanel.setImagePath(file.getPath());
					imagePanel.showImage(1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		chooseFileBut.addActionListener(listener);
		chooseFolderBut.addActionListener(listener);
		
		zoomCb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				imagePanel.setAutoZoom(zoomCb.isSelected());
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("Image Browser");
		frame.add(new ImageBrowser());
		frame.setSize(700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
