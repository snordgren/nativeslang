package com.nativeslang.sql;

public class Into {
	private final String column;
	private final Object value;

	public Into(String column, Object value) {
		this.column = column;
		this.value = value;
	}

	public String getColumnName() {
		return column;
	}

	public Object getValue() {
		return value;
	}
}
