package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Comment;
import com.northerndroid.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import java.util.List;

import static j2html.TagCreator.*;

public class ViewPostPage extends AbstractHeadedPage {
	private final Post post;
	private final List<Comment> comments;
	private final MarkdownConverter markdownConverter;

	public ViewPostPage(MarkdownConverter markdownConverter,
			Post post,
			List<Comment> comments,
			boolean isLoggedIn) {
		super("view-post", isLoggedIn);
		this.markdownConverter = markdownConverter;
		this.comments = comments;
		this.post = post;
	}

	@Override
	protected ContainerTag mainTag() {
		String title = post.getTitle();
		String description = markdownConverter.convert(post.getDescription());
		String poster = post.getPoster().getUsername();
		TopicSection topicSection = new TopicSection(title, " by " + poster + ".");
		ContainerTag[] commentViews = comments.stream()
				.map(a -> new CommentView(a, markdownConverter))
				.map(CommentView::render)
				.toArray(ContainerTag[]::new);
		return main(topicSection.render(),
				div(rawHtml(description)).withClass("description"),
				new CommentField(post).render(),
				div(commentViews).withClass("comment-list"));
	}
}
