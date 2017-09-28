package com.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.a;

public class AnchorText implements Component {
	private final String text, url;

	public AnchorText(String text, String url) {
		this.text = text;
		this.url = url;
	}

	@Override
	public ContainerTag render() {
		return a(text)
				.withClass("anchor-text")
				.withHref(url);
	}
}
