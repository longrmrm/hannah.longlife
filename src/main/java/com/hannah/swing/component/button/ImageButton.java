package com.hannah.swing.component.button;

import javax.swing.*;

public class ImageButton extends JButton {

	private static final long serialVersionUID = -2893238674060687617L;
	
	public ImageButton() {
		super();
	}
	
	public ImageButton(String text) {
		this();
		super.setText(text);
	}

	public ImageButton(String text, Icon icon) {
		this(text);
		super.setIcon(icon);
	}
}
