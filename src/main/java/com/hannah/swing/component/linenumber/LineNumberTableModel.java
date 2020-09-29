package com.hannah.swing.component.linenumber;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import java.awt.*;

/**
 * 用于显示Line Numbers的TableModel
 * @author longrm
 * @date 2013-4-11
 */
public class LineNumberTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1862049733141986744L;

	private JComponent component; // 需要显示Line Numbers的组件

	private LineNumberTable lineNumberTable;

	public LineNumberTableModel(LineNumberTable lineNumberTable, JTable table) {
		this.lineNumberTable = lineNumberTable;
		component = table;
	}

	public LineNumberTableModel(LineNumberTable lineNumberTable, JTextArea textArea) {
		this.lineNumberTable = lineNumberTable;
		component = textArea;
	}

	@Override
	public int getRowCount() {
		int rowCount = 1;
		if (component instanceof JTable)
			rowCount = ((JTable) component).getRowCount();
		else if (component instanceof JTextArea) {
			JTextArea textArea = (JTextArea) component;
			try {
				int length = textArea.getDocument().getLength();
				Rectangle rec = textArea.modelToView(length);
				rowCount = rec == null ? 1 : (rec.y / rec.height + 1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		// 根据行数设置行号显示宽度
		lineNumberTable.setLineNumberWidth(rowCount);
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int row, int column) {
		return row + 1;
	}
}