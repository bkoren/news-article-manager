package hr.algebra.dao.repositories.user;

import hr.algebra.dao.repositories.Base;
import hr.algebra.dao.models.Role;
import hr.algebra.dao.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public User getByUsername(String username) throws SQLException {
        List<User> user = executeQuery(
            "{call p_User_GetByUsername(?)}",
            statement -> statement.setString(1, username)
        );

        return !user.isEmpty() ? user.getFirst() : null;
    }
}
