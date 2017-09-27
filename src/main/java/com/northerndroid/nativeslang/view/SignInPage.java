package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.*;

public class SignInPage extends AbstractHeadedPage {
	private final TopicSection topicSection = new TopicSection("Sign In", "Register a new account or sign in to an existing account.");
	private final SignInForm signInForm = new SignInForm();
	private final String topMessage;

	public SignInPage(String topMessage) {
		super("sign-in", Optional.empty());
		this.topMessage = topMessage;
	}

	@Override
	protected ContainerTag mainTag() {
		return main(
				topMessage(),
				topicSection.render(),
				signInForm.render()
		);
	}

	private ContainerTag topMessage() {
		return iff(!topMessage.isEmpty(), p(topMessage)
				.withClass("top-message"));
	}
}
