package hr.algebra.dao.repositories.source;

import hr.algebra.dao.Base;
import hr.algebra.dao.models.Source;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SourceRepositoryImpl extends Base<Source> implements SourceRepository {

    @Override
    protected Source map(ResultSet rs) throws SQLException {
        return new Source(
                rs.getInt("IDSource"),
                rs.getString("Name"),
                rs.getString("FeedUrl")
        );
    }

    @Override
    public List<Source> read() throws SQLException {
        return executeQuery("{call p_Source_Read}");
    }

    @Override
    public int create(Source source) throws SQLException {
        return executeInsert(
                "{call p_Source_Create (?, ?)}",
                statement -> {
                    statement.setString(1, source.getName());
                    statement.setString(2, source.getFeedUrl());
                }
        );
    }

    @Override
    public int delete(int sourceId) throws SQLException {
        return executeReturn(
                "{? = call p_Source_Delete (?)}",
                statement -> statement.setInt(2, sourceId)
        );
    }

    @Override
    public int update(Source source) throws SQLException {
        return executeUpdate(
                "{call p_Source_Update (?, ?, ?)}",
                statement -> {
                    statement.setInt(1, source.getSourceId());
                    statement.setString(2, source.getName());
                    statement.setString(3, source.getFeedUrl());
                }
        );
    }
}
