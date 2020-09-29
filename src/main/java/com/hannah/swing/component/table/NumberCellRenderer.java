package com.hannah.swing.component.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

public class NumberCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 559351128288863244L;

	private NumberFormat numberFormat;

	public NumberCellRenderer(NumberFormat numberformat) {
		numberFormat = null;
		setHorizontalAlignment(JLabel.RIGHT);
		numberFormat = numberformat;
	}

	public NumberCellRenderer() {
		numberFormat = null;
		setHorizontalAlignment(JLabel.RIGHT);
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		if (row % 2 == 0)
			setBackground(new Color(250, 250, 250));
		else
			setBackground(new Color(240, 240, 240));
		value = numberFormat.format(value);
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}