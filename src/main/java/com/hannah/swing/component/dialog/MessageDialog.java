package com.hannah.swing.component.dialog;

import com.hannah.common.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * dialog that show message with icon
 * @author longrm
 * @date 2012-4-14
 */
public class MessageDialog extends ConfirmDialog {

	private static final long serialVersionUID = 4430513313039299569L;

	public static final int LOADING_MESSAGE = 4;
	private int messageType = JOptionPane.INFORMATION_MESSAGE;
	
	private JLabel iconLb = new JLabel();
	private JTextArea messageTa = new JTextArea();

	public MessageDialog() {
		super((Frame) null, true);
	}

	public MessageDialog(String title) {
		super((Frame) null, title, true);
	}

	public MessageDialog(String title, int messageType) {
		super((Frame) null, title, true);
		this.messageType = messageType;
	}

	@Override
	protected void initInterface() {
		super.initInterface();
		setMessageType(messageType);
	}

	@Override
	public JPanel getCenterPanel() {
		messageTa.setEditable(false);
		messageTa.setAutoscrolls(true);
		messageTa.setLineWrap(true);
		messageTa.setColumns(50);

		JPanel messagePanel = new JPanel();
		messagePanel.add(iconLb);
		messagePanel.add(messageTa);
		messagePanel.setBorder(new TitledBorder(""));
		return messagePanel;
	}

	public void setIcon(Icon icon) {
		iconLb.setIcon(icon);
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
		if (messageType == JOptionPane.ERROR_MESSAGE)
			iconLb.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
		else if (messageType == JOptionPane.INFORMATION_MESSAGE)
			iconLb.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
		else if (messageType == JOptionPane.WARNING_MESSAGE)
			iconLb.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		else if (messageType == JOptionPane.QUESTION_MESSAGE)
			iconLb.setIcon(UIManager.getIcon("OptionPane.questionIcon"));
		else if (messageType == LOADING_MESSAGE) {
			ImageIcon icon = ImageUtil.getImageIcon("/loading.gif");
			iconLb.setIcon(icon);
		} else
			iconLb.setIcon(null);
		if (cancelButton != null)
			cancelButton.setVisible(messageType == JOptionPane.QUESTION_MESSAGE);
	}

	public String getMessage() {
		return messageTa.getText();
	}

	public void setMessage(String message) {
		messageTa.setText(message);
		this.pack();
	}

	public void setMessage(String message, int messageType) {
		setMessage(message);
		setMessageType(messageType);
	}

	public JTextArea getMessageTa() {
		return messageTa;
	}

}
