package com.northerndroid.nativeslang.view;

public interface MarkdownConverter {

	/**
	 * Converts the Markdown source into an HTML string.
	 *
	 * @param source The Markdown source.
	 * @return The HTML output.
	 */
	String convert(String source);
}
