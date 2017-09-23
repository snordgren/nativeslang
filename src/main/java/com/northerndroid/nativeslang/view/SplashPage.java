package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.util.DesignUtil;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class SplashPage extends AbstractHeadlessPage {
	private final Header header = new Header(false);
	private final Footer footer = new Footer();

	public SplashPage() {
		super("splash");
	}

	private ContainerTag getStarted() {
		return a("Get Started")
				.withClasses("button", "get-started")
				.withHref("/sign-in");
	}

	private ContainerTag splashImg() {
		ContainerTag message = h1(
				text("Speak the language of everywhere."),
				br(),
				text("Become global."));
		ContainerTag splashContainer = div(
				div(message).withClass("splash-container-flex"),
				div(getStarted()).withClass("splash-container-flex"))
				.withClass("splash-container");
		return section(splashContainer)
				.withClass("splash");
	}

	@Override
	protected ContainerTag bodyTag() {
		return body(header.render(),
				splashImg(),
				rawHtml(DesignUtil.loadHtml("splash-info")),
				div(getStarted()).withClass("bottom-get-started-container"),
				footer.render());
	}
}
