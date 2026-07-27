package hr.algebra.dao.repositories.category;

import hr.algebra.dao.Base;
import hr.algebra.dao.models.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryRepositoryImpl extends Base<Category> implements CategoryRepository {

    @Override
    protected Category map(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("IDCategory"),
                rs.getString("Name")
        );
    }

    @Override
    public List<Category> read() throws SQLException {
        return executeQuery("{call p_Category_Read}");
    }

    @Override
    public int create(Category category) throws SQLException {
        return executeInsert(
                "{call p_Category_Create(?)}",
                statement -> statement.setString(1, category.getName())
        );
    }

    @Override
    public int update(Category category) throws SQLException {
        return executeUpdate(
                "{call p_Category_Update(?, ?)}",
                statement -> {
                    statement.setInt(1, category.getId());
                    statement.setString(2, category.getName());
                }
        );
    }

    @Override
    public int delete(int categoryId) throws SQLException {
        return executeUpdate(
                "{call p_Category_Delete(?)}",
                statement -> statement.setInt(1, categoryId)
        );
    }
}
