package hexlet.code.controller;

import io.javalin.http.Handler;

public final class RootController {

    public final static Handler root = ctx -> {
        ctx.render("index.html");
    };
}
