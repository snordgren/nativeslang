package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.UserOps;
import com.nativeslang.model.Post;
import com.nativeslang.model.User;
import spark.Route;
import spark.Service;

public class PostService {
    private PostService() {
    }

    private static Route comment(Database database) {
        return (req, res) -> {
            String id = req.params(":id");
            if (UserOps.isLoggedIn(req)) {
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
        };
    }

    private static Route delete(Database database) {
        return (req, res) -> {
            String id = req.params(":id");

            if (id != null && id.matches("\\d+") && UserOps.isSuperUser(database, req)) {

                long postId = Long.parseLong(id);

                if (database.hasPost(postId)) {
                    database.createHiddenPost(postId);
                }
            }
            res.redirect("/");
            return "";
        };
    }

    public static void register(Database database, Service service) {
        service.post("/post/comment/:id", comment(database));
        service.post("/post/delete/:id", delete(database));
    }
}
