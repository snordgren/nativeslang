package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class SplashPage extends AbstractHeadlessPage {
	private final Header header = new Header(false);
	private final Footer footer = new Footer();

	public SplashPage() {
		super("splash");
	}

	private ContainerTag splashImg() {
		ContainerTag message = h1(
				text("Speak the language of everywhere."),
				br(),
				text("Become global."));
		ContainerTag getStarted = a("Get Started")
				.withClass("button")
				.withHref("/sign-in");
		ContainerTag splashContainer = div(message, getStarted)
				.withClass("splash-container");
		return section(splashContainer)
				.withClass("splash");
	}

	@Override
	protected ContainerTag bodyTag() {
		return body(header.render(),
				splashImg(),
				footer.render());
	}
}
