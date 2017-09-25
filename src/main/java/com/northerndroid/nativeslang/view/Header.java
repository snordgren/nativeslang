package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.*;

public class Header implements Component {
	private final AnchorButton accountButton;

	public Header(Optional<String> currentUser) {
		accountButton = currentUser
				.map(Header::createProfileButton)
				.orElse(Header.createSignInButton());
	}

	public ContainerTag render() {
		return header(a(h1("Nativeslang"), accountButton.render())
				.withClass("header").withHref("/"));
	}

	public static AnchorButton createProfileButton(String user) {
		return new AnchorButton("Profile", "/user/" + user, "header-button");
	}

	public static AnchorButton createSignInButton() {
		return new AnchorButton("Sign In", "/sign-in", "header-button");
	}
}
