package com.nativeslang.view;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

public class Footer implements Component {
	@Override
	public ContainerTag render() {
		return footer(ul(
				li(a("About").withHref("/about")),
				li(a("Contact").withHref("/contact")),
				li(a("FAQ").withHref("/faq"))),
				p(rawHtml("Copyright &copy; 2019 Silas Nordgren")).withClass("copyright"));
	}
}
