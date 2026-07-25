package hr.algebra.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {
    protected abstract T map(ResultSet rs) throws SQLException;

    protected List<T> executeQuery(String call) throws SQLException {
        List<T> result = new ArrayList<>();

        //noinspection SqlSourceToSinkFlow
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
            ResultSet rs = statement.executeQuery();
        ) {
            while (rs.next()) {
                result.add(map(rs));
            }
        }

        return result;
    }
}
