package hr.algebra.dao.repositories.user;

import hr.algebra.dao.models.User;

import java.sql.SQLException;

public interface UserRepository {
    void register(User user) throws SQLException;
    boolean exists(String username) throws SQLException;

    User getByUsername(String username) throws SQLException;
}
