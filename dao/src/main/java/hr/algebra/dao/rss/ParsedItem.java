package hr.algebra.dao.rss;

import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Author;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.models.Source;
import java.util.List;

public record ParsedItem (
    Article article,
    Source source,
    List<Author> authors,
    List<Category> categories
) {
    public ParsedItem {
        categories = List.copyOf(categories);
        authors    = List.copyOf(authors);
    }
}
