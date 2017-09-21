package com.northerndroid.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public abstract class AbstractPage implements Component {
	private final String localCss, localJs;

	public AbstractPage(String pageName) {
		localCss = "/css/" + pageName + ".css";
		localJs = "/js/" + pageName + ".js";
	}

	protected abstract ContainerTag bodyTag();

	protected ContainerTag headTag() {
		return head(title("Nativeslang"),
				script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js").attr("async"),
				script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/autosize.js/4.0.0/autosize.min.js").attr("async"),
				script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/caret/1.0.0/jquery.caret.min.js").attr("async"),
				script().withSrc("https://use.fontawesome.com/b973521c44.js").attr("async"),
				script().withSrc(localJs).attr("async"),
				link().withRel("stylesheet").withHref("https://fonts.googleapis.com/css?family=Open+Sans|Pacifico|PT+Serif"),
				link().withRel("stylesheet").withHref("/css/style.css"),
				link().withRel("stylesheet").withHref(localCss),
				meta().withCharset("UTF-8"),
				meta().withName("viewport")
						.withContent("width=device-width, initial-scale=1"));
	}

	public ContainerTag render() {
		return html(headTag(), bodyTag());
	}
}
