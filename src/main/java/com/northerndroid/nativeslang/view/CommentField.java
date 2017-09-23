package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class CommentField implements Component {
	private final Post post;
	private final String postText;

	public CommentField(Post post) {
		this.post = post;
		this.postText = ">" + post.getDescription().replace("\n", "\n>") + "\n\n";
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
		ContainerTag quote = button("Quote")
				.attr("onclick", "onQuote();")
				.withClasses("button", "bottom-button")
				.withData("post-text", postText)
				.withId("quote-button")
				.withType("button");
		ContainerTag submit = button("Submit")
				.attr("formaction", post.getUrl() + "/comment")
				.withClasses("button", "bottom-button")
				.withType("submit");
		return form(bold, italic, strikeThrough, text, quote, submit)
				.withId(formId)
				.withMethod("post");
	}
}
