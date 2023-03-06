package hexlet.code.controller;

import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.models.query.QUrl;
import hexlet.code.models.query.QUrlCheck;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        int pageCount = countUrls / 10 + 1;
        List<Integer> pageList = new ArrayList<>();
        for(int i = 1; i <= pageCount; i++) {
            pageList.add(i);
        }

        ctx.attribute("urls", urls);
        ctx.attribute("page", page);
        ctx.attribute("count", countUrls);
        ctx.attribute("pageList", pageList);
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
        urlChecks = urlChecks.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        lst -> {
                            Collections.reverse(lst);
                            return lst.stream();
                        }
                ))
                .toList();
        ctx.attribute("url", findUrl);
        if(urlChecks != null && !urlChecks.isEmpty()) {
            ctx.attribute("urlsCheck", urlChecks);
        }
        ctx.render("show.html");
    };

    private static String getTitle(HttpResponse responsePost) {
        String body = responsePost.getBody().toString();
        //Pattern pattern = Pattern.compile("(?<=<title.{0,}>).{1,}(?=<\\/title>)", Pattern.DOTALL);
        Pattern pattern = Pattern.compile("<title.{0,}>.{1,}?<\\/title>");
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            return body.substring(matcher.start(), matcher.end()).replaceAll("(<title.*?>|<\\/title>)", "");
        }
        return "";
    }

    private static String getH1(HttpResponse responsePost) {
        String body = responsePost.getBody().toString();
//        Pattern pattern = Pattern.compile("(?<=<h1.{0,}>).{1,}(?=<\\/h1>)");
        Pattern pattern = Pattern.compile("<h1.{0,}?>.{1,}?<\\/h1>");
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            return body.substring(matcher.start(), matcher.end()).replaceAll("(<h1.*?>|<\\/h1>)", "");
        }
        return "";
    }

    private static String getDescription(HttpResponse responsePost) {
        String body = responsePost.getBody().toString();
//        Pattern pattern = Pattern.compile("(?<=<meta name=\"description\" content=\").+?(?=\" \\/>)");
        Pattern pattern = Pattern.compile("<meta name=\"description\" content=\".+?\".*?\\/>");
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()) {
            return body.substring(matcher.start(), matcher.end()).replaceAll("(<meta name=\"description\" content=\"|\" \\/>)", "");
        }
        return "";
    }

    public static Handler check = ctx -> {
        int id = getIdFromPathCheck(ctx.path());
        Url findUrl = new QUrl()
                .id.eq(id)
                .findOne();
        String name = findUrl.getName();

        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        Unirest.config().httpClient(httpClient);
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
