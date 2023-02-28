package hexlet.code.controller;

import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.models.query.QUrl;
import hexlet.code.models.query.QUrlCheck;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlsController {
    private static int getIdFromPath(String path) {
        String[] ids = path.split("/");
        return Integer.parseInt(ids[ids.length - 1]);
    }

    private static int getIdFromPathCheck(String path) {
        String[] ids = path.split("/");
        return Integer.parseInt(ids[ids.length - 2]);
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

        List<UrlCheck> urlChecks = new QUrlCheck()
                .url.eq(findUrl)
                .findList();

        ctx.attribute("url", findUrl);
        if(urlChecks != null && !urlChecks.isEmpty()) {
            ctx.attribute("urlsCheck", urlChecks);
        }
        ctx.render("show.html");
    };

    private static String getTitle(HttpResponse responsePost) {
        String body = responsePost.getBody().toString();
        Pattern pattern = Pattern.compile("(?<=<title.{0,}>).{1,}(?=<\\/title>)");
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            return body.substring(matcher.start(), matcher.end()).replaceAll("<.{0,}>", "");
        }
        return "";
    }

    private static String getH1(HttpResponse responsePost) {
        String body = responsePost.getBody().toString();
        Pattern pattern = Pattern.compile("(?<=<h1.{0,}>).{1,}(?=<\\/h1>)");
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            return body.substring(matcher.start(), matcher.end()).replaceAll("<.{0,}>", "");
        }
        return "";
    }

    private static String getDescription(HttpResponse responsePost) {
        String body = responsePost.getBody().toString();
        Pattern pattern = Pattern.compile("(?<=<meta name=\"description\" content=\").+?(?=\" \\/>)");
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            return body.substring(matcher.start(), matcher.end()).replaceAll("<.{0,}>", "");
        }
        return "";
    }

    public static Handler check = ctx -> {
        int id = getIdFromPathCheck(ctx.path());
        Url findUrl = new QUrl()
                .id.eq(id)
                .findOne();
        String name = findUrl.getName();

        HttpResponse<String> responsePost = Unirest
                .get(name)
                .asString();


        UrlCheck urlCheck = new UrlCheck(responsePost.getStatus(), getTitle(responsePost), getH1(responsePost), getDescription(responsePost), findUrl);
        findUrl.getUrlChecks().add(urlCheck);
        urlCheck.save();
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.redirect("/urls/" + id);
    };
}
