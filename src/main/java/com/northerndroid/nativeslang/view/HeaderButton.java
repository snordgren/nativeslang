package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.a;

public class HeaderButton implements Component {
	private final String text, url;

	public HeaderButton(String text, String url) {
		this.text = text;
		this.url = url;
	}

	@Override
	public ContainerTag render() {
		return a(text).withClasses("button", "header-button").withHref(url);
	}
}
