package com.hannah.swing.component.dialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * thread waiting dialog
 * @author longrm
 * @date 2012-4-5
 */
public class ThreadMessageDialog extends MessageDialog {

	private static final long serialVersionUID = -8437570525425340650L;

	private JButton abortButton;

	private Runnable runnable;
	private Thread thread;

	public ThreadMessageDialog() {
		super("", MessageDialog.LOADING_MESSAGE);
	}

	public ThreadMessageDialog(String title) {
		super(title, MessageDialog.LOADING_MESSAGE);
	}

	public ThreadMessageDialog(String title, int messageType) {
		super(title, messageType);
	}

	@Override
	protected void initButtonPanel() {
		abortButton = new JButton("终止");
		buttonPanel.add(abortButton);

		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doAbort();
			}
		});
	}

	@Override
	public void setMessageType(int messageType) {
		super.setMessageType(messageType);
		if (messageType != MessageDialog.LOADING_MESSAGE)
			setButtonText("确定");
		else
			setButtonText("终止");
	}

	public void setButtonText(String text) {
		setButtonText(0, text);
	}

	@Override
	public void setButtonText(int buttonIndex, String text) {
		abortButton.setText(text);
	}

	public void setButtonVisible(boolean aFlag) {
		abortButton.setVisible(aFlag);
	}

	protected void doAbort() {
		if (thread != null && thread.isAlive()) {
			thread.stop();
			setOk(false);
		} else if (thread == null)
			setOk(false);
		else
			setOk(true);
		super.dispose();
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
		thread = new Thread(runnable);
	}

	public void startThread() {
		if (thread != null && !thread.isAlive())
			thread.start();
	}

}
