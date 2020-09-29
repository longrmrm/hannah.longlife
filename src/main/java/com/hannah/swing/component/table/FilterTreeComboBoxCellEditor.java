package com.hannah.swing.component.table;

import com.hannah.swing.component.combobox.FilterTreeComboBox;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class FilterTreeComboBoxCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 4706968907604769043L;

	protected FilterTreeComboBox treeCb;
	private JTable table;

	public FilterTreeComboBoxCellEditor(JTree tree) {
		treeCb = new FilterTreeComboBox(tree);
		treeCb.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (treeCb.getSelectedObject() != null) {
						table.requestFocusInWindow();
						table.dispatchEvent(e);
					}
				}
			}
		});
	}

	@Override
	public boolean isCellEditable(EventObject e) {
		if (e instanceof MouseEvent)
			return ((MouseEvent) e).getClickCount() >= 2;
		return true;
	}

	@Override
	public Object getCellEditorValue() {
		return treeCb.getSelectedObject();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.table = table;
		treeCb.getEditor().setItem(value);
		treeCb.filter();
		return treeCb;
	}

}
