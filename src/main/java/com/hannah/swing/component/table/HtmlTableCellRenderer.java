package com.hannah.swing.component.table;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HtmlTableCellRenderer extends BasicTableCellRenderer {

	private static final long serialVersionUID = -5108235112991219804L;

	private Color originColor;
	private Color underLineColor;

	public HtmlTableCellRenderer() {
		super();
		this.setOpaque(true);
		this.setForeground(Color.blue);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				originColor = getForeground();
				HtmlTableCellRenderer.this.setForeground(Color.red);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				HtmlTableCellRenderer.this.setForeground(originColor);
			}
		});
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
		pointX = xoff;
		pointY = point2Y = r.height - yoff - getFontMetrics(getFont()).getDescent();
		point2X = pointX + getFontMetrics(getFont()).stringWidth(getText());
		if (underLineColor != null)
			g.setColor(underLineColor);
		g.drawLine(pointX, pointY, point2X, point2Y);
	}

}
