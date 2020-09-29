package com.hannah.swing.component.table;

import com.hannah.common.util.DateUtil;
import com.hannah.swing.util.TableUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * basic table cell renderer base on basic table model
 * @author longrm
 * @date 2012-6-19
 */
public class BasicTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -7781138872191390696L;

	public static final Color BACKGROUND_COLOR1 = new Color(250, 250, 250);

	public static final Color BACKGROUND_COLOR2 = new Color(240, 240, 240);

	public BasicTableCellRenderer() {
	}

	public Object getDisplayValue(JTable table, Object value, int row, int column) {
		BasicTableHeader header = getTableHeader(table, column);
		// 设置自定义显示格式
		if (value == null)
			return value;
		else if (header != null && header.getFormat() != null)
			value = header.getFormat().format(value);
		else if (value instanceof Date)
			value = DateUtil.dateToDdString((Date) value);
		// else if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
		// 	NumberFormat numberFormat = NumberFormat.getInstance();
		// 	numberFormat.setMinimumFractionDigits(2);
		// 	numberFormat.setMaximumFractionDigits(2);
		// 	value = numberFormat.format(value);
		// }
		return value;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		if (row % 2 == 0)
			setBackground(BACKGROUND_COLOR1);
		else
			setBackground(BACKGROUND_COLOR2);

		BasicTableHeader header = getTableHeader(table, column);
		// 设置Alignment
		if (header != null && header.getAlignment() != -1)
			setHorizontalAlignment(header.getAlignment());
		else if (value instanceof Date)
			setHorizontalAlignment(SwingConstants.CENTER);
		else if (value instanceof Integer)
			setHorizontalAlignment(SwingConstants.RIGHT);
		else if (value instanceof BigDecimal)
			setHorizontalAlignment(SwingConstants.RIGHT);
		else if (value instanceof Double)
			setHorizontalAlignment(SwingConstants.RIGHT);
		else if (value instanceof Float)
			setHorizontalAlignment(SwingConstants.RIGHT);
		else
			setHorizontalAlignment(SwingConstants.LEFT);

		value = getDisplayValue(table, value, row, column);
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

	protected BasicTableHeader getTableHeader(JTable table, int column) {
		List<BasicTableHeader> headerList = null;
		if (table.getModel() instanceof BasicTableModel)
			headerList = ((BasicTableModel) table.getModel()).getHeaderList();

		BasicTableHeader header = null;
		if (headerList != null)
			header = TableUtil.getTableHeaderByHeaderValue(headerList, table.getColumnName(column));
		return header;
	}

}
