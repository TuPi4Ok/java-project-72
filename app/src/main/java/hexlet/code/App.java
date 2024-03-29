package hexlet.code;

import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlsController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public final class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }
    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");

        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }
    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine());
        });

        addRouts(app);

        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });
        return app;
    }

    private static void addRouts(Javalin app) {
        app.get("/", RootController.root);
        app.post("/urls", UrlsController.addUrl);
        app.get("/urls", UrlsController.urls);
        app.get("/urls/{id}", UrlsController.show);
        app.post("/urls/{id}/checks", UrlsController.check);
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
