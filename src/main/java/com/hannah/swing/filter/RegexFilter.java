package com.hannah.swing.filter;

public class RegexFilter extends AbstractFilter {

	public RegexFilter() {
	}

	public RegexFilter(Object filterObject) {
		super(filterObject);
	}

	@Override
	public boolean accept(Object obj) {
		if (obj == null)
			return false;
		return obj.toString().toLowerCase()
				.matches(filterObject == null ? "" : filterObject.toString().toLowerCase());
	}

}
