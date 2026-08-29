package hr.algebra.dao.models;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("all")
@XmlRootElement(name = "article")
@XmlAccessorType(XmlAccessType.FIELD)
public class Article implements Comparable<Article>, SearchParams{

    @XmlAttribute
    private final int articleId;

    @XmlTransient
    private int sourceId;

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "description")
    private String description = null;

    @XmlElement(name = "link")
    private String link;

    @XmlElement(name = "imagePath")
    private String imagePath = null;

    @XmlTransient
    private LocalDateTime publishedAt = null;

    @XmlElement(name = "publishedAt")
    private String dateForXml = null;

    @XmlElement(name = "source")
    private Source source;

    @XmlTransient
    private List<Author> authors = new ArrayList<>();

    @XmlTransient
    private List<Category> categories = new ArrayList<>();

    private Article() {
        this.articleId = 0;
    }

    public Article(
            int articleId,
            String title,
            String description,
            String link,
            LocalDateTime publishedAt,
            String imagePath,
            Source source
    ) {
        this.articleId = articleId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.publishedAt = publishedAt;
        this.imagePath = imagePath;
        this.source = source;

        if (publishedAt != null) {
            dateForXml = publishedAt.toString();
        }
    }

    public int getArticleId() {
        return articleId;
    }

    public int getSourceId() {
        return (source != null && source.getSourceId() != 0) ? source.getSourceId() : sourceId;
    }

    public Source getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getSearchParam() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public List<Category> getCategories() {
        return categories;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }


    public void addAuthors(List<Author> authors) {
        this.authors.addAll(authors);
    }

    public void addCategories(List<Category> categories) {
        this.categories.addAll(categories);
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Article article)) return false;
        return Objects.equals(link, article.link);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(link);
    }

    @Override
    public int compareTo(Article o) {
        if(this.publishedAt == null && o.publishedAt == null)
            return 0;

        if(this.publishedAt == null)
            return 1;

        if(o.publishedAt == null)
            return -1;

        return o.publishedAt.compareTo(this.publishedAt);
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleId=" + articleId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", publishedAt=" + publishedAt +
                ", imagePath='" + imagePath + '\'' +
                ", source=" + source +
                ", authors=" + authors +
                ", categories=" + categories +
                '}';
    }
}
