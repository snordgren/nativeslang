package com.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class TopicSection implements Component {
	private final String title, description;

	public TopicSection(String title, String description) {
		this.title = title;
		this.description = description;
	}

	@Override
	public ContainerTag render() {
		return section(h1(title), p(description)).withClass("topic");
	}
}
