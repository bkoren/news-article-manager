package hr.algebra.dao.repositories.article;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.repositories.BaseRepository;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Author;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.repositories.author.AuthorRepository;
import hr.algebra.dao.repositories.category.CategoryRepository;
import hr.algebra.dao.rss.AssetService;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArticleRepositoryImpl extends BaseRepository<Article> implements ArticleRepository {
    private final AuthorRepository authorRepo;
    private final CategoryRepository categoryRepo;

    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public ArticleRepositoryImpl(AuthorRepository authorRepo, CategoryRepository categoryRepo) {
        this.authorRepo = authorRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    protected Article map(ResultSet databaseResult) throws SQLException {
        Source source = new Source(
                databaseResult.getInt("IDSource"),
                databaseResult.getString("Name"),
                databaseResult.getString("FeedUrl")
        );

        Timestamp publishedAt = databaseResult.getTimestamp("PublishedAt");
        LocalDateTime castPublishedAt = publishedAt != null
                ? publishedAt.toLocalDateTime()
                : null;

        return new Article(
                databaseResult.getInt("IDArticle"),
                databaseResult.getString("Title"),
                databaseResult.getString("Description"),
                databaseResult.getString("Link"),
                castPublishedAt,
                databaseResult.getString("ImagePath"),
                source
        );
    }

    @Override
    public List<Article> read(int sourceId) throws SQLException {
        List<Article> articles = executeRead(
                "{call p_Article_Read(?)}",
                statement -> statement.setInt(1, sourceId)
        );

        for(Article a : articles) {
            a.setAuthors(authorRepo.getAuthors(a.getArticleId()));
            a.setCategories(categoryRepo.getCategories(a.getArticleId()));
        }

        return articles;
    }

    @Override
    public int create(Article article) throws SQLException {
        if(article.getLink() == null) {
            return -1;
        }

        int id = executeInsert(
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
        if(id == -1) {
            return id;
        }

        List<Author> authors = article.getAuthors();
        for(Author author : authors) {
            executeUpdate(
                "{call p_Article_AddAuthor(?, ?)}",
                statement -> {
                    statement.setInt(1, id);
                    statement.setInt(2, author.getAuthorId());
                }
            );
        }

        List<Category> categories = article.getCategories();
        for(Category category : categories) {
            executeUpdate(
                    "{call p_Article_AddCategory(?, ?)}",
                    statement -> {
                        statement.setInt(1, id);
                        statement.setInt(2, category.getCategoryId());
                    }
            );
        }

        return id;
    }

    @Override
    public void update(Article article) throws SQLException {
        executeUpdate(
                "{call p_Article_ClearAuthors(?)}",
                statement -> statement.setInt(1, article.getArticleId())
        );

        executeUpdate(
                "{call p_Article_ClearCategories(?)}",
                statement -> statement.setInt(1, article.getArticleId())
        );

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

        List<Author> authors = article.getAuthors();
        for (Author author : authors) {
            executeUpdate(
                    "{call p_Article_AddAuthor(?, ?)}",
                    statement -> {
                        statement.setInt(1, article.getArticleId());
                        statement.setInt(2, author.getAuthorId());
                    }
            );
        }

        List<Category> categories = article.getCategories();
        for (Category category : categories) {
            executeUpdate(
                    "{call p_Article_AddCategory(?, ?)}",
                    statement -> {
                        statement.setInt(1, article.getArticleId());
                        statement.setInt(2, category.getCategoryId());
                    }
            );
        }

        triggerListeners();
    }

    @Override
    public void delete(int articleId) throws SQLException, AssetException {
        if(articleId == 0) {
            executeDelete("{call p_DeleteAllData()}");

            new AssetService().clearFolder();

            triggerListeners();
            return;
        }

        String ImagePath = executeDelete(
            "{call p_Article_Delete(?)}",
            statement -> statement.setInt(1, articleId)
        );

        new AssetService().removeImage(ImagePath);

        triggerListeners();
    }


    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void triggerListeners() {
        SwingUtilities.invokeLater(() -> {
            for(Runnable listener : listeners) {
                listener.run();
            }
        });
    }
}
