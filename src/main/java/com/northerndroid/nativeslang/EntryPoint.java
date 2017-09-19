package com.northerndroid.nativeslang;

import com.northerndroid.nativeslang.model.Comment;
import com.northerndroid.nativeslang.model.Post;
import com.northerndroid.nativeslang.model.User;
import com.northerndroid.nativeslang.view.CommonmarkMarkdownConverter;
import com.northerndroid.nativeslang.view.IndexPage;
import com.northerndroid.nativeslang.view.LanguagePage;
import com.northerndroid.nativeslang.view.MarkdownConverter;
import com.northerndroid.nativeslang.view.PostPage;
import com.northerndroid.nativeslang.view.SignInPage;
import com.northerndroid.nativeslang.view.ViewPostPage;
import spark.Request;
import spark.Route;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static spark.Spark.*;

public class EntryPoint {
	private static boolean isLoggedIn(Request req) {
		return req.session().attribute("username") != null;
	}

	public static void main(String[] args) {
		Database database = Database.newInFile("test/test");
		externalStaticFileLocation("resources/public/");
		port(8080);

		MarkdownConverter markdownConverter = new CommonmarkMarkdownConverter();

		get("/", (req, res) ->
				new IndexPage(req.session().attribute("username") != null)
						.render().toString());
		get("/sign-in", (req, res) -> new SignInPage().render().toString());
		Arrays.stream(Application.languages).forEach(language -> {
			PostPage postPage = new PostPage(language);

			get("/" + language, (req, res) -> {
				List<Post> posts = database.getPostsByLanguage(language);
				LanguagePage languagePage = new LanguagePage(language, posts, isLoggedIn(req));
				return languagePage.render().toString();
			});

			path("/" + language.toLowerCase(), () -> {
				get("/post", (req, res) -> {
					boolean isLoggedIn = req.session().attribute("username") != null;
					if (isLoggedIn) {
						return postPage.render().toString();
					} else {
						res.redirect("/sign-in");
						return "";
					}
				});

				post("/post", (req, res) -> {
					String title = req.queryParams("title");
					String description = req.queryParams("description");
					String username = req.session().attribute("username");
					if (username == null) {
						System.out.println("Username was null.");
					} else if (title == null) {
						System.out.println("Title was null.");
					} else if (description == null) {
						System.out.println("Description was null.");
					} else {
						database.createPost(language.toLowerCase(), username, title, description);
					}
					res.redirect("/" + language.toLowerCase());
					return "";
				});

				Route postRoute = (req, res) -> {
					String id = req.params(":id");
					Objects.requireNonNull(id);
					if (id.matches("\\d+")) {
						long postId = Long.parseLong(id);
						if (database.hasPost(language.toLowerCase(), postId)) {
							Post post = database.getPost(language.toLowerCase(), postId);
							List<Comment> comments = database.getComments(post);
							return new ViewPostPage(markdownConverter,
									post,
									comments,
									isLoggedIn(req)).render().toString();
						}
					}

					res.redirect("/");
					return "";
				};
				get("/post/:id", postRoute);
				get("/post/:id/*", postRoute);
				post("/post/:id/comment", (req, res) -> {
					String id = req.params(":id");
					if (!isLoggedIn(req)) {
						res.redirect("/sign-in");
					} else if (id.matches("\\d+")) {
						long postId = Long.parseLong(id);
						String username = req.session().attribute("username");
						String text = req.queryParams("text");
						if (username != null &&
								text != null &&
								database.hasUser(username) &&
								database.hasPost(postId)) {
							Post post = database.getPost(postId);
							User user = database.getUser(username);
							database.createComment(post, user, text);
						}
						res.redirect("/" + language.toLowerCase() + "/post/" + id);
					} else {
						res.redirect("/");
					}
					return "";
				});
			});
		});

		post("/register", (req, res) -> {
			String username = req.queryParams("username");
			String password = req.queryParams("password");
			if (username == null || password == null) {
				res.redirect("/");
			} else if (database.isUsernameAvailable(username)) {
				database.createUser(username, password);
				req.session().attribute("username", username);
				res.redirect("/");
			} else {
				res.redirect("/sign-in");
			}

			return "";
		});

		post("/sign-in", (req, res) -> {
			boolean isLoggedIn = req.session().attribute("username") != null;
			if (isLoggedIn) {
				res.redirect("/");
				return "";
			} else {
				String username = req.queryParams("username");
				String password = req.queryParams("password");
				if (username == null || password == null) {
					res.redirect("/");
				} else if (database.isLoginValid(username, password)) {
					req.session().attribute("username", username);
					res.redirect("/");
				} else {
					res.redirect("/sign-in");
				}

				return "";
			}
		});

		get("/sign-out", (req, res) -> {
			req.session().removeAttribute("username");
			res.redirect("/");
			return "";
		});

		//DatabaseManagerSwing.main(args);
	}
}
