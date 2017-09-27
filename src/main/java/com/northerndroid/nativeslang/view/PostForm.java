package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;
import j2html.tags.EmptyTag;

import static j2html.TagCreator.*;

public class PostForm implements Component {
	private final String language;

	public PostForm(String language) {
		this.language = language;
	}

	@Override
	public ContainerTag render() {
		EmptyTag title = input()
				.withPlaceholder("Enter title...")
				.withClass("text")
				.withType("text")
				.withName("title")
				.attr("autocomplete", "off")
				.isRequired();
		ContainerTag description = textarea()
				.withId("description")
				.withName("description")
				.withPlaceholder("Enter description...")
				.attr("autocomplete", "off")
				.attr("form", "submission-form")
				.isRequired();
		ContainerTag submit = button("Submit")
				.withClass("submit-button")
				.withType("submit");
		return form(attrs("#submission-form"),
				title,
				br(),
				description,
				br(),
				submit)
				.withAction("/" + language + "/post/create")
				.withMethod("post");
	}
}
