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
		ContainerTag text = textarea()
				.attr("form", formId)
				.withName("text")
				.withPlaceholder("Write a comment...")
				.isRequired();
		ContainerTag submit = button("Submit")
				.attr("formaction", post.getUrl() + "/comment")
				.withType("submit");
		return form(text, submit)
				.withId(formId)
				.withMethod("post");
	}
}
