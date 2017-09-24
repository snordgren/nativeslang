package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.a;

public class HeaderButton implements Component {
	private final String text, url;

	public HeaderButton(String text, String url) {
		this.text = text;
		this.url = url;
	}

	@Override
	public ContainerTag render() {
		return a(text)
				.withClasses("button", "header-button")
				.withHref(url);
	}

	public static HeaderButton createProfileButton(String user) {
		return new HeaderButton("Profile", "/user/" + user);
	}

	public static HeaderButton createSignInButton() {
		return new HeaderButton("Sign In", "/sign-in");
	}

	public static HeaderButton createSignOutButton() {
		return new HeaderButton("Sign Out", "/sign-out");
	}
}
