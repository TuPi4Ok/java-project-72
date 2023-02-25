package hexlet.code.controller;

import hexlet.code.models.Url;
import hexlet.code.models.query.QUrl;
import io.javalin.http.Handler;

import java.net.URL;
import java.util.Objects;
import java.util.Set;

public class UrlsController {
    private static int getIdFromPath(String path) {
        String[] ids = path.split("/");
        return Integer.parseInt(ids[ids.length - 1]);
    }

    public static Handler addUrl = ctx -> {
        String getUrl = ctx.formParam("url");
        try{
            URL url = new URL(Objects.requireNonNull(getUrl));
            String itogUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1? "": ( ":" + url.getPort()));

            Url findUrl = new QUrl()
                    .name.eq(itogUrl)
                    .findOne();

            if (findUrl != null) {
                ctx.sessionAttribute("flash", "Страница уже существует");
            } else {
                Url newUrl = new Url(itogUrl);
                newUrl.save();
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.redirect("/urls?page=1");
                return;
            }
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
        }
        ctx.redirect("/");
    };

    public static Handler urls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int offset = (page - 1) * 10;
        Set<Url> urls = new QUrl()
                .id.greaterThan(offset)
                .orderBy("id")
                .setMaxRows(10)
                .findSet();

        int countUrls = new QUrl()
                .findCount();
        ctx.attribute("urls", urls);
        ctx.attribute("page", page);
        ctx.attribute("count", countUrls);
        ctx.render("urls.html");
    };

    public static Handler show = ctx -> {
        int id = getIdFromPath(ctx.path());
        Url findUrl = new QUrl()
                .id.eq(id)
                .findOne();

        ctx.attribute("url", findUrl);
        String formattedDate = String.format("%1$te/%1$tm/%1$tY %1$tR", findUrl.getCreatedAt());
        ctx.attribute("date", formattedDate);
        ctx.render("show.html");
    };
}
