package hr.algebra.app.forms;

import hr.algebra.dao.models.Article;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArticlePanel extends JPanel {
    private JTextField searchField;
    private JButton searchBtn;
    private JButton viewBtn;
    private JButton newBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private List<Article> articleList = new ArrayList<>();

    private JTable articleTable;
    private DefaultTableModel tableModel;

    public ArticlePanel() {
        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildSearchBar(), BorderLayout.WEST);
        topSection.add(buildBtnSection(), BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        if(!articleList.isEmpty()) {
            add(buildTable(), BorderLayout.CENTER);
        }
        else {
            add(noContentMessage("No available content."), BorderLayout.CENTER);
        }
    }

    private JLabel noContentMessage(String message) {
        JLabel label = new JLabel(message);

        label.setFont(new Font("SansSerif", Font.BOLD, 48));
        label.setForeground(Color.RED);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        return label;
    }

    private JScrollPane buildTable() {
        String[] columnNames = { "Title", "Source", "Published At" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        articleTable = new JTable(tableModel);
        articleTable.setFillsViewportHeight(true);

        refreshTable();

        return new JScrollPane(articleTable);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Article article : articleList) {
            tableModel.addRow(new Object[] {
                    article.getArticleId(),
                    article.getTitle(),
                    article.getSource(),
                    article.getPublishedAt()
            });
        }
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
