package com.northerndroid.nativeslang.sql;

public class Equals extends Condition {
	private final Object value;

	public Equals(String column, Object value) {
		super(column);
		this.value = value;
	}

	@Override
	public String compile() {
		return getColumnName() + "=" + SQLBuilder.value(value);
	}
}
