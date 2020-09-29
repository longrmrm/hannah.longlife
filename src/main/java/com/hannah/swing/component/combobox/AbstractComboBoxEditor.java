package com.hannah.swing.component.combobox;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;

/**
 * @ClassName: AbstractComboBoxEditor
 * @Description: comboBoxEditor，文本内容改变时触发fireTextChanged方法
 * @date: 2011-4-15 下午03:22:54
 * @version: V1.0
 * @since: 1.0
 * @author: longrm
 * @modify:
 */
public abstract class AbstractComboBoxEditor implements ComboBoxEditor, DocumentListener {

	public JTextField text;

	private volatile boolean filtering = false;
	private volatile boolean setting = false;

	public AbstractComboBoxEditor() {
		text = new JTextField(15) {
			protected void processFocusEvent(FocusEvent e) {
				// 更改FocusEvent的temporary属性，使得comboboxui不隐藏popup
				if (e.getID() == FocusEvent.FOCUS_LOST)
					super.processFocusEvent(new FocusEvent((Component) e.getSource(), e.getID(), true, e
							.getOppositeComponent()));
				else
					super.processFocusEvent(e);
			}
		};
		text.getDocument().addDocumentListener(this);
	}

	public Component getEditorComponent() {
		return text;
	}

	public void setItem(Object item) {
		if (filtering)
			return;

		setting = true;
		text.setText(getShowText(item));
		setting = false;
	}

	protected String getShowText(Object item) {
		return (item == null) ? "" : item.toString();
	}

	public Object getItem() {
		return text.getText();
	}

	public void selectAll() {
		text.selectAll();
	}

	public void addActionListener(ActionListener l) {
		text.addActionListener(l);
	}

	public void removeActionListener(ActionListener l) {
		text.removeActionListener(l);
	}

	public void insertUpdate(DocumentEvent e) {
		try {
			handleChange();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void removeUpdate(DocumentEvent e) {
		try {
			handleChange();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void changedUpdate(DocumentEvent e) {
		try {
			handleChange();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void handleChange() {
		if (setting)
			return;

		filtering = true;
		fireTextChanged();
		filtering = false;
	}

	// fire editor that text changed
	protected abstract void fireTextChanged();

}