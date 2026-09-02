package hr.algebra.app.background;

import hr.algebra.app.panels.AdminPanel;
import hr.algebra.dao.rss.RssImportService;
import hr.algebra.dao.rss.RssSource;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public class AdminLoadWorker extends SwingWorker<Integer, String> {
    RssImportService service;
    RssSource        source;

    private final JComboBox<RssSource> sources;
    private final ButtonGroup buttonGroup;
    private final JLabel  statusLabel;
    private final JButton loadBtn;

    public AdminLoadWorker(
            RssImportService service,
            JComboBox<RssSource> sources,
            ButtonGroup buttonGroup,
            JLabel statusLabel,
            JButton loadBtn)
    {
        this.statusLabel     = statusLabel;
        this.service         = service;
        this.loadBtn         = loadBtn;
        this.sources         = sources;
        this.buttonGroup = buttonGroup;
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

        AdminPanel.setBusy(false);
        loadBtn.setEnabled(true);
        sources.setEnabled(true);
        Enumeration<AbstractButton> options = buttonGroup.getElements();
        while (options.hasMoreElements()) {
            options.nextElement().setEnabled(true);
        }
    }

    public void setSource(RssSource source) {
        this.source = source;
    }
}
