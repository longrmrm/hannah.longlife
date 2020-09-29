package com.hannah.swing.component.dialog;

import com.hannah.swing.util.AbstractInvokeHandler;
import com.hannah.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * dialog with ok&cancle button
 * @author longrm
 * @date 2013-4-22
 */
public class ConfirmDialog extends BasicDialog {

	private static final long serialVersionUID = -5041444417269795121L;

	private JPanel centerPanel;

	protected JPanel buttonPanel;
	protected JButton okButton;
	protected JButton cancelButton;

	public ConfirmDialog() {
		super();
		initInterface();
	}

	public ConfirmDialog(Frame owner) {
		super(owner);
		initInterface();
	}

	public ConfirmDialog(Frame owner, boolean modal) {
		super(owner, modal);
		initInterface();
	}

	public ConfirmDialog(Frame owner, String title) {
		super(owner, title);
		initInterface();
	}

	public ConfirmDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		initInterface();
		
	}

	protected void initInterface() {
		initButtonPanel();

		this.setLayout(new BorderLayout());
		JPanel centerPanel = getCenterPanel();
		if (centerPanel != null)
			this.add(centerPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(getOwner());
	}

	public JPanel getCenterPanel() {
		return centerPanel;
	}

	public void setCenterPanel(JPanel panel) {
		this.centerPanel = panel;
		this.add(panel, BorderLayout.CENTER);
		this.pack();
		this.setLocationRelativeTo(getOwner());
	}

	protected void initButtonPanel() {
		okButton = new JButton("确定");
		cancelButton = new JButton("取消");
		okButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
	}

	private ActionListener buttonListener = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			UiUtil.asyncInvoke(new AbstractInvokeHandler<Boolean>() {

				@Override
				public void before() {
					super.before();
					UiUtil.setComponentsEnabled(ConfirmDialog.this, false, true);
				};

				@Override
				public Boolean execute() throws Exception {
					if (e.getSource() == okButton) {
						return checkOk();
					} else
						return false;
				}

				@Override
				public void success(Boolean result) {
					if (e.getSource() == okButton) {
						setOk(result);
						if (result)
							ConfirmDialog.this.dispose();
					} else
						ConfirmDialog.this.dispose();
				}

				@Override
				public void after() {
					super.after();
					UiUtil.setComponentsEnabled(ConfirmDialog.this, true, true);
				};

			});
		}
	};

	protected boolean checkOk() throws Exception {
		return true;
	}

	public void setButtonText(int buttonIndex, String text) {
		if (buttonIndex == 1)
			okButton.setText(text);
		else if (buttonIndex == 2)
			cancelButton.setText(text);
	}

}
