package hr.algebra.dao.models;

import java.util.Objects;

public class Author implements SearchParams {
    private final int authorId;

    private String name;
    private int articlesCount;

    public Author(int authorId, String name) {
        this.authorId = authorId;
        this.name = name;
    }
    public Author(int authorId, String name, int articlesCount) {
        this.authorId = authorId;
        this.name = name;
        this.articlesCount = articlesCount;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getArticlesCount() {
        return articlesCount;
    }

    public String getName() {
        return name;
    }

    public String getSearchParam() { return name; }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Author author)) return false;
        return authorId == author.authorId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorId);
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", name='" + name + '\'' +
                '}';
    }
}
