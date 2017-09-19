package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Comment;
import com.northerndroid.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import java.util.List;

import static j2html.TagCreator.*;

public class ViewPostPage extends AbstractPage {
	private final Post post;
	private final List<Comment> comments;

	public ViewPostPage(Post post, List<Comment> comments, boolean isLoggedIn) {
		super("/view-post.css", isLoggedIn);
		this.comments = comments;
		this.post = post;
	}

	@Override
	protected ContainerTag mainTag() {
		String title = post.getTitle();
		String description = post.getDescription();
		String poster = post.getPoster().getUsername();
		TopicSection topicSection = new TopicSection(title, " by " + poster + ".");
		ContainerTag[] commentViews = comments.stream()
				.map(CommentView::new)
				.map(CommentView::render)
				.toArray(ContainerTag[]::new);
		return main(topicSection.render(),
				div(p(description)).withClass("description"),
				new CommentField(post).render(),
				div(commentViews).withClass("comment-list"));
	}
}
