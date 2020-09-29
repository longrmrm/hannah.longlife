package com.hannah.model;

import com.hannah.common.util.ObjectUtil;

import java.io.Serializable;

public class BaseElement implements Serializable {

	private static final long serialVersionUID = 2190901861549934566L;

	private String code;
	private String name;
	private String parentCode;
	
	public BaseElement() {
	}
	
	public BaseElement(String code, String name) {
		this();
		setCode(code);
		setName(name);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String toString() {
		if (code == null || code.equals(""))
			return name;
		return code + " " + name;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		BaseElement o = (BaseElement) obj;
		if (code != null)
			return ObjectUtil.compareEqual(code, o.getCode());
		else
			return ObjectUtil.compareEqual(name, o.getName());
	}

}
