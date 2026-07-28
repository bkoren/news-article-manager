package hr.algebra.dao.repositories.category;

import hr.algebra.dao.models.Category;

import java.sql.SQLException;
import java.util.List;

public interface CategoryRepository {
    List<Category> read() throws SQLException;
    List<Category> getCategories(int articleId) throws SQLException;

    int create(Category category) throws SQLException;
    void update(Category category) throws SQLException;
    void delete(int categoryId) throws SQLException;
}
