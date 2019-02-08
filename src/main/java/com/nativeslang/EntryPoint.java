package com.nativeslang;

import com.nativeslang.controller.CommentService;
import com.nativeslang.controller.PostService;
import com.nativeslang.controller.UserService;
import com.nativeslang.model.Comment;
import com.nativeslang.model.Post;
import com.nativeslang.model.User;
import com.nativeslang.view.AboutPage;
import com.nativeslang.view.EditUserPage;
import com.nativeslang.view.IndexPage;
import com.nativeslang.view.LanguagePage;
import com.nativeslang.view.PostPage;
import com.nativeslang.view.SignInPage;
import com.nativeslang.view.SplashPage;
import com.nativeslang.view.UserListPage;
import com.nativeslang.view.UserPage;
import com.nativeslang.view.ViewPostPage;
import spark.Request;
import spark.Response;
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
	public static Optional<String> getCurrentUser(Request req) {
		return Optional.ofNullable(req.session().attribute("username"));
	}

	public static boolean isLoggedIn(Request req) {
		return getCurrentUser(req).isPresent();
	}

	public static boolean isSuperUser(Database database, Request req) {
		String username = req.session().attribute("username");
		if (username != null) {
			return database.isSuperUser(username);
		}

		return false;
	}

	private static String signIn(String msg, Request req, Response res) {
		Optional<String> currentUser = getCurrentUser(req);
		if (currentUser.isPresent()) {
			res.redirect("/");
			return "";
		} else {
			return new SignInPage(msg).render().toString();
		}
	}

	private static Properties readProperties() {
		Properties properties = new Properties();
		properties.setProperty("httpPort", "8080");
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

		service.get("/sign-in", (req, res) -> signIn("", req, res));
		service.get("/sign-in/login-error", (req, res) ->
				signIn("Wrong username or password.", req, res));
		service.get("/sign-in/username-taken", (req, res) ->
				signIn("Username is taken.", req, res));
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

				Route postRoute = (req, res) -> {
					String id = req.params(":id");
					Objects.requireNonNull(id);
					if (id.matches("\\d+")) {
						long postId = Long.parseLong(id);
						if (database.hasPost(language.toLowerCase(), postId)) {
							Post post = database.getPost(language.toLowerCase(), postId);
							List<Comment> comments = database.getComments(post.getId());
							return new ViewPostPage(post,
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
			});
		});

		PostService postService = new PostService(database, Application.languages);
		postService.register(service);

		CommentService commentService = new CommentService(database);
		commentService.register(service);

		UserService userService = new UserService(database);
		userService.register(service);

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
				boolean isSuperUser = isSuperUser(database, req);

				return new UserPage(user,
						description,
						getCurrentUser(req),
						isSameUser,
						isSuperUser).render().toString();
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

		service.get("/user-list", (req, res) -> {
			if (isSuperUser(database, req)) {
				List<User> users = database.getUserList();
				return new UserListPage(getCurrentUser(req), users)
						.render()
						.toString();
			}

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
		create(database, http);

		new Application(database).run();
	}
}
