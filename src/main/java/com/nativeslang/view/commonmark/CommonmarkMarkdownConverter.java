package com.nativeslang.view.commonmark;

import com.nativeslang.view.MarkdownConverter;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;

public class CommonmarkMarkdownConverter implements MarkdownConverter {
	private final Parser parser;
	private final HtmlRenderer htmlRenderer;

	public CommonmarkMarkdownConverter() {
		List<Extension> extensions = Arrays.asList(
				AutolinkExtension.create(),
				StrikethroughExtension.create());
		parser = Parser.builder()
				.extensions(extensions)
				.build();
		htmlRenderer = HtmlRenderer.builder()
				.extensions(extensions)
				.build();
	}

	@Override
	public String convert(String source) {
		return htmlRenderer.render(parser.parse(source));
	}
}
