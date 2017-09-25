package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.util.DesignUtil;
import j2html.tags.ContainerTag;

import java.util.Optional;

import static j2html.TagCreator.main;
import static j2html.TagCreator.rawHtml;

public class AboutPage extends AbstractHeadedPage {
	public AboutPage(Optional<String> currentUser) {
		super("about", currentUser);
	}

	@Override
	protected ContainerTag mainTag() {
		return main(rawHtml(DesignUtil.loadHtml("about")));
	}
}
