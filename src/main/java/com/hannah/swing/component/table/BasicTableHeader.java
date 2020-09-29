package com.hannah.swing.component.table;

import java.text.Format;

public class BasicTableHeader {

	private String identifier = null;
	private String headerValue = null;
	private int alignment = -1; // 默认值为-1，标识未设定
	private Format format = null;
	private boolean hide = false;
	private boolean editable = false;
	private int width = -1;

	public BasicTableHeader(String identifier) {
		setIdentifier(identifier);
		setHeaderValue(identifier);
	}

	public BasicTableHeader(String identifier, String headerValue) {
		setIdentifier(identifier);
		setHeaderValue(headerValue);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public Format getFormat() {
		return format;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return headerValue;
	}

}
