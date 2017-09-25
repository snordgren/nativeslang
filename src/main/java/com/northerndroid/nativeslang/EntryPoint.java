package com.northerndroid.nativeslang;

import com.northerndroid.nativeslang.model.Comment;
import com.northerndroid.nativeslang.model.Post;
import com.northerndroid.nativeslang.model.User;
import com.northerndroid.nativeslang.view.AboutPage;
import com.northerndroid.nativeslang.view.EditUserPage;
import com.northerndroid.nativeslang.view.IndexPage;
import com.northerndroid.nativeslang.view.LanguagePage;
import com.northerndroid.nativeslang.view.MarkdownConverter;
import com.northerndroid.nativeslang.view.PostPage;
import com.northerndroid.nativeslang.view.SignInPage;
import com.northerndroid.nativeslang.view.SplashPage;
import com.northerndroid.nativeslang.view.UserPage;
import com.northerndroid.nativeslang.view.ViewPostPage;
import com.northerndroid.nativeslang.view.commonmark.CommonmarkMarkdownConverter;
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
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class EntryPoint {
	private static Optional<String> getCurrentUser(Request req) {
		return Optional.ofNullable(req.session().attribute("username"));
	}

	private static boolean isLoggedIn(Request req) {
		return getCurrentUser(req).isPresent();
	}

	private static boolean isSuperUser(Database database, Request req) {
		String username = req.session().attribute("username");
		if (username != null) {
			return database.isSuperUser(username);
		}

		return false;
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
		final long expireTime = 60 * 60 * 24 * 30;
		MarkdownConverter markdownConverter = new CommonmarkMarkdownConverter();
		service.staticFiles.externalLocation("resources/public/");
		service.staticFiles.expireTime(expireTime);
		service.staticFiles.header("Content-Encoding", "gzip");
		service.staticFiles.header("Cache-Control", "public, max-age=" + expireTime);
		service.staticFiles.header("ETag", "0x123456");
		service.staticFiles.header("Vary", "Accept-Encoding");

		service.get("/", (req, res) -> {
			Optional<String> currentUser = getCurrentUser(req);
			if (currentUser.isPresent()) {
				return new IndexPage(currentUser)
						.render()
						.toString();
			} else {
				return new SplashPage().render().toString();
			}
		});
		service.get("/about", (req, res) -> new AboutPage(getCurrentUser(req)).render().toString());
		service.get("/sign-in", (req, res) -> {
			Optional<String> currentUser = getCurrentUser(req);
			if (currentUser.isPresent()) {
				res.redirect("/");
				return "";
			} else {
				return new SignInPage().render().toString();
			}
		});
		Arrays.stream(Application.languages).forEach(language -> {

			service.get("/" + language, (req, res) -> {
				List<Post> posts = database.getPostsByLanguage(language);
				List<Integer> commentCounts = posts.stream()
						.map(p -> database.getCommentCount(p.getId()))
						.collect(Collectors.toList());
				LanguagePage languagePage = new LanguagePage(language, posts,
						commentCounts, getCurrentUser(req));
				return languagePage.render().toString();
			});

			service.path("/" + language.toLowerCase(), () -> {
				service.get("/post", (req, res) -> {
					PostPage postPage = new PostPage(language, getCurrentUser(req));
					if (isLoggedIn(req)) {
						return postPage.render().toString();
					} else {
						res.redirect("/sign-in");
						return "";
					}
				});

				service.post("/post", (req, res) -> {
					String title = req.queryParams("title");
					String description = req.queryParams("description");
					Optional<String> currentUser = getCurrentUser(req);
					currentUser.ifPresent(username -> {
						if (title == null) {
							System.out.println("Title was null.");
						} else if (description == null) {
							System.out.println("Description was null.");
						} else {
							database.createPost(language.toLowerCase(), username, title, description);
						}
					});
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
							List<Comment> comments = database.getComments(post.getId());
							return new ViewPostPage(markdownConverter,
									post,
									comments,
									getCurrentUser(req),
									isSuperUser(database, req)).render().toString();
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

		service.get("/post/delete/:id", (req, res) -> {
			String id = req.params(":id");
			if (id != null
					&& id.matches("\\d+")
					&& isSuperUser(database, req)) {
				long postId = Long.parseLong(id);
				if (database.hasPost(postId)) {
					database.createHiddenPost(postId);
				}
			}
			res.redirect("/");
			return "";
		});

		service.get("/comment/delete/:id", (req, res) -> {
			String id = req.params(":id");
			if (id != null
					&& id.matches("\\d+")
					&& isSuperUser(database, req)) {
				long postId = Long.parseLong(id);
				if (database.hasComment(postId)) {
					database.createHiddenComment(postId);
				}
			}
			res.redirect("/");
			return "";
		});

		service.get("/user/:name", (req, res) -> {
			String username = req.params(":name");
			if (username != null && database.hasUser(username)) {
				User user = database.getUser(username);
				String description;
				if (database.hasUserDescription(user.getId())) {
					description = database.getUserDescription(user.getId());
				} else {
					description = "This user has not yet added a description.";
				}


				String sessionUser = req.session().attribute("username");
				boolean isSameUser = sessionUser != null
						&& User.normalize(sessionUser).equals(
						User.normalize(username));

				return new UserPage(user,
						description,
						getCurrentUser(req),
						isSameUser).render().toString();
			} else {
				res.redirect("/");
				return "";
			}
		});

		service.get("/user/edit/:name", (req, res) -> {
			String username = req.params(":name");
			if (username != null && database.hasUser(username)) {
				User user = database.getUser(username);
				String description;
				if (database.hasUserDescription(user.getId())) {
					description = database.getUserDescription(user.getId());
				} else {
					description = "This user has not yet added a description.";
				}

				String sessionUser = req.session().attribute("username");
				boolean isSameUser = sessionUser != null
						&& User.normalize(sessionUser).equals(
						User.normalize(username));
				if (isSameUser) {
					return new EditUserPage(
							getCurrentUser(req),
							description)
							.render()
							.toString();
				}
			}

			res.redirect("/");
			return "";
		});

		service.post("/user/update/:name", (req, res) -> {
			String name = req.params(":name");
			if (name != null && database.hasUser(name)) {
				User user = database.getUser(name);
				Optional<String> currentUser = getCurrentUser(req);
				String description = req.queryParams("description");
				if (currentUser.isPresent()
						&& currentUser.get().equalsIgnoreCase(name)
						&& description != null) {
					database.createUserDescription(user.getId(), description);
				}
			}
			res.redirect(User.url(name));
			return "";
		});

		service.post("/register", (req, res) -> {
			String username = req.queryParams("username");
			String password = req.queryParams("password");
			System.out.println("Register request received");
			if (username == null || password == null) {
				System.out.println("Username " + username + ", password " + password + ", one null.");
				res.redirect("/");
			} else if (database.hasUser(username)) {
				System.out.println("Username unavailable.");
				res.redirect("/");
			} else {
				database.createUser(username, password);
				req.session().attribute("username", username);
				System.out.println("Logged in successfully.");
				res.redirect("/");
			}

			return "";
		});

		service.post("/sign-in", (req, res) -> {
			if (isLoggedIn(req)) {
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

		service.after((req, res) -> {
			res.header("Content-Encoding", "gzip");
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

		http.get("/*", (req, res) -> {
			res.redirect("https://nativeslang.com");
			return "";
		});
		create(database, https);

		new Application(database).run();
	}
}
