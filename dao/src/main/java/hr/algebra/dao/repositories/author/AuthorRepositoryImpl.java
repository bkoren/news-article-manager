package hr.algebra.dao.repositories.author;

import hr.algebra.dao.repositories.BaseRepository;
import hr.algebra.dao.models.Author;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuthorRepositoryImpl extends BaseRepository<Author> implements AuthorRepository {

    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    @Override
    protected Author map(ResultSet databaseResult) throws SQLException {
        return new Author(
                databaseResult.getInt("IDAuthor"),
                databaseResult.getString("Name"),
                (hasColumn(databaseResult, "ArticlesCount") ?
                        databaseResult.getInt("ArticlesCount") : 0
                )
        );
    }

    @Override
    public List<Author> read() throws SQLException {
        return executeRead("{call p_Author_Read}");
    }

    @Override
    public List<Author> getAuthors(int articleId) throws SQLException {
        return executeRead(
                "{call p_Article_GetAuthors(?)}",
                statement -> statement.setInt(1, articleId)
        );
    }

    @Override
    public int create(Author author) throws SQLException {
        if(author.getName().isEmpty()) {
            return -1;
        }

        int id = executeInsert(
                 "{call p_Author_Create(?)}",
                 statement -> statement.setString(1, author.getName())
        );

        triggerListeners();

        return id;
    }

    @Override
    public void delete(int authorId) throws SQLException {
        executeUpdate(
            "{call p_Author_Delete (?)}",
            statement -> statement.setInt(1, authorId)
        );

        triggerListeners();
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

        triggerListeners();
    }


    private boolean hasColumn(ResultSet databaseResult, String column) throws SQLException {
        ResultSetMetaData table = databaseResult.getMetaData();

        for(int i = 1; i <= table.getColumnCount(); i++) {
            if(column.equalsIgnoreCase(table.getColumnLabel(i))) {
                return true;
            }
        }

        return false;
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
