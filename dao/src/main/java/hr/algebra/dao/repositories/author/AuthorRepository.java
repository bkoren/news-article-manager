package hr.algebra.dao.repositories.author;

import hr.algebra.dao.models.Author;

import java.sql.SQLException;
import java.util.List;

public interface AuthorRepository {
    List<Author> getAll() throws SQLException;

    int create(Author author) throws SQLException;
    int delete(int authorId) throws SQLException;
    int update(Author author) throws SQLException;
}
