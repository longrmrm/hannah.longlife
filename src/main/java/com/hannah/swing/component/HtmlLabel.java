package com.hannah.swing.component;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author longrm
 * @date 2013-8-21
 */
public class HtmlLabel extends JLabel {

	private static final long serialVersionUID = 3658777417050992896L;

	private Color originColor;

	private Color underLineColor;

	private MouseListener listener;

	public HtmlLabel() {
		super();
		this.setForeground(Color.blue);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				originColor = getForeground();
				HtmlLabel.this.setForeground(Color.red);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				HtmlLabel.this.setForeground(originColor);
			}
		});
	}

	public HtmlLabel(String text) {
		this();
		setText(text);
	}

	public Color getUnderLineColor() {
		return underLineColor;
	}

	public void setUnderLineColor(Color underLineColor) {
		this.underLineColor = underLineColor;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Rectangle r = g.getClipBounds();
		int xoff = 0, yoff = 0, pointX = 0, pointY = 0, point2X = 0, point2Y = 0;

		// 根据border设置，计算出下划线的起止Point
		Border border = this.getBorder();
		if (border != null) {
			Insets insets = border.getBorderInsets(this);
			if (insets != null) {
				xoff = insets.left;
				yoff = insets.bottom;
			}
		}
		FontMetrics metrics = getFontMetrics(getFont());
		pointX = xoff;
		pointY = point2Y = r.height - yoff - metrics.getDescent() * 3 / 4;
		point2X = pointX + metrics.stringWidth(getText());
		if (underLineColor != null)
			g.setColor(underLineColor);
		g.drawLine(pointX, pointY, point2X, point2Y);
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
			this.removeMouseListener(listener);

		listener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(uri);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		this.addMouseListener(listener);
	}

}
