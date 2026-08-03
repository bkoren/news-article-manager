package hr.algebra.dao.repositories;

import hr.algebra.utilities.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {

    private static volatile ConnectionProvider instance;

    private final String base;
    private final String database;
    private final String user;
    private final String password;
    private final String certificate;

    private ConnectionProvider() {
         base = Config.get("db.url");
         database = Config.get("db.databaseName");
         user = Config.get("db.user");
         password = Config.get("db.password");
         certificate = Config.get("db.trustServerCertification");
    }

    public static ConnectionProvider getInstance() {
        if(instance == null) {
            instance = new ConnectionProvider();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        String url = base +
                ";databaseName=" + database +
                ";trustServerCertificate=" + certificate;

        return DriverManager.getConnection(url, user, password);
    }
}
