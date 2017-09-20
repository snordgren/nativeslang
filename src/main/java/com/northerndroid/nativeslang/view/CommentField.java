package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class CommentField implements Component {
	private final Post post;

	public CommentField(Post post) {
		this.post = post;
	}

	@Override
	public ContainerTag render() {
		String formId = "comment-form";
		ContainerTag bold = button(b("B"))
				.attr("onclick", "onBoldClick();")
				.withClass("format-button")
				.withType("button");
		ContainerTag italic = button(i("I"))
				.attr("onclick", "onItalicClick();")
				.withClass("format-button")
				.withType("button");
		ContainerTag strikeThrough = button(s("S"))
				.attr("onclick", "onStrikeThroughClick();")
				.withClass("format-button")
				.withType("button");
		ContainerTag text = textarea()
				.attr("form", formId)
				.withId("comment-text-area")
				.withName("text")
				.withPlaceholder("Write a comment...")
				.isRequired();
		ContainerTag submit = button("Submit")
				.attr("formaction", post.getUrl() + "/comment")
				.withType("submit");
		return form(bold, italic, strikeThrough, text, submit)
				.withId(formId)
				.withMethod("post");
	}
}
