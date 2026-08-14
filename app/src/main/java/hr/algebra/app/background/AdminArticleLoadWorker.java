package hr.algebra.app.background;

import hr.algebra.app.forms.AdminPanel;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.rss.RssImportService;
import hr.algebra.dao.rss.RssSource;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminArticleLoadWorker extends SwingWorker<Integer, String> {
    RssImportService service;
    RssSource        source;

    private final JLabel  statusLabel;
    private final JButton loadBtn;

    public AdminArticleLoadWorker(RssImportService service, JLabel statusLabel, JButton loadBtn) {
        this.statusLabel = statusLabel;
        this.service     = service;
        this.loadBtn     = loadBtn;

        this.source = null;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        return (source == null) ?
                service.importFromAll():
                service.importFrom(source);
    }

    @Override
    protected void done() {
        try {
            int result = get();
            if ((result > 0)) {
                AdminPanel.setStatusMsg(statusLabel, "Successfully imported " + result + " articles.", Color.GREEN);
            }
            else {
                AdminPanel.setStatusMsg(statusLabel, "Articles from selected source are already downloaded.", null);
            }

        }
        catch (Exception exception) {
            AdminPanel.setStatusMsg(statusLabel, "Error occurred.", Color.RED);
        }

        loadBtn.setEnabled(true);
    }

    public void setSource(RssSource source) {
        this.source = source;
    }
}
