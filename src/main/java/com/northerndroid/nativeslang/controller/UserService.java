package com.northerndroid.nativeslang.controller;

import com.northerndroid.nativeslang.Database;
import com.northerndroid.nativeslang.EntryPoint;
import spark.Request;
import spark.Response;
import spark.Service;

public class UserService {
	private final Database database;

	public UserService(Database database) {
		this.database = database;
	}

	private String create(Request req, Response res) {
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
	}

	private String connect(Request req, Response res) {
		if (EntryPoint.isLoggedIn(req)) {
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
	}

	private String disconnect(Request req, Response res) {
		req.session().removeAttribute("username");
		res.redirect("/");
		return "";
	}

	public void register(Service service) {
		service.post("/user/create", this::create);
		service.post("/user/connect", this::connect);
		service.post("/user/disconnect", this::disconnect);
	}
}
