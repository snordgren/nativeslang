package com.northerndroid.nativeslang.controller;

import com.northerndroid.nativeslang.Database;
import com.northerndroid.nativeslang.EntryPoint;
import spark.Request;
import spark.Response;
import spark.Service;

public class CommentService {
	private final Database database;

	public CommentService(Database database) {
		this.database = database;
	}

	private String delete(Request req, Response res) {
		String id = req.params(":id");
		if (id != null
				&& id.matches("\\d+")
				&& EntryPoint.isSuperUser(database, req)) {
			long postId = Long.parseLong(id);
			if (database.hasComment(postId)) {
				database.createHiddenComment(postId);
			}
		}
		res.redirect("/");
		return "";
	}

	public void register(Service service) {
		service.post("/comment/delete/:id", this::delete);
	}
}
