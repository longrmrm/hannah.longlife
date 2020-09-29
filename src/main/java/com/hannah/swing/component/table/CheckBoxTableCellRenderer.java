package com.hannah.swing.component.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CheckBoxTableCellRenderer extends JCheckBox implements TableCellRenderer {
	
	private static final long serialVersionUID = -8095422715674299362L;

	public CheckBoxTableCellRenderer() {
		this(null);
	}

	public CheckBoxTableCellRenderer(String text) {
		super(text);
		this.setBorderPainted(true);
		this.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		return this;
	}

}
