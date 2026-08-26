package hr.algebra.app.background;

import hr.algebra.app.panels.AdminPanel;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;
import hr.algebra.dao.rss.AssetService;
import hr.algebra.dao.rss.RssSource;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AdminDeleteWorker extends SwingWorker<Integer, String> {
    private JLabel  statusLabel;
    private JButton deleteBtn;

    private SourceRepositoryImpl  sourceRepository;
    private ArticleRepositoryImpl articleRepository;
    private AuthorRepositoryImpl authorRepository;
    private CategoryRepositoryImpl categoryRepository;

    private RssSource source;

    private List<DefaultComboBoxModel<RssSource>> listOfJComboBoxes;
    private final boolean usageFromBackend;

    public AdminDeleteWorker(String feedUrl) {
        for (RssSource rssSource : RssSource.values()) {
            if(Objects.equals(rssSource.getFeedUrl(),feedUrl)) {
                this.source = rssSource;
            }
        }

        this.usageFromBackend = true;
    }

    public AdminDeleteWorker(
            ArticleRepositoryImpl articleRepository,
            SourceRepositoryImpl sourceRepository,
            AuthorRepositoryImpl authorRepository,
            CategoryRepositoryImpl categoryRepository,
            RssSource source,
            JLabel statusLabel,
            JButton deleteAllBtn,
            List<DefaultComboBoxModel<RssSource>> listOfJComboBoxes
    ) {
        this.statusLabel = statusLabel;
        this.deleteBtn   = deleteAllBtn;

        this.source = source;
        this.articleRepository = articleRepository;
        this.sourceRepository = sourceRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;

        this.listOfJComboBoxes = listOfJComboBoxes;

        this.usageFromBackend = false;
    }

    @Override
    protected void done(){
        if(deleteBtn != null) {
            deleteBtn.setEnabled(true);
        }

        AdminPanel.setBusy(false);
    }

    @Override
    protected Integer doInBackground() {
        try {
            if(source != null) {
                int sourceId = sourceRepository.delete(source.getName());

                if(sourceId != 0) {
                    throw new SQLException("Source not found!");
                }

                articleRepository.triggerListeners();
                articleRepository.triggerAuthors();
                articleRepository.triggerCategories();
            }
            else {
                articleRepository.delete(0);
                AdminPanel.setStatusMsg(statusLabel, "Everything deleted successfully.", Color.GREEN);

                authorRepository.triggerListeners();
                categoryRepository.triggerListeners();

                deleteBtn.setEnabled(true);
                return 0;
            }

            if(usageFromBackend) {
               return 0;
            }

            AdminPanel.removeSourceFromJComboBoxes(source, listOfJComboBoxes);
            AdminPanel.setStatusMsg(statusLabel, "Source and it's articles successfully deleted.", Color.GREEN);
        }
        catch (Exception exception) {
            AdminPanel.setStatusMsg(statusLabel, "Error occurred.", Color.RED);
        }

        return 0;
    }
}
