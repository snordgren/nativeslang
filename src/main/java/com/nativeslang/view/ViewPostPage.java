package com.nativeslang.view;

import com.nativeslang.model.Comment;
import com.nativeslang.model.Post;
import com.nativeslang.model.User;
import j2html.tags.ContainerTag;
import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.Optional;

import static j2html.TagCreator.*;

public class ViewPostPage extends AbstractHeadedPage {
	private final Post post;
	private final List<Comment> comments;
	private final MarkdownConverter markdownConverter;
	private final boolean isSuperUser;

	public ViewPostPage(MarkdownConverter markdownConverter,
			Post post,
			List<Comment> comments,
			Optional<String> currentUser,
			boolean isSuperUser) {
		super("view-post", currentUser);
		this.markdownConverter = markdownConverter;
		this.comments = comments;
		this.post = post;
		this.isSuperUser = isSuperUser;
	}

	@Override
	protected ContainerTag mainTag() {
		String title = post.getTitle();
		String description = markdownConverter.convert(post.getDescription());
		String poster = post.getPoster().getUsername();
		String language = post.getLanguage();
		String capitalizedLang = WordUtils.capitalize(language);
		ContainerTag posterTag = p(
				text("by "),
				new AnchorText(poster, User.url(poster)).render(),
				text(" in "),
				new AnchorText(capitalizedLang, "/" + language).render());
		ContainerTag topicSection = section(
				h1(title),
				posterTag)
				.withClass("topic");
		ContainerTag[] commentViews = comments.stream()
				.map(a -> new CommentView(a, markdownConverter, isSuperUser))
				.map(CommentView::render)
				.toArray(ContainerTag[]::new);

		return main(topicSection,
				div(rawHtml(description)).withClass("description"),
				div(commentViews).withClass("comment-list"),
				new CommentForm(post, isSuperUser).render());
	}
}
