package com.nativeslang.view;

import com.nativeslang.EntryPoint;
import j2html.tags.ContainerTag;
import org.apache.commons.text.WordUtils;

import java.util.Arrays;
import java.util.Optional;

import static j2html.TagCreator.div;
import static j2html.TagCreator.main;

public class IndexPage extends AbstractHeadedPage {
	public IndexPage(Optional<String> currentUser) {
		super("index", currentUser);
	}

	private LanguageButton createLanguageButton(String language) {
		return new LanguageButton(WordUtils.capitalize(language),
				"/svg/" + language.toLowerCase() + ".svg",
				"/" + language.toLowerCase());
	}

	@Override
	protected ContainerTag mainTag() {
		TopicSection topicSection = new TopicSection("Select Language", "Choose which " +
			"language you want to browse posts in. This is a beta version and many " +
            "languages are not yet available.");
		ContainerTag[] languageButtons = Arrays.stream(EntryPoint.languages).map(
				lang -> createLanguageButton(lang).render()).
				toArray(ContainerTag[]::new);
		return main(topicSection.render(), div(languageButtons));
	}
}
