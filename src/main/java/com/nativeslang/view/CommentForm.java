package com.nativeslang.view;

import com.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class CommentForm implements Component {
	private final Post post;
	private final String postText;
	private final boolean isSuperUser;

	public CommentForm(Post post, boolean isSuperUser) {
		this.post = post;
		this.postText = ">" + post.getDescription().replace("\n", "\n>") + "\n\n";
		this.isSuperUser = isSuperUser;
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
				.attr("formaction", "/post/comment/" + post.getId())
				.withClasses("button", "bottom-button")
				.withType("submit");
		ContainerTag baseForm = form(quote, bold, italic, strikeThrough, text, submit)
				.withId(formId)
				.withMethod("post");
		ContainerTag delete = button("Delete")
				.attr("formaction", "/post/delete/" + post.getId())
				.withClasses("button", "bottom-button", "delete-button")
				.withHref("/post/delete/" + post.getId())
				.withMethod("post");
		return (isSuperUser) ? baseForm.with(delete) : baseForm;
	}
}
