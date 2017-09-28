package com.nativeslang.sql;

public abstract class Condition {
	private final String column;

	public Condition(String column) {
		this.column = column;
	}

	public abstract String compile();

	public String getColumnName() {
		return column;
	}
}
