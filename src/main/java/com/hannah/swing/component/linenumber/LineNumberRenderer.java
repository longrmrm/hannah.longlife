package com.hannah.swing.component.linenumber;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import java.awt.*;

/**
 * @author longrm
 * @date 2013-4-11
 */
public class LineNumberRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 4206882362411537157L;

	private JComponent component; // 需要显示Line Numbers的组件

	public LineNumberRenderer(JTable table) {
		component = table;
	}

	public LineNumberRenderer(JTextArea textArea) {
		component = textArea;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (component instanceof JTable) {
			JTableHeader tableHeader = ((JTable) component).getTableHeader();
			setForeground(tableHeader.getForeground());
			setBackground(new Color(230, 230, 230));
			// 设置border
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(230, 230, 230), Color.LIGHT_GRAY));
			// 设置字体、文本和对齐方式
			setHorizontalAlignment(RIGHT);
			setText(value + "  ");
			setFont(new Font("微软雅黑", Font.PLAIN, 12));
		} 
		else if (component instanceof JTextArea) {
			setForeground(Color.gray);
			setBackground(component.getBackground());
			// 设置字体、文本和对齐方式
			setHorizontalAlignment(RIGHT);
			setText(value + "   ");
			setFont(new Font("Times New Roman", Font.PLAIN, 12));
			// 当选取单元格时，在Line Number上设置成选取颜色
			if (isSelected(row))
				setBackground(new Color(210, 210, 210));
		}
		return this;
	}

	/**
	 * 判断当前行是否选中
	 * @param row
	 * @return
	 */
	private boolean isSelected(int row) {
		if (component instanceof JTable) {
			int[] selectedRows = ((JTable) component).getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++)
				if (selectedRows[i] == row)
					return true;
		} else if (component instanceof JTextArea) {
			JTextArea textArea = (JTextArea) component;
			try {
				Rectangle recStart = textArea.modelToView(textArea.getSelectionStart());
				Rectangle recEnd = textArea.modelToView(textArea.getSelectionEnd());
				if (row >= recStart.y / recStart.height && row <= recEnd.y / recEnd.height)
					return true;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}