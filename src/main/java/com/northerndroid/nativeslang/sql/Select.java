package com.northerndroid.nativeslang.sql;

import java.util.Arrays;

public class Select {
	private final String[] columns;
	private final String table;
	private final Order[] orderBy;

	public Select(String table, String[] columns, Order[] orderBy) {
		this.table = table;
		this.columns = columns;
		this.orderBy = orderBy;
	}

	private String compile() {
		String columnList = Arrays.stream(columns)
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
		return "select " + columnList + " from " + table;
	}

	public Select orderBy(Order... orderBy) {
		return new Select(table, columns, orderBy);
	}

	public String where(Condition... conditions) {
		String conditionList = Arrays.stream(conditions)
				.map(Condition::compile)
				.reduce((a, b) -> a + " and " + b)
				.orElse("");
		String order = Arrays.stream(orderBy)
				.map(a -> a.getColumn() + " " + a.getOrderType().name())
				.reduce((a, b) -> a + ", " + b)
				.map(a -> " order by " + a + " ")
				.orElse("");
		return compile() + " where " + conditionList + order + ";";
	}
}
