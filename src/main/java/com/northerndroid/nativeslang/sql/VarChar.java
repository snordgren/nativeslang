package com.northerndroid.nativeslang.sql;

public class VarChar extends ColumnType {
	private final int length;

	public VarChar(int length) {
		this.length = length;
	}

	@Override
	public String compile() {
		return "varchar(" + length + ")";
	}
}
