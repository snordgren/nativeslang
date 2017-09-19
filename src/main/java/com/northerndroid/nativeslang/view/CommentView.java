package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Comment;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.div;
import static j2html.TagCreator.p;

public class CommentView implements Component {
	private final Comment comment;

	public CommentView(Comment comment) {
		this.comment = comment;
	}

	@Override
	public ContainerTag render() {
		return div(
				p(comment.getText()),
				p("by " + comment.getUser().getUsername()).withClass("author"))
				.withClass("comment-view");
	}
}
