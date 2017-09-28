package com.nativeslang.view;

import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.*;

public class EditUserPage extends AbstractHeadedPage {
	private final String description, username;

	public EditUserPage(Optional<String> user, String description) {
		super("edit-user", user);
		this.description = description;
		if (user.isPresent()) {
			this.username = user.get();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected ContainerTag mainTag() {
		TopicSection topicSection = new TopicSection("Edit Profile",
				"Update your profile here.");
		ContainerTag descriptionArea = textarea(description)
				.withName("description")
				.withPlaceholder("Enter description here...");
		ContainerTag submitButton = button("Submit")
				.withType("submit");
		ContainerTag form = form(descriptionArea, submitButton)
				.withAction("/user/update/" + username)
				.withMethod("post");
		return main(topicSection.render(), form);
	}
}
