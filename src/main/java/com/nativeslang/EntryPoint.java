package com.nativeslang;

import com.nativeslang.controller.CommentService;
import com.nativeslang.controller.FrontEndService;
import com.nativeslang.controller.LanguageService;
import com.nativeslang.controller.PostService;
import com.nativeslang.controller.SignInService;
import com.nativeslang.controller.UserService;
import spark.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class EntryPoint {
    public static final String[] languages = {
        "english",
        "french",
        "german",
        "italian",
        "spanish",
        "portuguese",
        "russian",
        "swedish",
        "chinese",
        "japanese",
        "korean",
    };

    private static Properties readProperties() {
        Properties properties = new Properties();
        properties.setProperty("httpPort", "8080");
        File file = new File("properties.xml");
        if (file.exists()) {
            try {
                properties.loadFromXML(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                file.createNewFile();
                properties.storeToXML(new FileOutputStream(file), "", "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    private static void create(Database database, Service service) {
        final long expireTime = 60 * 60 * 24 * 30;
        service.staticFiles.externalLocation("resources/public/");
        service.staticFiles.expireTime(expireTime);
        service.staticFiles.header("Content-Encoding", "gzip");
        service.staticFiles.header("Cache-Control", "public, max-age=" + expireTime);
        service.staticFiles.header("ETag", "0x123456");
        service.staticFiles.header("Vary", "Accept-Encoding");

        service.after((req, res) -> {
            res.header("Content-Encoding", "gzip");
        });

        Arrays.stream(languages).forEach(language -> {
            new LanguageService(language, database).register(service);
        });

        PostService.register(database, service);
        CommentService.register(database, service);
        UserService.register(database, service);
        FrontEndService.register(database, service);
        SignInService.register(database, service);
    }

    public static void main(String[] args) {
        Properties properties = readProperties();
        Database database = Database.newInFile("test/test");

        Service http = Service.ignite();
        http.port(Integer.parseInt(properties.getProperty("httpPort")));
        create(database, http);
    }
}
