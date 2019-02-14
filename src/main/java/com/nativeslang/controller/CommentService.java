package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.UserOps;
import com.nativeslang.model.User;
import spark.Route;
import spark.Service;

import java.util.Optional;

public class CommentService {
    private final Database database;

    public CommentService(Database database) {
        this.database = database;
    }

    private static Route delete(Database database) {
        return (req, res) -> {
            String id = req.params(":id");

            if (isId(id) && UserOps.isSuperUser(database, req)) {
                long postId = Long.parseLong(id);
                if (database.hasComment(postId)) {
                    database.createHiddenComment(postId);
                }
            }
            return "";
        };
    }

    private static boolean isId(String id) {
        return id != null && id.matches("\\d+");
    }

    private static Route report(Database database) {
        return (req, res) -> {
            String id = req.params(":id");

            if (isId(id) && UserOps.isLoggedIn(req)) {

                long commentId = Long.parseLong(id);
                Optional<String> currentUser = UserOps.getCurrentUser(req);

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
        };
    }

    public static void register(Database database, Service service) {
        service.post("/comment/delete/:id", delete(database));
        service.post("/comment/report/:id", report(database));
    }
}
