package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.EntryPoint;
import com.nativeslang.model.Post;
import com.nativeslang.model.User;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.Arrays;
import java.util.Optional;

public class PostService {
	private final Database database;
	private final String[] languages;

	public PostService(Database database, String[] languages) {
		this.database = database;
		this.languages = languages;
	}

	private String comment(Request req, Response res) {
		String id = req.params(":id");
		if (EntryPoint.isLoggedIn(req)) {
			if (id.matches("\\d+")) {
				long postId = Long.parseLong(id);
				String username = req.session().attribute("username");
				String text = req.queryParams("text");
				if (database.hasPost(postId)) {
					Post post = database.getPost(postId);
					String language = post.getLanguage();
					if (username != null &&
							text != null &&
							database.hasUser(username)) {
						User user = database.getUser(username);
						System.out.println(text);
						database.createComment(post, user, text);
					}
					res.redirect("/" + language.toLowerCase() + "/post/" + id);
					return "";
				}
			}

			res.redirect("/");
			return "";
		} else {
			res.redirect("/sign-in");
			return "";
		}
	}

	private String create(String language, Request req, Response res) {
		String title = req.queryParams("title");
		String description = req.queryParams("description");
		Optional<String> currentUser = EntryPoint.getCurrentUser(req);
		currentUser.ifPresent(username -> {
			if (title == null) {
				System.out.println("Title was null.");
			} else if (description == null) {
				System.out.println("Description was null.");
			} else {
				database.createPost(language.toLowerCase(),
						username,
						title,
						description);
			}
		});
		res.redirect("/" + language.toLowerCase());
		return "";
	}

	private String delete(Request req, Response res) {
		String id = req.params(":id");
		if (id != null
				&& id.matches("\\d+")
				&& EntryPoint.isSuperUser(database, req)) {
			long postId = Long.parseLong(id);
			if (database.hasPost(postId)) {
				database.createHiddenPost(postId);
			}
		}
		res.redirect("/");
		return "";
	}

	public void register(Service service) {
		service.post("/post/comment/:id", this::comment);
		service.post("/post/delete/:id", this::delete);
		Arrays.stream(languages).forEach(language -> {
			service.post("/" + language + "/post/create",
					(req, res) -> create(language, req, res));
		});
	}
}
