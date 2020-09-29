package com.hannah.swing.component.table;

import com.hannah.swing.component.DateChooser;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.text.DateFormat;
import java.util.Date;

public class DateCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1133284286840889770L;

	private DateChooser dateChooser;

	public DateCellEditor() {
		dateChooser = new DateChooser();
	}

	public DateCellEditor(Date date, boolean showHour) {
		dateChooser = new DateChooser(date, showHour);
	}

	public void setDateFormat(DateFormat format) {
		dateChooser.setDateFormat(format);
	}

	@Override
	public Object getCellEditorValue() {
		return dateChooser.getDate();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		dateChooser.setDate(value == null ? new Date() : (Date) value);
		return dateChooser;
	}

}
