package hr.algebra.dao.repositories;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlSourceToSinkFlow", "SameParameterValue"})
public abstract class BaseRepository<T> {
    protected List<Runnable> listeners;

    protected abstract T map(ResultSet databaseResult) throws SQLException;

    protected List<T> executeRead(String call) throws SQLException {
        List<T> result = new ArrayList<>();
        try (
                Connection connection = ConnectionProvider.getInstance().getConnection();
                CallableStatement statement = connection.prepareCall(call);
                ResultSet databaseResult = statement.executeQuery();
        ) {
            while (databaseResult.next()) {
                result.add(map(databaseResult));
            }
        }

        return result;
    }

    protected List<T> executeRead(String call, StatementBinder binder) throws SQLException {
        List<T> result = new ArrayList<>();
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            binder.bind(statement);
            try(ResultSet databaseResult = statement.executeQuery()) {
                while (databaseResult.next()) {
                    result.add(map(databaseResult));
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

            try(ResultSet databaseResult = statement.executeQuery()) {
                if(databaseResult.next()) {
                    return databaseResult.getInt(1);
                }
            }

            return -1;
        }
    }

    protected String executeDelete(String call, StatementBinder binder) throws SQLException {
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            binder.bind(statement);
            try(ResultSet databaseResult = statement.executeQuery()){
                if(databaseResult.next()) {

                    if(hasColumn(databaseResult, "ImagePath")) {
                        return databaseResult.getString("ImagePath");
                    }

                    if(hasColumn(databaseResult, "Success")) {
                        return databaseResult.getString("Success");
                    }
                }
            }

            return null;
        }
    }

    protected void executeDelete(String call) throws SQLException {
        try (
            Connection connection = ConnectionProvider.getInstance().getConnection();
            CallableStatement statement = connection.prepareCall(call);
        ) {
            statement.executeUpdate();
        }
    }


    protected boolean hasColumn(ResultSet databaseResult, String column) throws SQLException {
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

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public void triggerListeners() {
        SwingUtilities.invokeLater(() -> {
            for(Runnable listener : listeners) {
                listener.run();
            }
        });
    }
}
