package com.hannah.swing.component;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class LimitativeDocument extends PlainDocument {

	private static final long serialVersionUID = -8552787767350602663L;

	private JTextComponent textComponent;
	private int maxLine = 100;

	public LimitativeDocument(JTextComponent tc, int maxLine) {
		textComponent = tc;
		this.maxLine = maxLine;
	}

	public LimitativeDocument(JTextComponent tc) {
		textComponent = tc;
	}

	public int getMaxLine() {
		return maxLine;
	}

	public void setMaxLine(int maxLine) {
		this.maxLine = maxLine;
	}

	public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
		String value = textComponent.getText();
		int overrun = 0;
		if (value != null && value.indexOf('\n') >= 0 && value.split("\n").length >= maxLine) {
			overrun = value.indexOf('\n') + 1;
			super.remove(0, overrun);
		}
		super.insertString(offset - overrun, s, attributeSet);
	}

}
