package hr.algebra.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementBinder {
    void bind(CallableStatement statement) throws SQLException;
}
