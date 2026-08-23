package hr.algebra.app.forms;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ArticlePanel extends JPanel {
    private JTextField searchField;
    private JButton searchBtn;
    private JButton viewBtn;
    private JButton newBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private final ArticleRepositoryImpl articleRepository;

    private List<Article> mainList;

    private DefaultTableModel tableModel;
    private JTable articleData;

    private JScrollPane articleDisplayTable;
    private JLabel messageLabel;

    private String searchQuery = "";

    public ArticlePanel(ArticleRepositoryImpl articleRepository) {
        this.articleRepository = articleRepository;

        articleRepository.addListener(this::fillTheList);

        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildSearchBar(), BorderLayout.WEST);
        topSection.add(buildBtnSection(), BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        fillTheList();

        addEvents();
    }

    private void addEvents() {
        searchBtn.addActionListener(event -> {
            searchQuery = searchField.getText().trim().toLowerCase();

            refreshUi();
            searchQuery = "";
            searchField.setText("");
        });

        deleteBtn.addActionListener(event -> {
            try {
                if(DialogUtils.confirm(this, "Are you sure you want to delete " +
                        tableModel.getValueAt(articleData.getSelectedRow(), 1) + "?")) {

                    articleRepository.delete((Integer) tableModel.getValueAt(articleData.getSelectedRow(), 0));
                }
            }
            catch (SQLException exception) {
                DialogUtils.showError(this, "A database error occurred. Please try again.");
            }
            catch (AssetException exception) {
                DialogUtils.showError(this, "Failed to delete the image of the article!");
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No article selected. Please select an article.");
            }
        });
    }

    private void fillTheList() {
        try {
            mainList = articleRepository.read(0);
            refreshUi();
        }
        catch (SQLException exception) {
            if(articleDisplayTable != null) {
                articleDisplayTable.setVisible(false);
            }

            if(messageLabel != null) {
                remove(messageLabel);
            }
            messageLabel = setMainMessageText("A database error occurred. Please try again.");
            add(messageLabel, BorderLayout.CENTER);

            this.revalidate();
            this.repaint();
        }
    }

    private void refreshUi() {
        List<Article> filteredList = searchQuery.isEmpty()
                ? List.copyOf(mainList)
                : mainList.stream()
                    .filter(a -> a.getTitle().toLowerCase().contains(searchQuery))
                    .toList();

        if(tableModel == null) {
            articleDisplayTable = buildTable();
            add(articleDisplayTable, BorderLayout.CENTER);
        }

        if(filteredList.isEmpty()) {
            if(articleDisplayTable != null) {
                articleDisplayTable.setVisible(false);
            }

            if(messageLabel != null) {
                remove(messageLabel);
            }

            messageLabel = setMainMessageText("No available content.");
            add(messageLabel, BorderLayout.CENTER);

            this.revalidate();
            this.repaint();

            return;
        }
        else {
            if(messageLabel != null) {
                remove(messageLabel);
            }

            if(articleDisplayTable != null) {
                articleDisplayTable.setVisible(true);
            }
        }

        tableModel.setRowCount(0);

        for (Article article : filteredList) {
            tableModel.addRow(new Object[] {
                    article.getArticleId(),
                    article.getTitle(),
                    article.getSource().getName(),
                    article.getPublishedAt().toLocalDate()
            });
        }
    }

    private JLabel setMainMessageText(String message) {
        messageLabel = new JLabel(message);

        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);

        return messageLabel;
    }

    private JScrollPane buildTable() {
        String[] columnNames = { "Id", "Title", "Source", "Published At" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        articleData = new JTable(tableModel);
        articleData.setFillsViewportHeight(true);
        articleData.setAutoCreateRowSorter(false);
        articleData.setFont(articleData.getFont().deriveFont(14f));
        articleData.setRowHeight(32);
        articleData.setRowMargin(4);
        articleData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        articleData.getTableHeader().setResizingAllowed(true);
        articleData.removeColumn(articleData.getColumnModel().getColumn(0));

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column2 = articleData.getColumnModel().getColumn(2);
        column2.setCellRenderer(right);
        column2.setPreferredWidth(125);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column1 = articleData.getColumnModel().getColumn(1);
        column1.setCellRenderer(center);
        column1.setPreferredWidth(125);

        articleData.getColumnModel().getColumn(0).setPreferredWidth(375);

        JTableHeader header = articleData.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setFocusable(false);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));

        TableCellRenderer original = header.getDefaultRenderer();

        header.setDefaultRenderer((t, value, sel, focus, row, col) -> {
            Component c = original.getTableCellRendererComponent(t, value, sel, focus, row, col);
            if (c instanceof JLabel label) {
                int alignment = switch (col) {
                    case 1, 2 -> SwingConstants.CENTER;
                    default -> SwingConstants.LEFT;
                };
                label.setHorizontalAlignment(alignment);
            }
            return c;
        });

        return new JScrollPane(articleData);
    }

    private JPanel buildBtnSection() {
        JPanel btnSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        viewBtn   = new JButton("View");
        newBtn    = new JButton("New");
        editBtn   = new JButton("Edit");
        deleteBtn = new JButton("Delete");

        btnSection.add(Box.createVerticalStrut(40));
        btnSection.add(viewBtn);
        btnSection.add(newBtn);
        btnSection.add(editBtn);
        btnSection.add(deleteBtn);
        btnSection.add(Box.createHorizontalStrut(10));

        return btnSection;
    }

    private JPanel buildSearchBar() {
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        searchField = new JTextField(20);
        searchBtn = new JButton("Search");

        searchBar.add(Box.createVerticalStrut(40));
        searchBar.add(searchField);
        searchBar.add(searchBtn);

        return searchBar;
    }
}
