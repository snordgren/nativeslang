package com.nativeslang.view;

import com.nativeslang.model.User;
import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.*;

public class UserPage extends AbstractHeadedPage {
	private final User user;
	private final String description;
	private final boolean isSameUser, isSuperUser;

	public UserPage(User user,
			String description,
			Optional<String> currentUser,
			boolean isSameUser,
			boolean isSuperUser) {
		super("user", currentUser);
		this.description = description;
		this.user = user;
		this.isSameUser = isSameUser;
		this.isSuperUser = isSuperUser;
	}

	private ContainerTag basicMainTag() {
		TopicSection topic = new TopicSection(user.getUsername(), description);
		return main(topic.render());
	}

	private ContainerTag sameUserTag() {
		AnchorButton editProfile = new AnchorButton("Edit Profile",
				"/user/edit/" + user.getUsername(), "");
		return div(editProfile.render(), br(), createSignOutButton());
	}

	private ContainerTag superUserTag() {
		ContainerTag banButton = button("Ban")
				.withType("submit");
		return form(banButton)
				.withAction("/user/ban/" + user.getUsername())
				.withMethod("post");
	}

	@Override
	protected ContainerTag mainTag() {
		if (isSameUser) {
			return basicMainTag().with(sameUserTag());
		} else if (isSuperUser) {
			return basicMainTag().with(superUserTag());
		}

		return basicMainTag();
	}

	private static ContainerTag createSignOutButton() {
		ContainerTag signOut = button("Sign Out")
				.withType("submit");
		return form(signOut)
				.withAction("/user/disconnect")
				.withMethod("post");
	}
}
