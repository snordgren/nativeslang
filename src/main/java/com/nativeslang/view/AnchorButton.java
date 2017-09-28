package com.nativeslang.view;

import com.google.common.collect.ObjectArrays;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.a;

public class AnchorButton implements Component {
	private final String text, url;
	private final String[] classes;

	public AnchorButton(String text, String url, String... classes) {
		this.text = text;
		this.url = url;
		this.classes = classes;
	}

	@Override
	public ContainerTag render() {
		return a(text)
				.withClasses(ObjectArrays.concat("button", classes))
				.withHref(url);
	}
}
