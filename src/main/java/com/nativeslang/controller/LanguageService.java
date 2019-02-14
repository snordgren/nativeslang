package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.UserOps;
import com.nativeslang.model.Comment;
import com.nativeslang.model.Post;
import com.nativeslang.view.LanguagePage;
import com.nativeslang.view.PostPage;
import com.nativeslang.view.ViewPostPage;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LanguageService {
    private final String language;
    private final Database database;

    public LanguageService(String language, Database database) {
        this.language = language;
        this.database = database;
    }

    private String createPost(Request req, Response res) {
        String title = req.queryParams("title");
        String description = req.queryParams("description");
        Optional<String> currentUser = UserOps.getCurrentUser(req);

        currentUser.ifPresent(username -> {
            if (title == null) {
                System.out.println("Title was null.");
            } else if (description == null) {
                System.out.println("Description was null.");
            } else {
                database.createPost(language.toLowerCase(),
                    username,
                    title,
                    description);
            }
        });
        res.redirect("/" + language.toLowerCase());
        return "";
    }

    private String indexPage(Request req, Response res) {
        List<Post> posts = database.getPostsByLanguage(language);
        List<Integer> commentCounts = posts.stream()
            .map(p -> database.getCommentCount(p.getId()))
            .collect(Collectors.toList());
        LanguagePage languagePage = new LanguagePage(language, posts,
            commentCounts, UserOps.getCurrentUser(req));
        return languagePage.render().toString();
    }

    private String post(Request req, Response res) {
        PostPage postPage = new PostPage(language, UserOps.getCurrentUser(req));
        if (UserOps.isLoggedIn(req)) {
            return postPage.render().toString();
        } else {
            res.redirect("/sign-in");
            return "";
        }
    }

    private String showPost(Request req, Response res) {
        String id = req.params(":id");
        Objects.requireNonNull(id);
        if (id.matches("\\d+")) {
            long postId = Long.parseLong(id);
            if (database.hasPost(language.toLowerCase(), postId)) {
                Post post = database.getPost(language.toLowerCase(), postId);
                List<Comment> comments = database.getComments(post.getId());
                return new ViewPostPage(post,
                    comments,
                    UserOps.getCurrentUser(req),
                    UserOps.isSuperUser(database, req)).render().toString();
            }
        }

        res.redirect("/");
        return "";
    }

    public void register(Service service) {
        service.get("/" + language, this::indexPage);

        service.path("/" + language.toLowerCase(), () -> {
            service.get("/post", this::post);
            service.get("/post/:id", this::showPost);
            service.get("/post/:id/*", this::showPost);
            service.post("/post/create", this::createPost);
        });
    }
}
