package hr.algebra.dao.repositories.article;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Article;

import java.sql.SQLException;
import java.util.List;

public interface ArticleRepository {
    List<Article> read() throws SQLException;

    int create(Article article) throws SQLException;
    void update(Article article) throws SQLException;
    void delete(int articleId) throws SQLException, AssetException;
}
