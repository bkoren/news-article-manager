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
import hr.algebra.utilities.gui.DialogUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class RssImportService {
    private final RssItemMapper mapper;

    private AssetService asset;

    AuthorRepository         authorRepository;
    CategoryRepository       categoryRepository;
    SourceRepositoryImpl     sourceRepository;
    ArticleRepositoryImpl    articleRepository;

    private List<Source> sourcesFromApp;

    private final RssSource[] allSources;

    private int sourceId;

    public RssImportService(
            AuthorRepository      authorRepository,
            CategoryRepository    categoryRepository,
            SourceRepositoryImpl  sourceRepository,
            ArticleRepositoryImpl articleRepository
    )  {
        mapper = new RssItemMapper();
        sourcesFromApp = new ArrayList<>();

        try {
            asset = new AssetService();
        }
        catch (AssetException exception) {
            asset = null;
        }

        this.sourceRepository = sourceRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;

        allSources = RssSource.values();
    }

    public int importFromAll() throws SQLException, ParserConfigurationException, IOException, SAXException, AssetException {
        int sumOfDownloadedArticles = 0;
        for(RssSource source : allSources) {
            sumOfDownloadedArticles += importFrom(source);
        }

        return sumOfDownloadedArticles;
    }

    public int importFrom(RssSource source) throws ParserConfigurationException, IOException, SAXException, SQLException, AssetException {
        /*List<Article> articles = articleRepository.read(sourceId);
        for (Article article : articles) {
            removeImage(article.getImagePath());
        }*/

        List<Callable<ParsedItem>> tasks = getCallables(source);

        List<ParsedItem> parsed = new ArrayList<>();
        try(ExecutorService pool = Executors.newFixedThreadPool(5)) {
            for(Future<ParsedItem> items : pool.invokeAll(tasks)) {
                parsed.add(items.get());
            }
        }
        catch (Exception exception) {
            //ignore
        }

        return exportToDB(parsed);
    }

    private List<Callable<ParsedItem>> getCallables(RssSource source) throws ParserConfigurationException, IOException, SAXException {
        List<Callable<ParsedItem>> tasks = new ArrayList<>();

        RssParser parser = new RssParser(source);
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
        return tasks;
    }

    public void importAllSourcesToDB() throws SQLException {
        for(RssSource source : allSources) {
            int id = sourceRepository.create(
                    new Source(
                            0,
                            source.getName(),
                            source.getFeedUrl()
                    )
            );

            sourcesFromApp.add(
                    new Source(
                            id,
                            source.getName(),
                            source.getFeedUrl()
                    )
            );
        }
    }

    public void importAllSourcesToApp() throws SQLException {
        sourcesFromApp = sourceRepository.read();
    }

    public RssSource[] getAllSources() {
        return allSources;
    }


    private int exportToDB(List<ParsedItem> parsed) throws SQLException {
        int sumOfImports = 0;

        importAllSourcesToDB();
        importAllSourcesToApp();

        Source sourceFromApp = sourcesFromApp.stream()
                .filter(source -> Objects.equals(source.getName(), parsed.getFirst().source().getName()))
                .findFirst()
                .orElse(null);


        assert sourceFromApp != null;
        sourceId = sourceFromApp.getSourceId();

        for (ParsedItem parsedItem : parsed) {
            Article article = parsedItem.article();
            article.setSourceId(sourceId);

            List<Author> linkedAuthors = new ArrayList<>();
            for (Author author : parsedItem.authors()) {
                int authorId = authorRepository.create(author);
                if(authorId != -1) {
                    linkedAuthors.add(new Author(
                            authorId,
                            author.getName()
                    ));
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

        return sumOfImports;
    }


    public boolean isAssetValid() {
        return asset != null;
    }

    public void clearSources() {
        sourcesFromApp.clear();
    }

    public int getSourceId(String name) throws SQLException {
        if(sourcesFromApp.isEmpty()) {
            importAllSourcesToApp();
        }

        return sourcesFromApp.stream()
                .filter(source -> Objects.equals(source.getName(), name))
                .findFirst()
                .orElseThrow()
                .getSourceId();
    }

    public void removeImage(String imagePath) throws AssetException {
        asset.removeImage(imagePath);
    }
}
