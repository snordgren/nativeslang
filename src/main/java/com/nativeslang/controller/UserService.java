package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.EntryPoint;
import spark.Request;
import spark.Response;
import spark.Service;

public class UserService {
	private final Database database;

	public UserService(Database database) {
		this.database = database;
	}

	private String ban(Request req, Response res) {
		if (EntryPoint.isSuperUser(database, req)) {
			String user = req.params(":name");
			if (database.hasUser(user)) {
				database.createHiddenUser(database.getUser(user).getId());
			}
		}

		res.redirect("/");
		return "";
	}

	private String create(Request req, Response res) {
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
	}

	private String connect(Request req, Response res) {
		if (EntryPoint.isLoggedIn(req)) {
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
	}

	private String disconnect(Request req, Response res) {
		req.session().removeAttribute("username");
		res.redirect("/");
		return "";
	}

	public void register(Service service) {
		service.post("/user/ban/:name", this::ban);
		service.post("/user/create", this::create);
		service.post("/user/connect", this::connect);
		service.post("/user/disconnect", this::disconnect);
	}
}
