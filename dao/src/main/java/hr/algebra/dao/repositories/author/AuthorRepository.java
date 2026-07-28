package hr.algebra.dao.repositories.author;

import hr.algebra.dao.models.Author;

import java.sql.SQLException;
import java.util.List;

public interface AuthorRepository {
    List<Author> read() throws SQLException;
    List<Author> getAuthors(int articleId) throws SQLException;

    int create(Author author) throws SQLException;
    void update(Author author) throws SQLException;
    void delete(int authorId) throws SQLException;
}
