package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class Header implements Component {
	private final HeaderButton accountButton;

	public Header(boolean isLoggedIn) {
		if (isLoggedIn) {
			accountButton = new HeaderButton("Sign Out", "/sign-out");
		} else {
			accountButton = new HeaderButton("Sign In", "/sign-in");
		}
	}

	public ContainerTag render() {
		return header(a(h1("Nativeslang"), accountButton.render())
				.withClass("header").withHref("/"));
	}
}
