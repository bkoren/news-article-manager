package hr.algebra.dao.repositories.author;

import hr.algebra.dao.Base;
import hr.algebra.dao.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AuthorRepositoryImpl extends Base<Author> implements AuthorRepository {

    @Override
    protected Author map(ResultSet rs) throws SQLException {
        return new Author(
                rs.getInt("IDAuthor"),
                rs.getString("Name")
        );
    }

    @Override
    public List<Author> getAll() throws SQLException {
        return executeQuery("{call p_Author_GetAll}");
    }
}
