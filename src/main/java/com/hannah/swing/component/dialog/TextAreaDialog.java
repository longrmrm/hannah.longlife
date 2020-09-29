package com.hannah.swing.component.dialog;

import javax.swing.*;
import java.awt.*;

public class TextAreaDialog extends BasicDialog {

	private static final long serialVersionUID = -7602504807788204927L;

	private JTextArea textArea;
	private JTextArea statusTa = new JTextArea();

	public TextAreaDialog() {
		super();
		initInterface();
	}

	public TextAreaDialog(String title) {
		super();
		super.setTitle(title);
		initInterface();
	}

	public TextAreaDialog(String title, JTextArea textArea) {
		super();
		super.setTitle(title);
		this.textArea = textArea;
		initInterface();
	}

	private void initInterface() {
		if (textArea == null) {
			textArea = new JTextArea(20, 50);
			textArea.setAutoscrolls(true);
		}
		statusTa.setEditable(false);

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(textArea), BorderLayout.CENTER);
		this.add(statusTa, BorderLayout.SOUTH);
	}

	public JTextArea getTextArea() {
		return textArea;
	}
	
	public void setText(String text) {
		textArea.setText(text);
	}

	public JTextArea getStatusTa() {
		return statusTa;
	}
	
	public void setStatus(String status) {
		statusTa.setText(status);
	}
	
	public void setStatusVisible(boolean flag) {
		statusTa.setVisible(flag);
	}

}
