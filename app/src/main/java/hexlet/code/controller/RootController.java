package hexlet.code.controller;

import io.javalin.http.Handler;

public class RootController {

    public static Handler root = ctx -> {
        ctx.render("index.html");
    };
}
