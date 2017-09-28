package com.nativeslang.sql;

public class Order {
	private final String column;
	private final OrderType orderType;

	public Order(String column, OrderType orderType) {
		this.column = column;
		this.orderType = orderType;
	}

	public String getColumn() {
		return column;
	}

	public OrderType getOrderType() {
		return orderType;
	}
}
