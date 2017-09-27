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
			AnchorButton editProfile = new AnchorButton("Edit Profile",
					"/user/edit/" + user.getUsername(), "");
			return main
					.with(editProfile.render())
					.with(br())
					.with(createSignOutButton());
		} else {
			return main;
		}
	}

	private static ContainerTag createSignOutButton() {
		ContainerTag signOut = button("Sign Out")
				.withType("submit");
		return form(signOut)
				.withAction("/user/disconnect")
				.withMethod("post");
	}
}
