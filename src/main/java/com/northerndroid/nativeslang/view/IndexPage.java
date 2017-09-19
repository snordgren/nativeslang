package com.northerndroid.nativeslang.view;

import com.northerndroid.nativeslang.Application;
import j2html.tags.ContainerTag;
import org.apache.commons.text.WordUtils;

import java.util.Arrays;

import static j2html.TagCreator.div;
import static j2html.TagCreator.main;

public class IndexPage extends AbstractPage {
	public IndexPage(boolean isLoggedIn) {
		super("index.css", isLoggedIn);
	}

	private LanguageButton createLanguageButton(String language) {
		return new LanguageButton(WordUtils.capitalize(language),
				"/images/" + language.toLowerCase() + ".svg",
				"/" + language.toLowerCase());
	}

	@Override
	protected ContainerTag mainTag() {
		TopicSection topicSection = new TopicSection("Select Language", "Choose which language you want to browse posts in. This is a beta version and many languages are not yet available.");
		ContainerTag[] languageButtons = Arrays.stream(Application.languages).map(
				lang -> createLanguageButton(lang).render()).
				toArray(ContainerTag[]::new);
		return main(topicSection.render(), div(languageButtons));
	}
}
