package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Comment;
import com.northerndroid.nativeslang.model.User;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class CommentView implements Component {
	private final Comment comment;
	private final MarkdownConverter markdownConverter;
	private final boolean isSuperUser;

	public CommentView(Comment comment,
			MarkdownConverter markdownConverter,
			boolean isSuperUser) {
		this.comment = comment;
		this.markdownConverter = markdownConverter;
		this.isSuperUser = isSuperUser;
	}

	private ContainerTag getAuthorTag() {
		String author = comment.getUser().getUsername();
		ContainerTag authorText = p(
				text("by "),
				new AnchorText(author, User.url(author))
						.render()
						.withClass("author"))
				.withClass("author");
		if (isSuperUser) {
			ContainerTag deleteButton = button("delete")
					.withClasses("textonly", "delete-comment");
			ContainerTag deleteForm = form(deleteButton)
					.withAction("/comment/delete/" + comment.getId())
					.withClass("delete-form")
					.withMethod("post");
			return authorText.with(deleteForm);
		} else {
			return authorText;
		}
	}

	@Override
	public ContainerTag render() {
		return div(
				rawHtml(markdownConverter.convert(comment.getText())),
				getAuthorTag())
				.withClass("comment-view");
	}
}
