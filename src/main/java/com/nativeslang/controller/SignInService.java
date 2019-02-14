package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.UserOps;
import com.nativeslang.view.SignInPage;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.Optional;

public final class SignInService {
    private SignInService() {

    }

    private static String signIn(String msg, Request req, Response res) {
        Optional<String> currentUser = UserOps.getCurrentUser(req);
        if (currentUser.isPresent()) {
            res.redirect("/");
            return "";
        } else {
            return new SignInPage(msg).render().toString();
        }
    }

    public static void register(Database database, Service service) {
        service.get("/sign-in", (req, res) -> signIn("", req, res));

        service.get("/sign-in/login-error", (req, res) ->
            signIn("Wrong username or password.", req, res));

        service.get("/sign-in/username-taken", (req, res) ->
            signIn("Username is taken.", req, res));
    }
}
