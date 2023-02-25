package hexlet.code.models;

import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class UrlCheck {
    @Id
    long id;
    long statusCode;
    String title;
    String h1;
    @Lob
    String description;
    @ManyToOne(optional = false)
    Url url;
    @WhenCreated
    Date createdAt;

    public UrlCheck(long statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public long getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getH1() {
        return h1;
    }

    public String getDescription() {
        return description;
    }

    public Url getUrl() {
        return url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getFormatDate() {
        return String.format("%1$te/%1$tm/%1$tY %1$tR", this.getCreatedAt());
    }
}
