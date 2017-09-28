package com.nativeslang.sql;

public class BigInt extends ColumnType {
	@Override
	public String compile() {
		return "bigint";
	}
}
