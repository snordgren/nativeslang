package com.nativeslang;

import spark.Request;

import java.util.Optional;

public final class UserOps {
    private UserOps() {
    }

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
}
