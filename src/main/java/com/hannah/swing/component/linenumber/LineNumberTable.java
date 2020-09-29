package com.hannah.swing.component.linenumber;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 显示行号，调用JScrollPane.setRowHeaderView方法即可，目前支持JTable和JTextArea
 * @author longrm
 * @date 2013-4-12
 */
public class LineNumberTable extends JTable {

	private static final long serialVersionUID = -647831720918896668L;

	public LineNumberTable(final JTable table) {
		setEnabled(false);
		setModel(new LineNumberTableModel(this, table));
		setRowHeight(table.getRowHeight());
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setDefaultRenderer(Object.class, new LineNumberRenderer(table));

		table.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// table行高改变时，LineNumberTable行高随之变化
				if ("rowHeight".equals(evt.getPropertyName()))
					LineNumberTable.this.setRowHeight(table.getRowHeight());
				// model改变时，重新注册监听
				else if ("model".equals(evt.getPropertyName()))
					addTableModelListener(table);
			}
		});
		// 监听table选中事件，更新LineNumber选中状态
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				LineNumberTable.this.repaint();
			}
		});
		addTableModelListener(table);
	}

	// 监听tablemodel改变事件，增删查改时刷新行号
	private void addTableModelListener(final JTable table) {
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				((LineNumberTableModel) LineNumberTable.this.getModel()).fireTableDataChanged();
			}
		});
	}

	public LineNumberTable(final JTextArea textArea) {
		setEnabled(false);
		setModel(new LineNumberTableModel(this, textArea));
		// 行高和textArea的字体大小高度一致
		setRowHeight(textArea.getFontMetrics(textArea.getFont()).getHeight());
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setDefaultRenderer(Object.class, new LineNumberRenderer(textArea));
		setShowGrid(false);

		// 光标位置改变时，刷新行号
		textArea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				((LineNumberTableModel) LineNumberTable.this.getModel()).fireTableDataChanged();
			}
		});
		// 字体改变时，更新行高
		textArea.addPropertyChangeListener("font", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				LineNumberTable.this.setRowHeight(textArea.getFontMetrics(textArea.getFont()).getHeight());
			}
		});
	}

	/**
	 * 设置行号显示宽度
	 * @param rowCount 总行数
	 */
	public void setLineNumberWidth(int rowCount) {
		int log10 = (int) Math.log10(rowCount);
		int width = log10 == 0 ? 27 : (log10 + 3) * 8;
		getColumnModel().getColumn(0).setPreferredWidth(width);
		setPreferredScrollableViewportSize(new Dimension(width, 0));
	}

}