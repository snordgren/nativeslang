package com.nativeslang.sql;

import java.util.Arrays;
import java.util.Optional;

public class Select {
	private final String[] columns;
	private final String table;
	private final Order[] orderBy;
	private final Optional<Integer> topCount;

	public Select(String table,
			String[] columns,
			Order[] orderBy) {
		this(table, columns, orderBy, Optional.empty());
	}

	public Select(String table,
			String[] columns,
			Order[] orderBy,
			int topCount) {
		this(table, columns, orderBy, Optional.of(topCount));
	}

	public Select(String table,
			String[] columns,
			Order[] orderBy,
			Optional<Integer> topCount) {
		this.table = table;
		this.columns = columns;
		this.orderBy = orderBy;
		this.topCount = topCount;
	}

	public Select orderBy(Order... orderBy) {
		return new Select(table, columns, orderBy, topCount);
	}

	public String where(Condition... conditions) {
		String top = topCount.map(a -> "top " + a + " ").orElse("");
		String columnList = Arrays.stream(columns)
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
		String select = "select " + columnList + " from " + table;
		String conditionList = Arrays.stream(conditions)
				.map(Condition::compile)
				.reduce((a, b) -> a + " and " + b)
				.orElse("");
		String order = Arrays.stream(orderBy)
				.map(a -> a.getColumn() + " " + a.getOrderType().name())
				.reduce((a, b) -> a + ", " + b)
				.map(a -> " order by " + a + " ")
				.orElse("");
		return select + " where " + conditionList + order + ";";
	}
}
