package com.hannah.swing.filter;

public interface Filter {

	public Object getFilterObject();

	public void setFilterObject(Object filterObject);

	public boolean accept(Object obj);

}
