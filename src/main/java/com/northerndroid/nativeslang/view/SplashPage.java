package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.util.DesignUtil;
import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.*;

public class SplashPage extends AbstractHeadlessPage {
	private final Header header = new Header(Optional.empty());
	private final Footer footer = new Footer();

	public SplashPage() {
		super("splash");
	}

	private AnchorButton getStarted() {
		return new AnchorButton("Get Started",
				"/sign-in",
				"get-started");
	}

	private ContainerTag splashImg() {
		ContainerTag message = h1(
				text("Write in your target language,"),
				br(),
				text("get native feedback."));
		ContainerTag splashContainer = div(
				div(message).withClass("splash-container-flex"),
				div(getStarted().render()).withClass("splash-container-flex"))
				.withClass("splash-container");
		return section(splashContainer)
				.withClass("splash");
	}

	@Override
	protected ContainerTag bodyTag() {
		return body(header.render(),
				splashImg(),
				rawHtml(DesignUtil.loadHtml("splash-info")),
				div(getStarted().render())
						.withClass("bottom-get-started-container"),
				footer.render());
	}
}
