package com.northerndroid.nativeslang.sql;

public class Column {
	private final String name;
	private final ColumnType columnType;

	public Column(String name, ColumnType columnType) {
		this.name = name;
		this.columnType = columnType;
	}

	public String compile() {
		return name + " " + columnType.compile();
	}

	public String getName() {
		return name;
	}

	public Column primaryKey() {
		return new Column(name, new Generated(columnType));
	}
}
