package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
@IgnoreExtraProperties
public class HealthNews implements java.io.Serializable{
    private String newId;
    private String title;
    private String content;
    private String image;
//    private Date publishedDate;
    @com.google.firebase.firestore.PropertyName("pulishedDate")
    private Date publishedDate;

    public HealthNews() {
    }

    public HealthNews(String newId, String title, String content, String image, Date publishedDate) {
        this.newId = newId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.publishedDate = publishedDate;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
}
