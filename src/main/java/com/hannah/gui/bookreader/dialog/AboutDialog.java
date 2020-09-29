package com.hannah.gui.bookreader.dialog;

import com.hannah.gui.bookreader.JBookReader;
import com.hannah.gui.bookreader.config.Constant;
import com.hannah.swing.component.dialog.BasicDialog;
import com.hannah.swing.layout.VFlowLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AboutDialog extends BasicDialog {

	private static final long serialVersionUID = -8757041560337089183L;

	public AboutDialog(Frame owner) {
		super(owner, JBookReader.langConfig.getTrans("About JBookReader"), true);

		JLabel titleLb = new JLabel();
		titleLb.setForeground(Color.WHITE);
		titleLb.setFont(new Font("Consolas Bold Italic", Font.BOLD, 20));
		titleLb.setText(Constant.JBOOKREADER);

		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(new TitledBorder(""));
		titlePanel.setBackground(Color.DARK_GRAY);
		titlePanel.add(titleLb);

		this.setLayout(new VFlowLayout());
		this.getContentPane().setBackground(Color.DARK_GRAY);
		this.add(titlePanel);
		this.add(createTextPanel("Author：酒醉千年  ( Email：longrm@qq.com )"));
		this.add(createTextPanel("Version 0.1.0 bt,  Build 2014.01.01"));
		this.setSize(400, 190);
		this.setLocationRelativeTo(owner);
	}

	public JPanel createTextPanel(String text) {
		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		label.setText(text);

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.add(label);
		return panel;
	}

}
