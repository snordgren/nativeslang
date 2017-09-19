package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.Post;
import j2html.tags.ContainerTag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static j2html.TagCreator.main;

public class LanguagePage extends AbstractPage {
	private final TopicSection topicSection;
	private final PostButton postButton;
	private final List<Post> posts;

	public LanguagePage(String language, List<Post> posts, boolean isLoggedIn) {
		super("/language.css", isLoggedIn);
		String uppercaseLang = Character.toUpperCase(language.charAt(0)) + language.substring(1);
		this.posts = posts;
		postButton = new PostButton("Make a Post", "/" + language.toLowerCase() + "/post");
		topicSection = new TopicSection(uppercaseLang, "Read and correct posts written in " + uppercaseLang + " here.");
	}

	@Override
	protected ContainerTag mainTag() {
		List<ContainerTag> tags = new ArrayList<ContainerTag>();
		tags.add(topicSection.render());
		tags.add(postButton.render());
		tags.addAll(posts.stream()
				.map(a -> new PostLabel(a).render())
				.collect(Collectors.toList()));
		return main(tags.toArray(new ContainerTag[tags.size()]));
	}
}
