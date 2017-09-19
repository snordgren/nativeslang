package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public abstract class AbstractPage implements Component {
	private final Header header;
	private final Footer footer = new Footer();
	private final String localCss;

	public AbstractPage(String localCss, boolean isLoggedIn) {
		if (localCss.startsWith("/")) {
			this.localCss = localCss;
		} else {
			this.localCss = "/" + localCss;
		}
		header = new Header(isLoggedIn);
	}

	protected ContainerTag bodyTag() {
		return body(
				header.render(),
				mainTag(),
				footer.render()
		);
	}

	protected ContainerTag headTag() {
		return head(title("Nativeslang"),
				link().withRel("stylesheet").withHref("https://fonts.googleapis.com/css?family=Open+Sans|Pacifico"),
				link().withRel("stylesheet").withHref("/style.css"),
				link().withRel("stylesheet").withHref(localCss),
				meta().withCharset("UTF-8"));
	}

	protected abstract ContainerTag mainTag();

	public ContainerTag render() {
		return html(headTag(), bodyTag());
	}
}
