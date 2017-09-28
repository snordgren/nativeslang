package com.nativeslang.sql;

public class SQLBuilder {
	public static Order ascending(String column) {
		return new Order(column, OrderType.ASC);
	}

	public static Order descending(String column) {
		return new Order(column, OrderType.DESC);
	}

	public static Column bigint(String name) {
		return new Column(name, new BigInt());
	}

	public static Equals isEqual(String column, Object value) {
		return new Equals(column, value);
	}

	public static Into into(String column, Object value) {
		return new Into(column, value);
	}

	public static Table table(String name, Column... columns) {
		return new Table(name, columns);
	}

	public static String value(Object value) {
		if (value == null) {
			throw new NullPointerException();
		} else if (value instanceof String) {
			return "'" + value + "'";
		} else if (value instanceof Number) {
			return value.toString();
		} else {
			throw new UnsupportedOperationException(value.getClass().getSimpleName());
		}
	}

	public static Column varchar(String name, int length) {
		return new Column(name, new VarChar(length));
	}
}
