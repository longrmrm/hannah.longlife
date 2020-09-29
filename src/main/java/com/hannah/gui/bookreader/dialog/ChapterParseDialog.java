package com.hannah.gui.bookreader.dialog;

import com.hannah.gui.bookreader.JBookReader;
import com.hannah.swing.component.dialog.BasicDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChapterParseDialog extends BasicDialog {

	private static final long serialVersionUID = 3998743111776443877L;

	private JComboBox charsetNameCb;
	private JComboBox titleRegexCb;

	private String[] titleRegexs = new String[] { "^第?[0-9\\u4e00-\\u9fa5]*[章|节]($|[^\\u4e00-\\u9fa5]+.*$)" };

	private JButton okButton;

	public ChapterParseDialog(Frame owner) {
		super(owner, true);

		charsetNameCb = new JComboBox();
		charsetNameCb.addItem("GBK");
		charsetNameCb.addItem("UTF-8");
		charsetNameCb.setPreferredSize(new Dimension(200, 20));

		titleRegexCb = new JComboBox();
		for (String titleRegex : titleRegexs)
			titleRegexCb.addItem(titleRegex);
		titleRegexCb.setEditable(true);
		titleRegexCb.setPreferredSize(new Dimension(200, 20));

		JPanel charsetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
		charsetPanel.add(new JLabel("编码格式："));
		charsetPanel.add(charsetNameCb);
		JPanel titleRegexPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		titleRegexPanel.add(new JLabel("章节标题："));
		titleRegexPanel.add(titleRegexCb);

		okButton = new JButton("确定");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setOk(true);
				ChapterParseDialog.this.dispose();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		buttonPanel.add(okButton);

		this.setTitle(JBookReader.langConfig.getTrans("Chapter Parse"));
		this.setLayout(new BorderLayout(10, 10));
		this.add(charsetPanel, BorderLayout.NORTH);
		this.add(titleRegexPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(owner);
	}

	public String getCharsetName() {
		return (String) charsetNameCb.getSelectedItem();
	}

	public void setCharsetName(String charsetName) {
		charsetNameCb.setSelectedItem(charsetName);
	}

	public String getTitleRegex() {
		return (String) titleRegexCb.getSelectedItem();
	}

	public void setTitleRegex(String titleRegex) {
		titleRegexCb.setSelectedItem(titleRegex);
	}

}
