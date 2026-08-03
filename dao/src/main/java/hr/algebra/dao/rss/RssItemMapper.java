package hr.algebra.dao.rss;

import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Author;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.models.Source;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class RssItemMapper {
    public ParsedItem map(RssItem item, String imagePath, RssSource rssSource) {
        Source source = toSource(rssSource);

        return new ParsedItem(
                toArticle(item, imagePath, source),
                source,
                toAuthors(item),
                toCategories(item)
        );
    }

    Article toArticle(RssItem item, String imagePath, Source source) {
        return new Article(
            0,
            item.title(),
            item.description(),
            item.link(),
            toDate(item.pubDate()),
            imagePath,
            source
        );
    }

    LocalDateTime toDate(String pubDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
            ZonedDateTime zone = ZonedDateTime.parse(pubDate, formatter);

            return zone.toLocalDateTime();
        }
        catch (DateTimeParseException exception) {
            return  null;
        }
    }

    Source toSource(RssSource source) {
        return new Source(0, source.getName(), source.getFeedUrl());
    }

    List<Author> toAuthors(RssItem item) {
        List<Author> authors = new ArrayList<>();
        for (String author : item.authors()) {
            authors.add(new Author(0, author));
        }

        return authors;
    }

    List<Category> toCategories(RssItem item) {
        List<Category> categories = new ArrayList<>();
        for (String category : item.categories()) {
            categories.add(new Category(0, category));
        }

        return categories;
    }
}
