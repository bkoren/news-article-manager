package hr.algebra.dao.repositories.source;

import hr.algebra.dao.models.Source;

import java.sql.SQLException;
import java.util.List;

public interface SourceRepository {
    List<Source> read() throws SQLException;

    int create(Source source) throws SQLException;
    void update(Source source) throws SQLException;
    void delete(int sourceId) throws SQLException;
}
