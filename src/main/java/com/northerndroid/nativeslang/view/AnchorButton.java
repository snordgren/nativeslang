package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.a;

public class AnchorButton implements Component {
	private final String text, customClass, url;

	public AnchorButton(String text, String url) {
		this(text, url, "");
	}

	public AnchorButton(String text, String url, String customClass) {
		this.text = text;
		this.url = url;
		this.customClass = customClass;
	}

	@Override
	public ContainerTag render() {
		return a(text)
				.withClasses("button", customClass)
				.withHref(url);
	}
}
