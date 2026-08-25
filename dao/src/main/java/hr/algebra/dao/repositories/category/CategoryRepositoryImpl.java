package hr.algebra.dao.repositories.category;

import hr.algebra.dao.repositories.BaseRepository;
import hr.algebra.dao.models.Category;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CategoryRepositoryImpl extends BaseRepository<Category> implements CategoryRepository {

    public CategoryRepositoryImpl() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    protected Category map(ResultSet databaseResult) throws SQLException {
        return new Category(
                databaseResult.getInt("IDCategory"),
                databaseResult.getString("Name"),
                (hasColumn(databaseResult, "ArticlesCount") ?
                        databaseResult.getInt("ArticlesCount") : 0
                )
        );
    }

    @Override
    public List<Category> read() throws SQLException {
        return executeRead("{call p_Category_Read}");
    }

    @Override
    public List<Category> getCategories(int articleId) throws SQLException {
        return executeRead(
                "{call p_Article_GetCategories(?)}",
                statement -> statement.setInt(1, articleId)
        );
    }

    @Override
    public int create(Category category) throws SQLException {
        if(category.getName().isEmpty()) {
            return -1;
        }

        int id = executeInsert(
                "{call p_Category_Create(?)}",
                statement -> statement.setString(1, category.getName())
        );

        triggerListeners();

        return id;
    }

    @Override
    public void update(Category category) throws SQLException {
        executeUpdate(
            "{call p_Category_Update(?, ?)}",
            statement -> {
                statement.setInt(1, category.getCategoryId());
                statement.setString(2, category.getName());
            }
        );

        triggerListeners();
    }

    @Override
    public void delete(int categoryId) throws SQLException {
        executeUpdate(
            "{call p_Category_Delete(?)}",
            statement -> statement.setInt(1, categoryId)
        );

        triggerListeners();
    }

    public void triggerCategories() {
        triggerListeners();
    }
}
