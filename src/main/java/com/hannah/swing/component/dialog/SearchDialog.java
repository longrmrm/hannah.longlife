package com.hannah.swing.component.dialog;

import com.hannah.swing.component.button.ImageButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class SearchDialog extends BasicDialog {

	private static final long serialVersionUID = -1247805070265724662L;

	private JTextField searchTf = new JTextField(30);
	private JButton upSearchButton = new ImageButton("上一个");
	private JButton downSearchButton = new ImageButton("下一个");

	public SearchDialog() {
		this((Frame) null);
	}

	public SearchDialog(Frame owner) {
		super(owner, true);
		super.setTitle("查找");

		initInterface();

		this.pack();
		this.setLocationRelativeTo(null);
	}

	public SearchDialog(Frame owner, String title) {
		this(owner);
		super.setTitle(title);
	}

	private void initInterface() {
		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean result = false;
				if (e.getSource() == upSearchButton) {
					result = search(-1);
				} else {
					result = search(1);
				}
				if (!result)
					JOptionPane.showMessageDialog(null, "没找到含有 \"" + getSearchText() + "\" 的记录！",
							"查找", JOptionPane.WARNING_MESSAGE);
			}
		};
		searchTf.addActionListener(l);
		upSearchButton.addActionListener(l);
		downSearchButton.addActionListener(l);

		searchTf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					SearchDialog.this.dispose();
			}
		});

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		searchPanel.add(new JLabel("查找内容："));
		searchPanel.add(searchTf);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
		buttonPanel.add(upSearchButton);
		buttonPanel.add(downSearchButton);

		this.setLayout(new GridLayout(2, 1));
		this.add(searchPanel);
		this.add(buttonPanel);
	}

	protected abstract boolean search(int direction);

	public String getSearchText() {
		return searchTf.getText();
	}

}
