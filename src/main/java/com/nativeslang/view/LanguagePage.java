package com.nativeslang.view;

import com.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static j2html.TagCreator.main;

public class LanguagePage extends AbstractHeadedPage {
	private final TopicSection topicSection;
	private final AnchorButton postButton;
	private final List<Post> posts;
	private final List<Integer> commentCounts;

	public LanguagePage(String language,
			List<Post> posts,
			List<Integer> commentCounts,
			Optional<String> currentUser) {
		super("language", currentUser);
		String uppercaseLang = Character.toUpperCase(language.charAt(0)) + language.substring(1);
		this.commentCounts = commentCounts;
		this.posts = posts;
		postButton = new AnchorButton("Make a Post",
				"/" + language.toLowerCase() + "/post",
				"post-button");
		topicSection = new TopicSection(uppercaseLang, "Read and correct posts written in " + uppercaseLang + " here.");
	}

	@Override
	protected ContainerTag mainTag() {
		List<ContainerTag> tags = new ArrayList<ContainerTag>();
		tags.add(topicSection.render());
		tags.add(postButton.render());
		for (int i = 0; i < posts.size(); i++) {
			Post post = posts.get(i);
			int commentCount = commentCounts.get(i);
			tags.add(new PostLabel(post, commentCount).render());
		}
		return main(tags.toArray(new ContainerTag[tags.size()]));
	}
}
