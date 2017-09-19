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
import spark.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class EntryPoint {
	private static boolean isLoggedIn(Request req) {
		return req.session().attribute("username") != null;
	}

	private static Properties readProperties() {
		Properties properties = new Properties();
		properties.setProperty("httpPort", "8080");
		properties.setProperty("httpsPort", "8181");
		properties.setProperty("ssl", "true");
		properties.setProperty("keystoreFile", "/etc/letsencrypt/live/nativeslang.com/keystore.p12");
		properties.setProperty("keystorePassword", "password");
		File file = new File("properties.xml");
		if (file.exists()) {
			try {
				properties.loadFromXML(new FileInputStream(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
				properties.storeToXML(new FileOutputStream(file), "", "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	private static void create(Database database, Service service) {
		MarkdownConverter markdownConverter = new CommonmarkMarkdownConverter();
		service.externalStaticFileLocation("resources/public/");
		service.get("/", (req, res) ->
				new IndexPage(req.session().attribute("username") != null)
						.render().toString());
		service.get("/sign-in", (req, res) -> new SignInPage().render().toString());
		Arrays.stream(Application.languages).forEach(language -> {
			PostPage postPage = new PostPage(language);

			service.get("/" + language, (req, res) -> {
				List<Post> posts = database.getPostsByLanguage(language);
				LanguagePage languagePage = new LanguagePage(language, posts, isLoggedIn(req));
				return languagePage.render().toString();
			});

			service.path("/" + language.toLowerCase(), () -> {
				service.get("/post", (req, res) -> {
					boolean isLoggedIn = req.session().attribute("username") != null;
					if (isLoggedIn) {
						return postPage.render().toString();
					} else {
						res.redirect("/sign-in");
						return "";
					}
				});

				service.post("/post", (req, res) -> {
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
				service.get("/post/:id", postRoute);
				service.get("/post/:id/*", postRoute);
				service.post("/post/:id/comment", (req, res) -> {
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

		service.post("/register", (req, res) -> {
			String username = req.queryParams("username");
			String password = req.queryParams("password");
			System.out.println("Register request received");
			if (username == null || password == null) {
				System.out.println("Username " + username + ", password " + password + ", one null.");
				res.redirect("/");
			} else if (database.isUsernameAvailable(username)) {
				database.createUser(username, password);
				req.session().attribute("username", username);
				System.out.println("Logged in successfully.");
				res.redirect("/");
			} else {
				System.out.println("Username unavailable.");
				res.redirect("/");
			}

			return "";
		});

		service.post("/sign-in", (req, res) -> {
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

		service.get("/sign-out", (req, res) -> {
			req.session().removeAttribute("username");
			res.redirect("/");
			return "";
		});
	}

	public static void main(String[] args) {
		Properties properties = readProperties();
		Database database = Database.newInFile("test/test");

		Service http = Service.ignite();
		http.port(Integer.parseInt(properties.getProperty("httpPort")));
		Service https = Service.ignite();
		https.port(Integer.parseInt(properties.getProperty("httpsPort")));

		if (Boolean.parseBoolean(properties.getProperty("ssl"))) {
			https.secure(properties.getProperty("keystoreFile"),
					properties.getProperty("keystorePassword"), null, null);
		}


		create(database, https);
	}
}
