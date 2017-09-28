package com.nativeslang.view;

import com.nativeslang.model.User;
import j2html.tags.ContainerTag;

import java.util.List;
import java.util.Optional;

import static j2html.TagCreator.*;

public class UserListPage extends AbstractHeadedPage {
	private final List<User> users;

	public UserListPage(Optional<String> user, List<User> users) {
		super("user-list", user);
		this.users = users;
	}

	private ContainerTag createUserLabel(User user) {
		return p(
				a(user.getUsername()).withHref(User.url(user.getUsername())),
				br());
	}

	@Override
	protected ContainerTag mainTag() {
		return main(users.stream()
				.map(this::createUserLabel)
				.toArray(ContainerTag[]::new));
	}
}
