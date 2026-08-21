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
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RssImportService {
    private final RssItemMapper mapper;
    private final AssetService asset;

    SourceRepositoryImpl     sourceRepository;
    AuthorRepository         authorRepository;
    CategoryRepository       categoryRepository;
    ArticleRepositoryImpl    articleRepository;

    private final RssSource[] allSources;

    public RssImportService(
            SourceRepositoryImpl sourceRepository,
            AuthorRepositoryImpl authorRepository,
            CategoryRepositoryImpl categoryRepository,
            ArticleRepositoryImpl articleRepository
    ) throws AssetException {
        mapper = new RssItemMapper();
        asset  = new AssetService();

        this.sourceRepository = sourceRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;

        allSources = RssSource.values();
    }

    public int importFromAll() throws SQLException, ParserConfigurationException, IOException, SAXException {
        for(RssSource source : allSources) {
            importFrom(source);
        }

        return allSources.length;
    }

    public int importFrom(RssSource source) throws ParserConfigurationException, IOException, SAXException, SQLException {
        RssParser parser = new RssParser(source);

        List<Callable<ParsedItem>> tasks = new ArrayList<>();
        for (RssItem item : parser.parseItems()) {
            tasks.add(() ->
                    mapper.map(
                            item,
                            asset.downloadImage(
                                    item.imageUrl(),
                                    parser.giveBackImgExt()
                            ),
                            source
                    )
            );
        }

        List<ParsedItem> parsed = new ArrayList<>();
        try(ExecutorService pool = Executors.newFixedThreadPool(5)) {
            for(Future<ParsedItem> items : pool.invokeAll(tasks)) {
                parsed.add(items.get());
            }
        }
        catch (Exception exception) {
            System.out.println("Error occurred wile downloading image, image was rejected. (Error is ignorable)");
        }

        return exportToDB(parsed);
    }

    private int exportToDB(List<ParsedItem> parsed) throws SQLException {
        int sumOfImports = 0;
        int sourceId = sourceRepository.create(parsed.getFirst().source());
        for (ParsedItem parsedItem : parsed) {
            Article article = parsedItem.article();
            article.setSourceId(sourceId);

            List<Author> linkedAuthors = new ArrayList<>();
            for (Author author : parsedItem.authors()) {
                int authorId = authorRepository.create(author);
                if(authorId != -1) {
                    linkedAuthors.add(new Author(authorId, author.getName()));
                }
            }
            article.addAuthors(linkedAuthors);

            List<Category> linkedCategories = new ArrayList<>();
            for (Category category : parsedItem.categories()) {
                int categoryId = categoryRepository.create(category);
                if(categoryId != -1) {
                    linkedCategories.add(new Category(categoryId, category.getName()));
                }
            }
            article.addCategories(linkedCategories);

            int articleId = articleRepository.create(article);

            if(articleId != 0) {
                sumOfImports++;
            }
        }

        articleRepository.triggerListeners();

        return sumOfImports;
    }

    public RssSource[] getAllSources() {
        return allSources;
    }
}
