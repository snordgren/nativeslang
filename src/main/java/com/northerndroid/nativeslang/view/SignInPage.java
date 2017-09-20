package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.main;

public class SignInPage extends AbstractPage {
	private final TopicSection topicSection = new TopicSection("Sign In", "Register a new account or sign in to an existing account.");
	private final SignInForm signInForm = new SignInForm();

	public SignInPage() {
		super("sign-in", false);
	}

	@Override
	protected ContainerTag mainTag() {
		return main(
				topicSection.render(),
				signInForm.render()
		);
	}
}
