package com.hannah.swing.component.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * @author longrm
 * @date 2012-4-14
 */
public class BasicDialog extends JDialog {

	private static final long serialVersionUID = 6430624305815866374L;

	private boolean ok = false;

	public BasicDialog() {
		super();
	}

	public BasicDialog(Frame owner) {
		super(owner);
	}

	public BasicDialog(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public BasicDialog(Frame owner, String title) {
		super(owner, title);
	}

	public BasicDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public void setMaxSizeWindow() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.getSize().width, screenSize.getSize().height - insets.bottom);
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public void registerEscapeKeyAction() {
		registerEscapeKeyAction(false);
	}

	/**
	 * 注册退出快捷键Esc
	 * @param isDispose 退出时是否dispose当前窗体
	 */
	public void registerEscapeKeyAction(final boolean isDispose) {
		ActionListener searchListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isDispose)
					BasicDialog.this.dispose();
				else
					BasicDialog.this.setVisible(false);
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.VK_UNDEFINED, false);
		this.getRootPane().registerKeyboardAction(searchListener, ks, JComponent.WHEN_FOCUSED);
	}

}
