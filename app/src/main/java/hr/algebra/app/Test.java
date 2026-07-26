package hr.algebra.app;

import hr.algebra.dao.models.Author;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws SQLException {

        AuthorRepositoryImpl repository = new AuthorRepositoryImpl();
        repository.save(new Author(("Bruno Koren")));

        List<Author> authors = repository.getAll();

        for (Author author : authors) {
            System.out.println(author.getName());
        }
    }

}
