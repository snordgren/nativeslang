package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.*;

public class Header implements Component {
	private final HeaderButton accountButton;

	public Header(Optional<String> currentUser) {
		accountButton = currentUser
				.map(HeaderButton::createProfileButton)
				.orElse(HeaderButton.createSignInButton());
	}

	public ContainerTag render() {
		return header(a(h1("Nativeslang"), accountButton.render())
				.withClass("header").withHref("/"));
	}
}
