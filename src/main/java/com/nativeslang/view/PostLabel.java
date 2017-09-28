package com.nativeslang.view;

import com.nativeslang.model.Post;
import com.nativeslang.model.User;
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
		String poster = post.getPoster().getUsername();
		ContainerTag infoLabel = p(
				attrs(".post-label-info"),
				text("by "),
				new AnchorText(poster, User.url(poster))
						.render()
						.withClass("post-author"),
				text(", " + commentCount + " comments."));
		String postUrl = post.getUrl();
		String postTitle = post.getTitle();
		return div(attrs(".post-label"),
				p(new AnchorText(postTitle, postUrl)
						.render()
						.withClass("post-title")),
				infoLabel);
	}
}
