package hr.algebra.app.forms;

import hr.algebra.app.background.ArticleImportWorker;
import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.rss.RssImportService;
import hr.algebra.dao.rss.RssSource;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class AdminPanel extends JPanel {
    JPanel stack = new JPanel();

    RssImportService importService;

    ArticleImportWorker articleImportWorker;

    public AdminPanel() {
        try {
            importService = new RssImportService();
        }
        catch (AssetException exception) {
            //Display error!
        }

        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout());

        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(deleteAllCard());
        stack.add(deleteSourceCard());
        stack.add(reloadSourceCard());
        stack.add(loadArticlesCard());

        add(stack, BorderLayout.NORTH);
    }

    private Component deleteAllCard() {
        JPanel card = customPanel();

        card.add(buildTitle("Delete all data"));
        card.add(buildDescription(
                "Removes all articles, authors, categories and their images."
        ));

        JButton deleteBtn = new JButton("Delete all data");
        deleteBtn.putClientProperty("FlatLaf.style", "background: #8c2828; foreground: #fff; margin: 4,12,4,12");
        deleteBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel deleteAllSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        deleteAllSection.setAlignmentX(LEFT_ALIGNMENT);
        deleteAllSection.setOpaque(false);
        deleteAllSection.add(deleteBtn);
        deleteAllSection.add(Box.createHorizontalStrut(50));

        JLabel status = statusMsg("Loading...", null);
        deleteAllSection.add(status);

        card.add(deleteAllSection);

        return card;
    }

    private Component deleteSourceCard() {
        JPanel card = customPanel();

        card.add(buildTitle("Delete source"));
        card.add(buildDescription("Removes the selected source and all of its articles and images"));

        JComboBox<Source> sourcesComboBox = new JComboBox<Source>();
        sourcesComboBox.setMaximumSize(new Dimension(250, sourcesComboBox.getPreferredSize().height));
        Font font = sourcesComboBox.getFont();
        sourcesComboBox.setFont(font.deriveFont(font.getSize() + 5f));

        //Add sources here

        card.add(labeledColumn(sourcesComboBox, "Source"));

        JButton deleteBtn = new JButton("Delete source");
        deleteBtn.putClientProperty("FlatLaf.style", "background: #8c2828; foreground: #fff; margin: 4,12,4,12");
        deleteBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel deleteSourceSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        deleteSourceSection.setAlignmentX(LEFT_ALIGNMENT);
        deleteSourceSection.setOpaque(false);
        deleteSourceSection.add(deleteBtn);
        deleteSourceSection.add(Box.createHorizontalStrut(50));

        JLabel status = statusMsg("Loading...", null);
        deleteSourceSection.add(status);

        card.add(deleteSourceSection);

        return card;
    }

    private Component reloadSourceCard() {
        JPanel card = customPanel();

        card.add(buildTitle("Reload sources"));
        card.add(buildDescription("Refreshes the source list from configuration and re-checks each feed URL."));

        JButton reloadBtn = new JButton("Reload sources");
        reloadBtn.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #fff; margin: 4,12,4,12");
        reloadBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel reloadSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        reloadSection.setAlignmentX(LEFT_ALIGNMENT);
        reloadSection.setOpaque(false);
        reloadSection.add(reloadBtn);
        reloadSection.add(Box.createHorizontalStrut(50));

        JLabel status = statusMsg("Loading...", null);
        reloadSection.add(status);

        card.add(reloadSection);

        return card;
    }

    private Component loadArticlesCard() {
        JPanel card = customPanel();

        JLabel title = buildTitle("Load new articles");
        title.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));

        JToggleButton allSourcesToggle = new JToggleButton("All sources");
        JToggleButton oneSourceToggle  = new JToggleButton("One source");

        allSourcesToggle.putClientProperty("FlatLaf.style",
                "margin: 5,5,5,5; selectedBackground: #2563eb; selectedForeground: #fff");
        oneSourceToggle.putClientProperty("FlatLaf.style",
                "margin: 5,5,5,5; selectedBackground: #2563eb; selectedForeground: #fff");

        ButtonGroup buttonGroup = new ButtonGroup();
        allSourcesToggle.setActionCommand("all");
        oneSourceToggle.setActionCommand("one");
        buttonGroup.add(allSourcesToggle);
        buttonGroup.add(oneSourceToggle);

        JPanel optionsSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optionsSection.setAlignmentX(LEFT_ALIGNMENT);
        optionsSection.setOpaque(false);
        optionsSection.add(allSourcesToggle);
        optionsSection.add(Box.createHorizontalStrut(8));
        optionsSection.add(oneSourceToggle);

        JComboBox<RssSource> sourcesForLoadArticles = new JComboBox<RssSource>();
        Font fontSources = sourcesForLoadArticles.getFont();

        sourcesForLoadArticles.setPreferredSize(new Dimension(250, sourcesForLoadArticles.getPreferredSize().height));
        sourcesForLoadArticles.setFont(fontSources.deriveFont(fontSources.getSize() + 3f));

        for(RssSource source : importService.getAllSources()) {
            sourcesForLoadArticles.addItem(source);
        }

        JPanel comboBoxSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboBoxSection.setAlignmentX(LEFT_ALIGNMENT);
        comboBoxSection.setOpaque(false);
        comboBoxSection.add(labeledColumn(sourcesForLoadArticles, "Source"));
        comboBoxSection.add(Box.createVerticalStrut(60));

        JButton loadBtn  = new JButton("Load articles");
        loadBtn.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #fff; margin: 6,14,6,14");
        loadBtn.setAlignmentX(LEFT_ALIGNMENT);

        JPanel loadBtnSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loadBtnSection.setAlignmentX(LEFT_ALIGNMENT);
        loadBtnSection.setOpaque(false);
        loadBtnSection.add(Box.createVerticalStrut(8));
        loadBtnSection.add(loadBtn);
        loadBtnSection.add(Box.createHorizontalStrut(50));

        JLabel status = new JLabel();
        loadBtnSection.add(status);

        oneSourceToggle.addActionListener(e -> {
            sourcesForLoadArticles.setEnabled(true);
        });

        allSourcesToggle.addActionListener(e -> {
            sourcesForLoadArticles.setEnabled(false);
        });
        allSourcesToggle.doClick();

        loadBtn.addActionListener(e -> {
            articleImportWorker = new ArticleImportWorker(importService, status);

            ButtonModel selected = buttonGroup.getSelection();
            try {
                if (Objects.equals(selected.getActionCommand(), "all")) {
                    articleImportWorker.execute();
                }
                else if (Objects.equals(selected.getActionCommand(), "one")) {
                    articleImportWorker.setSource((RssSource) sourcesForLoadArticles.getSelectedItem());
                    articleImportWorker.execute();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        card.add(title);
        card.add(optionsSection);
        card.add(Box.createVerticalStrut(12));
        card.add(Box.createHorizontalStrut(5));
        card.add(comboBoxSection);
        card.add(loadBtnSection);
        card.add(Box.createVerticalStrut(12));

        return card;
    }

    private JPanel customPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        result.setAlignmentX(LEFT_ALIGNMENT);
        result.add(Box.createVerticalStrut(10));

        return result;
    }

    private JLabel buildTitle(String title) {
        JLabel result = new JLabel(title);
        result.setFont(result.getFont().deriveFont(Font.BOLD));
        result.setAlignmentX(LEFT_ALIGNMENT);

        return result;
    }

    private JTextArea buildDescription(String description) {
        JTextArea result = new JTextArea(description);
        result.setRows(2);
        result.setEditable(false);
        result.setFocusable(false);
        result.setOpaque(false);
        result.setAlignmentX(Component.LEFT_ALIGNMENT);
        result.setMargin(new Insets(10, 0, 0, 0));

        return result;
    }

    private JPanel labeledColumn(JComponent field, String labelMsg) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);

        JLabel label = new JLabel(labelMsg);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        column.add(label);
        column.add(Box.createVerticalStrut(6));
        column.add(field);
        column.add(Box.createVerticalStrut(6));

        return column;
    }

    private JLabel statusMsg(String msg, Color color) {
        JLabel result = new JLabel(msg);
        Font fontStatusBold = result.getFont().deriveFont(Font.BOLD);
        Font fontStatusSize  = fontStatusBold.deriveFont(15f);

        result.setAlignmentX(RIGHT_ALIGNMENT);
        result.setFont(fontStatusSize);
        result.setForeground(color);

        return result;
    }
}
