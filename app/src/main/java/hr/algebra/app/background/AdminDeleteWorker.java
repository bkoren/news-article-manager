package hr.algebra.app.background;

import hr.algebra.app.forms.AdminPanel;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;
import hr.algebra.dao.rss.RssSource;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class AdminDeleteWorker extends SwingWorker<Integer, String> {
    private JLabel  statusLabel;
    private JButton deleteBtn;

    private SourceRepositoryImpl  sourceRepository  = new SourceRepositoryImpl();
    private ArticleRepositoryImpl articleRepository = new ArticleRepositoryImpl(
                new AuthorRepositoryImpl(),
                new CategoryRepositoryImpl()
        );

    private final boolean usageFromBackend;
    private RssSource source;

    private List<DefaultComboBoxModel<RssSource>> listOfJComboBoxes;

    public AdminDeleteWorker(String feedUrl) {
        for (RssSource rssSource : RssSource.values()) {
            if(Objects.equals(rssSource.getFeedUrl(),feedUrl)) {
                this.source = rssSource;
            }
        }

        this.usageFromBackend = true;
    }

    public AdminDeleteWorker(
            JLabel statusLabel,
            JButton deleteAllBtn,
            RssSource source,
            List<DefaultComboBoxModel<RssSource>> listOfJComboBoxes
    ) {
        this.statusLabel = statusLabel;
        this.deleteBtn   = deleteAllBtn;

        this.source = source;

        this.listOfJComboBoxes = listOfJComboBoxes;

        this.usageFromBackend = false;
    }

    @Override
    protected Integer doInBackground() {
        try {
            if(source == null) {
                articleRepository.deleteAll();
                AdminPanel.setStatusMsg(statusLabel, "Everything deleted successfully.", Color.GREEN);

                deleteBtn.setEnabled(true);
                return 0;
            }

            int sourceId = sourceRepository.readIdByName(source.getName());

            if(sourceId != -1) {
                List<Article> articles = articleRepository.readBySource(sourceId);
                for (Article article : articles) {
                    articleRepository.delete(article.getArticleId());
                }
            }

            sourceRepository.delete(sourceId);

            if(!usageFromBackend) {
                AdminPanel.removeSourceFromJComboBoxes(source, listOfJComboBoxes);
                AdminPanel.setStatusMsg(statusLabel, "Source and it's articles successfully deleted.", Color.GREEN);
            }
        }
        catch (Exception exception) {
            AdminPanel.setStatusMsg(statusLabel, "Error occurred.", Color.RED);
        }

        deleteBtn.setEnabled(true);
        return 0;
    }

}
