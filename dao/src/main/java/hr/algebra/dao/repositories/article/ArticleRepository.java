package hr.algebra.dao.repositories.article;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Source;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ArticleRepository {
    List<Article> read(int sourceId) throws SQLException;

    int create(Article article) throws SQLException;
    void update(Article article) throws SQLException;
    void delete(int articleId) throws SQLException, AssetException;
}
