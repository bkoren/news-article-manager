package hr.algebra.app.forms;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.rss.RssImportService;
import hr.algebra.dao.rss.RssSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class AdminPanel extends JPanel {
    JPanel stack = new JPanel();

    RssImportService importService;

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
        deleteBtn.putClientProperty("FlatLaf.style", "background: #8c2828; foreground: #fff; margin: 6,14,6,14");
        deleteBtn.setAlignmentX(LEFT_ALIGNMENT);

        card.add(deleteBtn);

        return card;
    }

    private Component deleteSourceCard() {
        JPanel card = customPanel();

        card.add(buildTitle("Delete source"));
        card.add(buildDescription("Removes the selected source and all of its articles and images"));

        JComboBox<Source> sourcesComboBox = new JComboBox<Source>();
        sourcesComboBox.setMaximumSize(new Dimension(250, sourcesComboBox.getPreferredSize().height));
        Font f = sourcesComboBox.getFont();
        sourcesComboBox.setFont(f.deriveFont(f.getSize() + 5f));

        //Add sources here

        card.add(labeledColumn("Source", sourcesComboBox));

        JButton deleteBtn = new JButton("Delete source");
        deleteBtn.putClientProperty("FlatLaf.style", "background: #8c2828; foreground: #fff; margin: 6,14,6,14");
        deleteBtn.setAlignmentX(LEFT_ALIGNMENT);

        card.add(deleteBtn);

        return card;
    }

    private Component reloadSourceCard() {
        JPanel card = customPanel();

        card.add(buildTitle("Reload sources"));
        card.add(buildDescription("Refreshes the source list from configuration and re-checks each feed URL."));

        JButton reloadBtn = new JButton("Reload sources");
        reloadBtn.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #fff; margin: 6,14,6,14");
        reloadBtn.setAlignmentX(LEFT_ALIGNMENT);

        card.add(reloadBtn);

        return card;
    }

    private Component loadArticlesCard() {
        JPanel card = customPanel();

        JLabel title = buildTitle("Load new articles");
        title.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

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

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        options.setAlignmentX(LEFT_ALIGNMENT);
        options.setOpaque(false);
        options.add(allSourcesToggle);
        options.add(Box.createHorizontalStrut(8));
        options.add(oneSourceToggle);

        JComboBox<RssSource> loadArticlesComboBox = new JComboBox<RssSource>();
        Font font = loadArticlesComboBox.getFont();

        loadArticlesComboBox.setPreferredSize(new Dimension(180, loadArticlesComboBox.getPreferredSize().height));
        loadArticlesComboBox.setFont(font.deriveFont(font.getSize() + 5f));

        for(RssSource source : importService.getAllSources()) {
            loadArticlesComboBox.addItem(source);
        }

        JPanel comboBoxes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboBoxes.setAlignmentX(LEFT_ALIGNMENT);
        comboBoxes.setOpaque(false);
        comboBoxes.add(labeledColumn("Source", loadArticlesComboBox));
        comboBoxes.add(Box.createHorizontalStrut(12));

        JButton loadBtn  = new JButton("Load articles");
        loadBtn.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #fff; margin: 6,14,6,14");
        loadBtn.setAlignmentX(LEFT_ALIGNMENT);

        oneSourceToggle.addActionListener(e -> {
            loadArticlesComboBox.setEnabled(true);
        });

        allSourcesToggle.addActionListener(e -> {
            loadArticlesComboBox.setEnabled(false);
        });
        allSourcesToggle.doClick();

        loadBtn.addActionListener(e -> {
            ButtonModel selected = buttonGroup.getSelection();
            try {
                if (Objects.equals(selected.getActionCommand(), "all")) {
                    importService.importFromAll();
                }
                else if (Objects.equals(selected.getActionCommand(), "one")) {
                    importService.importFrom(
                            (RssSource) loadArticlesComboBox.getSelectedItem()
                    );
                }
            }
            catch (AssetException ex) {
                throw new RuntimeException(ex);
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        card.add(title);
        card.add(options);
        card.add(Box.createVerticalStrut(12));
        card.add(Box.createHorizontalStrut(5));
        card.add(comboBoxes);
        card.add(loadBtn);

        return card;
    }

    private JPanel customPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        result.setAlignmentX(LEFT_ALIGNMENT);

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

    private JPanel labeledColumn(String label, JComponent field) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        column.add(l);
        column.add(Box.createVerticalStrut(8));
        column.add(field);
        column.add(Box.createVerticalStrut(8));

        return column;
    }
}
