package com.nativeslang.view;

import com.nativeslang.util.DesignUtil;
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
				rawHtml(DesignUtil.loadHtml("head")),
				script().withSrc(localJs).attr("async"),
				link().withRel("stylesheet").withHref(localCss));
	}

	public ContainerTag render() {
		return html(headTag(), bodyTag());
	}
}
