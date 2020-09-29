package com.hannah.swing.filter;

public abstract class AbstractFilter implements Filter {

	protected Object filterObject;

	public AbstractFilter() {
	}

	public AbstractFilter(Object filterObject) {
		this.filterObject = filterObject;
	}

	public Object getFilterObject() {
		return filterObject;
	}

	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	@Override
	public abstract boolean accept(Object obj);

}
