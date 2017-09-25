package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class LanguageButton implements Component {
	private final String langName, langImgUrl, langUrl;

	public LanguageButton(String langName, String langImgUrl, String langUrl) {
		this.langName = langName;
		this.langImgUrl = langImgUrl;
		this.langUrl = langUrl;
	}

	@Override
	public ContainerTag render() {
		return div(a(img().withSrc(langImgUrl), h3(langName))
				.withClass("language")
				.withHref(langUrl))
				.withClass("language-container");
	}
}
