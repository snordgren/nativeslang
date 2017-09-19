package com.northerndroid.nativeslang.sql;

public class BigInt extends ColumnType {
	@Override
	public String compile() {
		return "bigint";
	}
}
