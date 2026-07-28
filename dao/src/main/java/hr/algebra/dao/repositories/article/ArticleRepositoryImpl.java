package hr.algebra.dao.repositories.article;

import hr.algebra.dao.Base;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.repositories.author.AuthorRepository;
import hr.algebra.dao.repositories.category.CategoryRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class ArticleRepositoryImpl extends Base<Article> implements ArticleRepository {
    private final AuthorRepository authorRepo;
    private final CategoryRepository categoryRepo;

    public ArticleRepositoryImpl(AuthorRepository authorRepo, CategoryRepository categoryRepo) {
        this.authorRepo = authorRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    protected Article map(ResultSet rs) throws SQLException {
        Source source = new Source(
                rs.getInt("IDSource"),
                rs.getString("Name"),
                rs.getString("FeedUrl")
        );

        Timestamp publishedAt = rs.getTimestamp("PublishedAt");
        LocalDateTime castPublishedAt = publishedAt != null
                ? publishedAt.toLocalDateTime()
                : null;

        return new Article(
                rs.getInt("IDArticle"),
                rs.getString("Title"),
                rs.getString("Description"),
                rs.getString("Link"),
                castPublishedAt,
                rs.getString("ImagePath"),
                source
        );
    }

    @Override
    public List<Article> read() throws SQLException {
        List<Article> articles = executeQuery("{call p_Article_Read}");
        for(Article a : articles) {
            a.setAuthors(authorRepo.getAuthors(a.getArticleId()));
            a.setCategories(categoryRepo.getCategories(a.getArticleId()));
        }

        return articles;
    }

    @Override
    public int create(Article article) throws SQLException {
        return executeInsert(
                "{call p_Article_Create(?, ?, ?, ?, ?, ?)}",
                statement -> {
                    statement.setInt(1, article.getSourceId());
                    statement.setString(2, article.getTitle());
                    statement.setString(3, article.getDescription());
                    statement.setString(4, article.getLink());
                    statement.setTimestamp(5, Timestamp.valueOf(article.getPublishedAt()));
                    statement.setString(6, article.getImagePath());
                }
        );
    }

    @Override
    public void update(Article article) throws SQLException {
        executeUpdate(
            "{call p_Article_Update(?, ?, ?, ?, ?, ?, ?)}",
            statement -> {
                statement.setInt(1, article.getArticleId());
                statement.setInt(2, article.getSourceId());
                statement.setString(3, article.getTitle());
                statement.setString(4, article.getDescription());
                statement.setString(5, article.getLink());
                statement.setTimestamp(6, Timestamp.valueOf(article.getPublishedAt()));
                statement.setString(7, article.getImagePath());
            }
        );
    }

    @Override
    public void delete(int articleId) throws SQLException {
        executeUpdate(
            "{call p_Article_Delete(?)}",
            statement -> statement.setInt(1, articleId)
        );
    }
}
