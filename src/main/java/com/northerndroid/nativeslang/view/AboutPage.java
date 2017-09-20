package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.main;

public class AboutPage extends AbstractHeadedPage {
	private static final String ABOUT_TITLE = "About",
			ABOUT_DESCRIPTION = "Find out how Nativeslang works.",
			CORRECT_TITLE = "Native speakers correct your entries",
			CORRECT_DESCRIPTION = "Everyone learns by making mistakes, and on Nativeslang " +
					"native speakers help you learn how to best speak their language.",
			FRIENDS_TITLE = "Make friends all over the world",
			FRIENDS_DESCRIPTION = "Nativeslang makes learning and connecting with people " +
					"from other countries and cultures fun and easy.",
			TEACH_TITLE = "Help others learn your language",
			TEACH_DESCRIPTION = "Give back to the community by helping others learn your " +
					"native language. You can correct their entries and help them practice " +
					"general conversation in their target language.",
			WRITE_TITLE = "Write entries in the languages you study",
			WRITE_DESCRIPTION = "Write entries in the languages you learn, whether they " +
					"be about the weather, your day, or the news, and share them with " +
					"the rest of the world!";

	public AboutPage(boolean isLoggedIn) {
		super("about", isLoggedIn);
	}

	@Override
	protected ContainerTag mainTag() {
		ContainerTag about = new TopicSection(ABOUT_TITLE, ABOUT_DESCRIPTION).render();
		ContainerTag correct = new SubTopicSection(CORRECT_TITLE, CORRECT_DESCRIPTION).render();
		ContainerTag friends = new SubTopicSection(FRIENDS_TITLE, FRIENDS_DESCRIPTION).render();
		ContainerTag teach = new SubTopicSection(TEACH_TITLE, TEACH_DESCRIPTION).render();
		ContainerTag write = new SubTopicSection(WRITE_TITLE, WRITE_DESCRIPTION).render();
		return main(about, write, correct, teach, friends);
	}
}
