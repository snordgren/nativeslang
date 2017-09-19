package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;
import j2html.tags.EmptyTag;

import static j2html.TagCreator.*;

public class PostForm implements Component {
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
				.withName("description")
				.withPlaceholder("Enter description...")
				.attr("autocomplete", "off")
				.attr("form", "submission-form")
				.isRequired();
		ContainerTag submit = button(attrs(".submit-button"), "Submit");
		return form(attrs("#submission-form"), title, br(), description, br(), submit).withMethod("post");
	}
}
