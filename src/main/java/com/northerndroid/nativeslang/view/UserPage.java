package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.model.User;
import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.*;

public class UserPage extends AbstractHeadedPage {
	private final User user;
	private final String description;
	private final boolean isSameUser;

	public UserPage(User user,
			String description,
			Optional<String> currentUser,
			boolean isSameUser) {
		super("user", currentUser);
		this.description = description;
		this.user = user;
		this.isSameUser = isSameUser;
	}

	@Override
	protected ContainerTag mainTag() {
		TopicSection topic = new TopicSection(user.getUsername(), description);
		ContainerTag main = main(topic.render());
		if (isSameUser) {
			ContainerTag editProfile = a("Edit Profile")
					.withClass("button")
					.withHref("/user/edit/" + user.getUsername());
			return main
					.with(editProfile)
					.with(br())
					.with(HeaderButton.createSignOutButton().render());
		} else {
			return main;
		}
	}
}
