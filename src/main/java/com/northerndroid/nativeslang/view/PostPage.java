package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.main;

public class PostPage extends AbstractHeadedPage {
	private final TopicSection topicSection;
	private final PostForm postForm = new PostForm();

	public PostPage(String language, Optional<String> currentUser) {
		super("post", currentUser);
		String uppercaseLang = Character.toUpperCase(language.charAt(0)) + language.substring(1);
		topicSection = new TopicSection("Post", "Create a new post in " + uppercaseLang + ".");
	}

	@Override
	protected ContainerTag mainTag() {
		return main(topicSection.render(), postForm.render());
	}
}
