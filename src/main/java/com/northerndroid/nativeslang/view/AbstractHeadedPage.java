package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.body;
import static j2html.TagCreator.script;

public abstract class AbstractHeadedPage extends AbstractPage {
	private final Header header;
	private final Footer footer = new Footer();

	public AbstractHeadedPage(String pageName, boolean isLoggedIn) {
		super(pageName);
		header = new Header(isLoggedIn);
	}

	@Override
	protected ContainerTag bodyTag() {
		return body(
				header.render(),
				mainTag(),
				footer.render(),
				script("main();"));
	}

	protected abstract ContainerTag mainTag();
}
