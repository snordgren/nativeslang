package com.nativeslang.view;

import com.nativeslang.Database;
import j2html.tags.ContainerTag;
import j2html.tags.EmptyTag;

import static j2html.TagCreator.*;

public class SignInForm implements Component {
	@Override
	public ContainerTag render() {
		EmptyTag username = input()
				.attr("pattern", Database.USERNAME_PATTERN)
				.withName("username")
				.withPlaceholder("Username")
				.withType("text")
				.isRequired();
		EmptyTag password = input()
				.withName("password")
				.withPlaceholder("Password")
				.withType("password")
				.isRequired();
		ContainerTag register = button("Register")
				.withType("submit")
				.attr("formaction", "/user/create");
		ContainerTag signIn = button("Sign In")
				.withType("submit")
				.attr("formaction", "/user/connect");
		return form(username, br(), password, br(), register, signIn).withMethod("post");
	}
}
