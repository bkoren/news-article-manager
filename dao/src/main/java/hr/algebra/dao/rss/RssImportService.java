package hr.algebra.dao.rss;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Author;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.repositories.author.AuthorRepository;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.dao.repositories.category.CategoryRepository;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RssImportService {
    private final RssItemMapper mapper;
    private final AssetService asset;

    public RssImportService() throws AssetException {
        mapper = new RssItemMapper();
        asset  = new AssetService();
    }

    public void importFrom(RssSource source)
            throws ParserConfigurationException, IOException, SAXException, AssetException, SQLException {

        RssParser parser = new RssParser(source);
        SourceRepositoryImpl sourceRepository = new SourceRepositoryImpl();

        List<Source> sources = sourceRepository.read();
        boolean preventDownload = sources.contains(
                new Source(0, source.getName(), source.getFeedUrl())
        );

        List<ParsedItem> parsed = new ArrayList<>();
        for (RssItem item : parser.parseItems()) {
            parsed.add(mapper.map(
                    item,
                    preventDownload ? null : asset.downloadImage(item.imageUrl()),
                    source)
            );
        }

        exportToDB(parsed);
    }

    private void exportToDB(List<ParsedItem> parsed) throws SQLException {
        SourceRepositoryImpl     sourceRepository     = new SourceRepositoryImpl();
        AuthorRepository         authorRepository     = new AuthorRepositoryImpl();
        CategoryRepository       categoryRepository   = new CategoryRepositoryImpl();
        ArticleRepositoryImpl    articleRepository    = new ArticleRepositoryImpl(
                authorRepository, categoryRepository
        );

        int sourceId = sourceRepository.create(parsed.getFirst().source());
        for (ParsedItem parsedItem : parsed) {
            Article article = parsedItem.article();
            article.setSourceId(sourceId);

            List<Author> linkedAuthors = new ArrayList<>();
            for (Author author : parsedItem.authors()) {
                int authorId = authorRepository.create(author);

                linkedAuthors.add(new Author(authorId, author.getName()));
            }
            article.addAuthors(linkedAuthors);

            List<Category> linkedCategories = new ArrayList<>();
            for (Category category : parsedItem.categories()) {
                int categoryId = categoryRepository.create(category);

                linkedCategories.add(new Category(categoryId, category.getName()));
            }
            article.addCategories(linkedCategories);

            int articleId = articleRepository.create(article);
        }
    }
}
