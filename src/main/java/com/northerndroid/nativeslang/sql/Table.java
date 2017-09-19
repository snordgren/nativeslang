package com.northerndroid.nativeslang.sql;

import java.util.Arrays;

public class Table {
	private final Column[] columns;
	private final String name;

	public Table(String name, Column[] columns) {
		this.columns = columns;
		this.name = name;
	}

	public String create() {
		String columnStr = Arrays.stream(columns)
				.map(Column::compile)
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
		return "create table if not exists " + name + " (" + columnStr + ");";
	}

	private boolean hasColumn(String name) {
		for (Column column : columns) {
			if (column.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public String insert(Into... intos) {
		requireColumns(Arrays.stream(intos)
				.map(Into::getColumnName)
				.toArray(String[]::new));
		String nameList = Arrays.stream(intos)
				.map(Into::getColumnName)
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
		String valueList = Arrays.stream(intos)
				.map(Into::getValue)
				.map(SQLBuilder::value)
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
		return "insert into " + name + " (" + nameList + ") values (" + valueList + ");";
	}

	private void requireColumns(String[] columns) {
		for (String columnName : columns) {
			if (!hasColumn(columnName)) {
				throw new UnsupportedOperationException(
						"Table has no column named " + columnName
				);
			}
		}
	}

	public Select selectAll() {
		return select(Arrays.stream(columns).map(Column::getName).toArray(String[]::new));
	}

	public Select select(String... columns) {
		requireColumns(columns);
		return new Select(this.name, columns, new Order[0]);
	}

	public String selectCountWhere(Condition... conditions) {
		requireColumns(Arrays.stream(conditions)
				.map(Condition::getColumnName)
				.toArray(String[]::new));
		String conditionStr = Arrays.stream(conditions)
				.map(Condition::compile)
				.reduce((a, b) -> a + " and " + b)
				.orElse("");
		return "select count (*) from " + name + " where " + conditionStr;
	}
}
