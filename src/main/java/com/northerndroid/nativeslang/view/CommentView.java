package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Comment;
import j2html.tags.ContainerTag;
import j2html.tags.Text;

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
		Text authorText = text("by " + author);
		if (isSuperUser) {
			ContainerTag deleteAnchor = a("delete")
					.withClass("delete-comment")
					.withHref("/comment/delete/" + comment.getId());
			return p(authorText, deleteAnchor).withClass("author");
		} else {
			return p(authorText).withClass("author");
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
