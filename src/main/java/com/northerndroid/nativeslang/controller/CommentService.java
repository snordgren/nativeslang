package com.northerndroid.nativeslang.controller;

import com.northerndroid.nativeslang.Database;
import com.northerndroid.nativeslang.EntryPoint;
import com.northerndroid.nativeslang.model.User;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.Optional;

public class CommentService {
	private final Database database;

	public CommentService(Database database) {
		this.database = database;
	}

	private String delete(Request req, Response res) {
		String id = req.params(":id");
		if (isId(id) && EntryPoint.isSuperUser(database, req)) {
			long postId = Long.parseLong(id);
			if (database.hasComment(postId)) {
				database.createHiddenComment(postId);
			}
		}
		return "";
	}

	private boolean isId(String id) {
		return id != null && id.matches("\\d+");
	}

	public void register(Service service) {
		service.post("/comment/delete/:id", this::delete);
		service.post("/comment/report/:id", this::report);
	}

	private String report(Request req, Response res) {
		String id = req.params(":id");
		if (isId(id) && EntryPoint.isLoggedIn(req)) {
			long commentId = Long.parseLong(id);
			Optional<String> currentUser = EntryPoint.getCurrentUser(req);
			if (currentUser.isPresent() && database.hasComment(commentId)) {
				User user = database.getUser(currentUser.get());
				if (!database.hasCommentReport(user.getId(), commentId)) {
					database.createCommentReport(user.getId(), commentId);
					int commentReportCount = database.getCommentReportCount(commentId);
					if (commentReportCount >= 5) {
						database.createHiddenComment(commentId);
					}
				}
			}
		}
		res.redirect("/");
		return "";
	}
}
