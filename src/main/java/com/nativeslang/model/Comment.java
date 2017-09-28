package com.nativeslang.model;

public class Comment {
	private final Post post;
	private final User user;
	private final String text;
	private final long id;

	public Comment(long id, Post post, User user, String text) {
		this.post = post;
		this.user = user;
		this.text = text;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public Post getPost() {
		return post;
	}

	public String getText() {
		return text;
	}

	public User getUser() {
		return user;
	}
}
