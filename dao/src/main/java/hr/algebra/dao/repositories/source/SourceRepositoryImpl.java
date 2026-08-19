package hr.algebra.dao.repositories.source;

import hr.algebra.dao.repositories.BaseRepository;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.repositories.StatementBinder;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class SourceRepositoryImpl extends BaseRepository<Source> implements SourceRepository {

    @Override
    protected Source map(ResultSet databaseResult) throws SQLException {
        return new Source(
                databaseResult.getInt("IDSource"),
                databaseResult.getString("Name"),
                databaseResult.getString("FeedUrl")
        );
    }

    @Override
    public List<Source> read() throws SQLException {
        return executeRead("{call p_Source_Read}");
    }

    @Override
    public int create(Source source) throws SQLException {
        if(source.getName().isEmpty()) {
            return -1;
        }

        return executeInsert(
                "{call p_Source_Create (?, ?)}",
                statement -> {
                    statement.setString(1, source.getName());
                    statement.setString(2, source.getFeedUrl());
                }
        );
    }

    @Override
    public int delete(String name) throws SQLException {
        try {
            return Integer.parseInt(
                    executeDelete(
                        "{call p_Source_Delete(?)}",
                        statement -> {
                            statement.setString(1, name);
                        }
                    )
            );
        }
        catch (NumberFormatException exception) {
            return -1;
        }
    }

    @Override
    public void update(Source source) throws SQLException {
        executeUpdate(
            "{call p_Source_Update (?, ?, ?)}",
            statement -> {
                statement.setInt(1, source.getSourceId());
                statement.setString(2, source.getName());
                statement.setString(3, source.getFeedUrl());
            }
        );
    }
}
