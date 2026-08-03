package hr.algebra.dao.repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Base<T> {
    protected abstract T map(ResultSet rs) throws SQLException;

    protected List<T> executeQuery(String call) throws SQLException {
        List<T> result = new ArrayList<>();
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

    protected List<T> executeQuery(String call, StatementBinder binder) throws SQLException {
        List<T> result = new ArrayList<>();
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            binder.bind(statement);
            try(ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        }

        return result;
    }

    protected void executeUpdate(String call, StatementBinder binder) throws SQLException {
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            binder.bind(statement);
            statement.executeUpdate();
        }
    }

    protected int executeInsert(String call, StatementBinder binder) throws SQLException {
        try(
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            binder.bind(statement);

            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) return rs.getInt(1);
            }

            return -1;
        }
    }

    protected int executeReturn(String call, StatementBinder binder) throws SQLException {
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            statement.registerOutParameter(1, Types.INTEGER);

            binder.bind(statement);
            statement.execute();

            return statement.getInt(1);
        }
    }

    protected String executeDelete(String call, StatementBinder binder) throws SQLException {
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            binder.bind(statement);
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()) return rs.getString("ImagePath");
            }

            return null;
        }
    }
}
