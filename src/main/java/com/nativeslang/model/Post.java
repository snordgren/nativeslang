package com.nativeslang.model;

public class Post {
	private final long id;
	private final User poster;
	private final String language, title, description;

	public Post(long id, User poster, String language, String title, String description) {
		this.id = id;
		this.poster = poster;
		this.language = language;
		this.title = title;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public User getPoster() {
		return poster;
	}

	public String getDescription() {
		return description;
	}

	public String getLanguage() {
		return language;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return "/" + getLanguage().toLowerCase() + "/post/" + id;
	}
}
