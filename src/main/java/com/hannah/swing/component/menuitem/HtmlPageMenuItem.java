package com.hannah.swing.component.menuitem;

import com.hannah.swing.component.panel.HtmlPagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HtmlPageMenuItem extends JMenuItem {

	private static final long serialVersionUID = -96451611327381163L;

	private ActionListener listener;
	private Dimension frameSize = new Dimension(600, 600);

	public HtmlPageMenuItem(String url) {
		this(null, null, url);
	}

	public HtmlPageMenuItem(Icon icon, String url) {
		this(null, icon, url);
	}

	public HtmlPageMenuItem(String text, String url) {
		this(text, null, url);
	}

	public HtmlPageMenuItem(String text, Icon icon, String url) {
		super(text, icon);
		setPage(url);
	}

	public void setPage(final String url) {
		if (listener != null)
			this.removeActionListener(listener);

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HtmlPagePanel pagePanel = new HtmlPagePanel();
				JFrame f = new JFrame(e.getActionCommand());
				f.add(pagePanel);
				f.setSize(frameSize);
				f.setLocationRelativeTo(null);
				f.setVisible(true);

				pagePanel.setPage(url);
			}
		};
		this.addActionListener(listener);
	}

	public Dimension getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(Dimension frameSize) {
		this.frameSize = frameSize;
	}

}
