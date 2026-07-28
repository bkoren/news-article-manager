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
    public List<Author> read() throws SQLException {
        return executeQuery("{call p_Author_Read}");
    }

    @Override
    public List<Author> getAuthors(int articleId) throws SQLException {
        return executeQuery(
                "{call p_Article_GetAuthors(?)}",
                statement -> statement.setInt(1, articleId)
        );
    }

    @Override
    public int create(Author author) throws SQLException {
         return executeInsert(
                 "{call p_Author_Create (?)}",
                 statement -> statement.setString(1, author.getName())

         );
    }

    @Override
    public void delete(int authorId) throws SQLException {
        executeUpdate(
            "{call p_Author_Delete (?)}",
            statement -> statement.setInt(1, authorId)
        );
    }

    @Override
    public void update(Author author) throws SQLException {
        executeUpdate(
            "{call p_Author_Update (?, ?)}",
            statement -> {
                statement.setInt(1, author.getAuthorId());
                statement.setString(2, author.getName());
            }
        );
    }
}
