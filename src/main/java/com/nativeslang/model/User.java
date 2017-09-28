package com.nativeslang.model;

public class User {
	private final long id;
	private final String username, password;

	public User(long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public static String normalize(String username) {
		return username.toLowerCase();
	}

	public static String url(String username) {
		return "/user/" + username;
	}
}
