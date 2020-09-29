package com.hannah.swing.component;

import com.hannah.common.util.DateUtil;
import com.hannah.common.util.StringUtil;
import com.hannah.swing.component.dialog.ConfirmDialog;
import com.hannah.swing.component.panel.FontPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class LogTextArea extends JTextArea {

	private static final long serialVersionUID = 4755627182909189454L;

	private boolean logTime = true;

	private File logFile;
	private FileOutputStream output;

	private static ThreadLocal<String> prefixLocal = new ThreadLocal<String>();

	public LogTextArea() {
		this(30, 50);
	}

	public LogTextArea(int rows, int columns) {
		super(rows, columns);
		super.setEditable(false);

		registerPopMenu();
	}

	private void registerPopMenu() {
		JMenuItem setFontMenuItem = new JMenuItem("Set Text Font ......");
		setFontMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FontPanel fontPanel = new FontPanel(getFont());
				ConfirmDialog d = new ConfirmDialog((JFrame) null, "Set Text Font ......", true);
				d.setCenterPanel(fontPanel);
				d.registerEscapeKeyAction(true);
				d.setVisible(true);
				if (d.isOk())
					setFont(fontPanel.getSelectedFont());
			}
		});

		JMenuItem clearMenuItem = new JMenuItem("Clear Log");
		clearMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});

		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(setFontMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(clearMenuItem);

		add(popupMenu);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3)
					popupMenu.show(LogTextArea.this, e.getX(), e.getY());
			}
		});
	}

	public void clear() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setText(null);
			}
		});
	}

	/**
	 * 设置当前线程的输出前缀
	 * @param prefix
	 */
	public void setCurrentPrefix(String prefix) {
		prefixLocal.set(prefix);
	}

	public synchronized void info(final String text) {
		info(null, text);
	}

	public synchronized void info(final String firstText, final String text) {
		info(firstText, text, logTime);
	}

	public synchronized void info(final String firstText, final String text, final boolean logTime) {
		final String prefix = prefixLocal.get();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String message = "";
				if (logTime)
					message += "【" + DateUtil.dateToSsString(new Date()) + "】";
				message += StringUtil.toText(firstText) + StringUtil.toText(prefix) + StringUtil.toText(text)
						+ System.getProperty("line.separator");

				append(message);
				setCaretPosition(getText().length());

				if (logFile != null) {
					try {
						logToFile(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void logToFile(final String text) throws IOException {
		if (output == null)
			output = new FileOutputStream(logFile, true);

		output.write(text.getBytes());
		output.flush();
	}

	public void success(final String text) {
		info(" +++ ", text);
	}

	public void error(final String text) {
		info(" --- ", text);
	}

	public void warn(final String text) {
		info(" ~~~ ", text);
	}

	public void separator() {
		separator(60);
	}

	public void separator(int length) {
		separator(length, ">");
	}

	public void separator(int length, String str) {
		String firstText = "";
		for (int i = 0; i < length; i++)
			firstText += str;

		info(firstText, "");
	}

	public void blankLine() {
		blankLine(1);
	}

	public void blankLine(int lineCount) {
		String text = "";
		for (int i = 0; i < lineCount; i++)
			text += System.getProperty("line.separator");

		info(text, "", false);
	}

	public boolean isLogTime() {
		return logTime;
	}

	public void setLogTime(boolean logTime) {
		this.logTime = logTime;
	}

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		if (!logFile.exists()) {
			try {
				if (logFile.createNewFile())
					this.logFile = logFile;
				else
					this.logFile = null;
			} catch (IOException e) {
				e.printStackTrace();
				this.logFile = null;
			}
		} else
			this.logFile = logFile;

		try {
			String text = System.getProperty("line.separator") + System.getProperty("line.separator");
			for (int i = 0; i < 100; i++)
				text += "-";
			logToFile(text + System.getProperty("line.separator"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
