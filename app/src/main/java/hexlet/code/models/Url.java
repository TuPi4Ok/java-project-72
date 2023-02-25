package hexlet.code.models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
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
    List<UrlCheck> urlChecks = new ArrayList<>();

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
    public void setUrlChecks(UrlCheck urlCheck) {
        this.urlChecks.add(urlCheck);
    }

    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }
}
