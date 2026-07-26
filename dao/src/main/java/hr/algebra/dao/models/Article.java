package hr.algebra.dao.models;

import javax.xml.transform.Source;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Article implements Comparable<Article> {
    private final int articleId;
    private int SourceId;
    private String title;
    private String description = null;
    private String link;
    private LocalDateTime publishedAt = null;
    private String imagePath = null;

    private Source source;
    private List<Author> authors = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public Article(
            int articleId,
            int sourceId,
            String title,
            String description,
            String link,
            LocalDateTime publishedAt,
            String imagePath,
            Source source,
            List<Author> authors,
            List<Category> categories) {
        this.articleId = articleId;
        SourceId = sourceId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.publishedAt = publishedAt;
        this.imagePath = imagePath;
        this.source = source;
        this.authors = authors;
        this.categories = categories;
    }

    @Override
    public int compareTo(Article o) {
        return 0;
    }
}
