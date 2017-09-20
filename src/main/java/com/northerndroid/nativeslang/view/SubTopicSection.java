package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class SubTopicSection implements Component {
	private final String title, description;

	public SubTopicSection(String title, String description) {
		this.title = title;
		this.description = description;
	}

	@Override
	public ContainerTag render() {
		return section(h3(title), p(description)).withClass("topic");
	}
}
