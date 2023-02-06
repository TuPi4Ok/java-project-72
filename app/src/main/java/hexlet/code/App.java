package hexlet.code;

import io.javalin.Javalin;

public class App {
    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        addRouts(app);
        return app;
    }

    private static void addRouts(Javalin app) {
        app.get("/", ctx -> ctx.result("Hello world"));
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(5050);
    }
}
