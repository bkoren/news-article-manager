package hr.algebra.dao.repositories.user;

import hr.algebra.dao.models.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {
    void register(User user) throws SQLException;
    boolean exists(String username) throws SQLException;

    Optional<User> getByUsername(String username) throws SQLException;
}
