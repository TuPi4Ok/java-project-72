package hexlet.code.models;

import hexlet.code.models.query.QUrlCheck;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Entity
public class Url extends Model {
    @Id
    long id;
    String name;
    @WhenCreated
    Date createdAt;
    @OneToMany(mappedBy = "url")
    List<UrlCheck> urlChecks;

    public Url(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getFormatDate() {
        return String.format("%1$te/%1$tm/%1$tY %1$tR", this.getCreatedAt());
    }

    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }

    public void setUrlChecks(List<UrlCheck> urlChecks) {
        this.urlChecks = urlChecks;
    }

    public long getLastStatusCode() {
        UrlCheck urlCheck = new QUrlCheck()
                .url.eq(this)
                .orderBy()
                .createdAt.desc()
                .setMaxRows(1)
                .findOne();
        if (urlCheck != null) {
            return urlCheck.getStatusCode();
        }
        return 0;
    }

    public String getLastDate() {
        UrlCheck urlCheck = new QUrlCheck()
                .url.eq(this)
                .orderBy()
                .createdAt.desc()
                .setMaxRows(1)
                .findOne();
        if (urlCheck != null) {
            return urlCheck.getFormatDate();
        }
        return "";
    }
}
