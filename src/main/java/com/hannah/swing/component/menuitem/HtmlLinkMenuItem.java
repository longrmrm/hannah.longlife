package com.hannah.swing.component.menuitem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HtmlLinkMenuItem extends JMenuItem {

	private static final long serialVersionUID = -96451611327381163L;

	private ActionListener listener;

	public HtmlLinkMenuItem(String url) {
		this(null, null, url);
	}

	public HtmlLinkMenuItem(Icon icon, String url) {
		this(null, icon, url);
	}

	public HtmlLinkMenuItem(String text, String url) {
		this(text, null, url);
	}

	public HtmlLinkMenuItem(String text, Icon icon, String url) {
		super(text, icon);
		setLink(url);
	}

	public void setLink(String url) {
		try {
			setLink(new URI(url));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void setLink(final URI uri) {
		if (listener != null)
			this.removeActionListener(listener);

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(uri);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		this.addActionListener(listener);
	}

}
