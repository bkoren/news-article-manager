package hr.algebra.dao.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Article implements Comparable<Article>{
    private final int articleId;
    private String title;
    private String description = null;
    private String link;
    private LocalDateTime publishedAt = null;
    private String imagePath = null;

    private int sourceId;

    private Source source;
    private List<Author> authors = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

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
    }

    public int getArticleId() {
        return articleId;
    }

    public int getSourceId() {
        return (source != null && source.getSourceId() != 0) ? source.getSourceId() : sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Category> getCategories() {
        return categories;
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
