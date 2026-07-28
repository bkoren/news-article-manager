package hr.algebra.dao.repositories.user;

import hr.algebra.dao.Base;
import hr.algebra.dao.models.Role;
import hr.algebra.dao.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserRepositoryImpl extends Base<User> implements UserRepository {

    @Override
    protected User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("IDUser"),
                rs.getString("Username"),
                rs.getString("PasswordHash"),
                Role.valueOf(rs.getString("Role"))
        );
    }

    @Override
    public void register(User user) throws SQLException {
        executeUpdate(
                "{call p_User_Register(?, ?)}",
                statement -> {
                    statement.setString(1, user.getUsername());
                    statement.setString(2, user.getPasswordHash());
                }
        );
    }

    @Override
    public boolean exists(String username) throws SQLException {
        return executeReturn(
                "{? = call p_User_Exists(?)}",
                statement -> statement.setString(2, username)
        ) != 0;
    }

    @Override
    public Optional<User> getByUsername(String username) throws SQLException {
        List<User> users = executeQuery(
                "{call p_User_GetByUsername(?)}",
                statement -> statement.setString(1, username));

        return users.isEmpty() ?
                Optional.empty() :
                Optional.of(users.get(0));
    }
}
