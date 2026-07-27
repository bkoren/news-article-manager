package hr.algebra.dao.repositories.category;

import hr.algebra.dao.models.Category;

import java.sql.SQLException;
import java.util.List;

public interface CategoryRepository {
    List<Category> read() throws SQLException;

    int create(Category category) throws SQLException;
    int update(Category category) throws SQLException;
    int delete(int categoryId) throws SQLException;
}
