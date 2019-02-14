package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.EntryPoint;
import com.nativeslang.UserOps;
import com.nativeslang.model.User;
import com.nativeslang.view.EditUserPage;
import com.nativeslang.view.UserPage;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import java.util.Optional;

public final class UserService {
	private UserService() {
	}

	private static Route ban(Database database) {
		return (req, res) -> {
            if (UserOps.isSuperUser(database, req)) {
                String user = req.params(":name");
                if (database.hasUser(user)) {
                    database.createHiddenUser(database.getUser(user).getId());
                }
            }

            res.redirect("/");
            return "";
        };
	}

	private static Route create(Database database) {
	    return (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            System.out.println("Register request received");
            if (username == null || password == null) {
                System.out.println("Username " + username + ", password " + password + ", one null.");
                res.redirect("/sign-in");
            } else if (database.hasUser(username)) {
                System.out.println("Username unavailable.");
                res.redirect("/sign-in/username-taken");
            } else {
                database.createUser(username, password);
                req.session().attribute("username", username);
                System.out.println("Logged in successfully.");
                res.redirect("/");
            }

            return "";
        };
	}

	private static Route connect(Database database) {
	    return (req, res) -> {
            if (UserOps.isLoggedIn(req)) {
                res.redirect("/");
                return "";
            } else {
                String username = req.queryParams("username");
                String password = req.queryParams("password");
                if (username == null || password == null) {
                    res.redirect("/sign-in");
                } else if (database.isLoginValid(username, password)) {
                    req.session().attribute("username", username);
                    res.redirect("/");
                } else {
                    res.redirect("/sign-in/login-error");
                }

                return "";
            }
        };
	}

	private static String disconnect(Request req, Response res) {
		req.session().removeAttribute("username");
		res.redirect("/");
		return "";
	}

	private static Route update(Database database) {
	    return (req, res) -> {
            String name = req.params(":name");
            if (name != null && database.hasUser(name)) {
                User user = database.getUser(name);
                Optional<String> currentUser = UserOps.getCurrentUser(req);
                String description = req.queryParams("description");
                if (currentUser.isPresent()
                    && currentUser.get().equalsIgnoreCase(name)
                    && description != null) {
                    database.createUserDescription(user.getId(), description);
                }
            }
            res.redirect(User.url(name));
            return "";
        };
    }

    private static Route viewUser(Database database) {
	    return (req, res) -> {
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
                boolean isSuperUser = UserOps.isSuperUser(database, req);

                return new UserPage(user,
                    description,
                    UserOps.getCurrentUser(req),
                    isSameUser,
                    isSuperUser).render().toString();
            } else {
                res.redirect("/");
                return "";
            }
        };
    }

	public static void register(Database database, Service service) {
		service.post("/user/ban/:name", ban(database));
		service.post("/user/create", create(database));
		service.post("/user/connect", connect(database));
		service.post("/user/disconnect", UserService::disconnect);
		service.post("/user/update/:name", update(database));
        service.get("/user/:name", viewUser(database));
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
                        UserOps.getCurrentUser(req),
                        description)
                        .render()
                        .toString();
                }
            }

            res.redirect("/");
            return "";
        });
	}
}
