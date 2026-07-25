package hr.algebra.dao.repositories.author;

import hr.algebra.dao.models.Author;

import java.sql.SQLException;
import java.util.List;

public interface AuthorRepository {
    List<Author> getAll() throws SQLException;
}
