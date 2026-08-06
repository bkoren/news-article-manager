package hr.algebra.app.forms;

import hr.algebra.dao.models.Source;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    JPanel stack = new JPanel();

    public AdminPanel() {
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
        card.add(title);

        JToggleButton allSourcesToggle = new JToggleButton("All sources");
        JToggleButton oneSourceToggle  = new JToggleButton("One source");

        allSourcesToggle.putClientProperty("FlatLaf.style",
                "margin: 5,5,5,5; selectedBackground: #2563eb; selectedForeground: #fff");
        oneSourceToggle.putClientProperty("FlatLaf.style",
                "margin: 5,5,5,5; selectedBackground: #2563eb; selectedForeground: #fff");

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(allSourcesToggle);
        modeGroup.add(oneSourceToggle);

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        options.setAlignmentX(LEFT_ALIGNMENT);
        options.setOpaque(false);
        options.add(allSourcesToggle);
        options.add(Box.createHorizontalStrut(8));
        options.add(oneSourceToggle);
        card.add(options);
        card.add(Box.createVerticalStrut(12));

        JComboBox<Source> loadArticlesComboBox = new JComboBox<Source>();

        loadArticlesComboBox.setPreferredSize(new Dimension(180, loadArticlesComboBox.getPreferredSize().height));
        Font f = loadArticlesComboBox.getFont();
        loadArticlesComboBox.setFont(f.deriveFont(f.getSize() + 5f));

        //Add values here!!!

        JSpinner maxArticles = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));

        JPanel comboBoxes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboBoxes.setAlignmentX(LEFT_ALIGNMENT);
        comboBoxes.setOpaque(false);
        comboBoxes.add(labeledColumn("Source", loadArticlesComboBox));
        comboBoxes.add(Box.createHorizontalStrut(12));
        comboBoxes.add(labeledColumn("Max articles", maxArticles));
        card.add(Box.createHorizontalStrut(5));
        card.add(comboBoxes);

        JButton loadBtn  = new JButton("Load articles");
        loadBtn.putClientProperty("FlatLaf.style", "background: #2563eb; foreground: #fff; margin: 6,14,6,14");
        loadBtn.setAlignmentX(LEFT_ALIGNMENT);
        card.add(loadBtn);

        oneSourceToggle.addActionListener(e -> {
            maxArticles.setEnabled(true);
            loadArticlesComboBox.setEnabled(true);
        });

        allSourcesToggle.addActionListener(e -> {
            maxArticles.setEnabled(false);
            loadArticlesComboBox.setEnabled(false);
        });
        allSourcesToggle.doClick();

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
