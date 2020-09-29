package com.hannah.swing.component.button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author longrm
 * @date 2013-8-22
 */
public class MenuToolBar extends JToolBar {

	private static final long serialVersionUID = 645868470893541329L;

	private AbstractButton activeButton;
	private Color originColor;
	private Color activeColor = Color.WHITE;

	private Color underLineColor = Color.RED;
	private int thickness = 2;

	public MenuToolBar() {
		setFloatable(false);
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		super.addImpl(comp, constraints, index);
		if (comp instanceof AbstractButton) {
			final AbstractButton button = (AbstractButton) comp;
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setActiveButton(button);
				}
			});
		}
	}

	public AbstractButton getActiveButton() {
		return activeButton;
	}

	public void setActiveButton(AbstractButton activeButton) {
		if (this.activeButton == activeButton)
			return;

		if (this.activeButton != null)
			this.activeButton.setForeground(originColor);
		this.activeButton = activeButton;
		this.originColor = activeButton.getForeground();
		activeButton.setForeground(activeColor);
		repaint();
	}

	public Color getActiveColor() {
		return activeColor;
	}

	public void setActiveColor(Color activeColor) {
		this.activeColor = activeColor;
	}

	public Color getUnderLineColor() {
		return underLineColor;
	}

	public void setUnderLineColor(Color underLineColor) {
		this.underLineColor = underLineColor;
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (activeButton != null) {
			int pointX = activeButton.getX();
			int pointY = getHeight() - thickness;

			g.setColor(underLineColor);
			g.fillRect(pointX, pointY, activeButton.getWidth(), thickness);
		}
	}

}
