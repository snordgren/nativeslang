package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class PostLabel implements Component {
	private final Post post;
	private final int commentCount;

	public PostLabel(Post post, int commentCount) {
		this.post = post;
		this.commentCount = commentCount;
	}

	@Override
	public ContainerTag render() {
		ContainerTag infoLabel = p(
				attrs(".post-label-info"),
				text("by "),
				a(post.getPoster().getUsername()),
				text(", " + commentCount + " comments."));
		return div(attrs(".post-label"),
				p(a(attrs(".post-title"), post.getTitle()).withHref(post.getUrl())),
				infoLabel);
	}
}
