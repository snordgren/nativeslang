package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.a;

public class PostButton implements Component {
	private final String text, url;

	public PostButton(String text, String url) {
		this.text = text;
		this.url = url;
	}

	@Override
	public ContainerTag render() {
		return a(text).withClasses("button", "post-button").withHref(url);
	}
}
