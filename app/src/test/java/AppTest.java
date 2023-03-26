import hexlet.code.App;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AppTest {

    private static Javalin app;
    private static String baseUrl;
    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();

        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

//    @BeforeEach
//    void beforeEach(){
//        Database db = DB.getDefault();
//        db.truncate("url", "url_check");
//    }
//    @Test
//    void testMainUrl() {
//        HttpResponse <String> responsePost = Unirest
//                .post(baseUrl + "/urls")
//                .field("url", "https://www.example.com")
//                .asString();
//        assertThat(responsePost.getStatus()).isEqualTo(302);
//
//        Url actualUrl = new QUrl()
//                .name.eq("https://www.example.com")
//                .findOne();
//
//        assertThat(actualUrl).isNotNull();
//        assertThat(actualUrl.getName()).isEqualTo("https://www.example.com");
//    }

    @Test
    void testGetMainPage() {
        HttpResponse<String> responsePost = Unirest
                .get(baseUrl + "/")
                .asString();
        assertThat(responsePost.getStatus()).isEqualTo(200);
        assertThat(responsePost.getBody()).contains("Бесплатно проверяйте сайты на SEO пригодность");
    }

    @Test
    void testGetUrls() {
        HttpResponse<String> responsePost = Unirest
                .get(baseUrl + "/urls")
                .asString();
        assertThat(responsePost.getStatus()).isEqualTo(200);
        assertThat(responsePost.getBody()).contains("Сайты");
    }

    @Test
    void testGetUrl() {
        HttpResponse<String> responsePost1 = Unirest
                .post(baseUrl + "/urls")
                .field("url", "https://www.example.com")
                .asString();
        assertThat(responsePost1.getStatus()).isEqualTo(302);

        HttpResponse<String> responsePost2 = Unirest
                .get(baseUrl + "/urls/1")
                .asString();

        assertThat(responsePost2.getStatus()).isEqualTo(200);
        assertThat(responsePost2.getBody()).contains("https://www.example.com");
    }

    @Test
    void testUrlCheck() {
        HttpResponse<String> responsePost1 = Unirest
                .post(baseUrl + "/urls")
                .field("url", "https://www.example.com")
                .asString();
        assertThat(responsePost1.getStatus()).isEqualTo(302);

        HttpResponse<String> responsePost2 = Unirest
                .post(baseUrl + "/urls/1/checks")
                .asString();
        assertThat(responsePost2.getStatus()).isEqualTo(302);

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/1")
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Example Domain");
        assertThat(response.getBody()).contains("200");
    }
}
