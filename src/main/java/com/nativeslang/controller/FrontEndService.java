package com.nativeslang.controller;

import com.nativeslang.Database;
import com.nativeslang.UserOps;
import com.nativeslang.model.User;
import com.nativeslang.view.AboutPage;
import com.nativeslang.view.IndexPage;
import com.nativeslang.view.SplashPage;
import com.nativeslang.view.UserListPage;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import java.util.List;
import java.util.Optional;

public final class FrontEndService {
    private FrontEndService() {
    }

    private static String aboutPage(Request req, Response res) {
        return new AboutPage(UserOps.getCurrentUser(req)).render().toString();
    }

    private static String indexPage(Request req, Response res) {
        Optional<String> currentUser = UserOps.getCurrentUser(req);
        if (currentUser.isPresent()) {
            return new IndexPage(currentUser)
                .render()
                .toString();
        } else {
            return new SplashPage().render().toString();
        }
    }

    private static Route userList(Database database) {
        return (req, res) -> {
            if (UserOps.isSuperUser(database, req)) {
                List<User> users = database.getUserList();
                return new UserListPage(UserOps.getCurrentUser(req), users)
                    .render()
                    .toString();
            }

            res.redirect("/");
            return "";
        };
    }

    public static void register(Database database, Service service) {
        service.get("/", FrontEndService::indexPage);
        service.get("/about", FrontEndService::aboutPage);
        service.get("/user-list", userList(database));
    }
}
