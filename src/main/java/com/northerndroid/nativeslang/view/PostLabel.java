package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class PostLabel implements Component {
	private final Post post;

	public PostLabel(Post post) {
		this.post = post;
	}

	@Override
	public ContainerTag render() {
		return div(attrs(".post-label"),
				p(a(attrs(".post-title"), post.getTitle()).withHref(post.getUrl())),
				p(attrs(".post-label-info"), text("by "), i(a(post.getPoster().getUsername()))));
	}
}
