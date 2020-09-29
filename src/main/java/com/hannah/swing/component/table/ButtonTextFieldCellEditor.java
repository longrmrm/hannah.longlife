package com.hannah.swing.component.table;

import com.hannah.swing.component.ButtonTextField;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public abstract class ButtonTextFieldCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1L;

	private ButtonTextField buttonTf;

	public ButtonTextFieldCellEditor() {
		buttonTf = new ButtonTextField() {
			@Override
			public void handleClick(ButtonTextField buttonTextField) {
				ButtonTextFieldCellEditor.this.handleClick(buttonTf);
				ButtonTextFieldCellEditor.this.stopCellEditing();
			}
		};
		buttonTf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleInput(buttonTf);
			}
		});
	}

	public ButtonTextFieldCellEditor(boolean readOnly) {
		this();
		buttonTf.setEditable(!readOnly);
	}

	public abstract void handleClick(ButtonTextField buttonTextField);

	public void handleInput(ButtonTextField buttonTextField) {
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= 2;
		}
		return true;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
			int row, int column) {
		buttonTf.setText(value == null ? "" : value.toString());
		return buttonTf;
	}

	@Override
	public Object getCellEditorValue() {
		return buttonTf.getText();
	}

}
