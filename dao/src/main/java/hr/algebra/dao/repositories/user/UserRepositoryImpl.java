package hr.algebra.dao.repositories.user;

import hr.algebra.dao.repositories.BaseRepository;
import hr.algebra.dao.models.Role;
import hr.algebra.dao.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {

    @Override
    protected User map(ResultSet databaseResult) throws SQLException {
        return new User(
                databaseResult.getInt("IDUser"),
                databaseResult.getString("Username"),
                databaseResult.getString("PasswordHash"),
                Role.valueOf(databaseResult.getString("Role"))
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
        return !executeRead(
                "{call p_User_Exists(?)}",
                statement -> statement.setString(1, username)
        ).isEmpty();
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        List<User> user = executeRead(
            "{call p_User_GetByUsername(?)}",
            statement -> statement.setString(1, username)
        );

        return !user.isEmpty() ? user.getFirst() : null;
    }
}
