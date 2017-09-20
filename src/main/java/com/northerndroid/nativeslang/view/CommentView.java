package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Comment;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class CommentView implements Component {
	private final Comment comment;
	private final MarkdownConverter markdownConverter;

	public CommentView(Comment comment, MarkdownConverter markdownConverter) {
		this.comment = comment;
		this.markdownConverter = markdownConverter;
	}

	@Override
	public ContainerTag render() {
		String author = comment.getUser().getUsername();
		return div(
				rawHtml(markdownConverter.convert(comment.getText())),
				p("by " + author + " as " + comment.getId() + ".").withClass("author"))
				.withClass("comment-view");
	}
}
