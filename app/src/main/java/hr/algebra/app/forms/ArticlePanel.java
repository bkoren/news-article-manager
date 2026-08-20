package hr.algebra.app.forms;

import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticlePanel extends JPanel {
    private JTextField searchField;
    private JButton searchBtn;
    private JButton viewBtn;
    private JButton newBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private final ArticleRepositoryImpl articleRepository;

    private List<Article> articleList;

    private JTable articleTable;
    private DefaultTableModel tableModel;

    public ArticlePanel() {
        articleRepository = new ArticleRepositoryImpl(
                new AuthorRepositoryImpl(),
                new CategoryRepositoryImpl()
        );

        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildSearchBar(), BorderLayout.WEST);
        topSection.add(buildBtnSection(), BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        try {
            articleList = articleRepository.read(0);
        }
        catch (SQLException exception) {
            add(noContentMessage("A database error occurred. Please try again."), BorderLayout.CENTER);
        }

        if(!articleList.isEmpty()) {
            add(buildTable(), BorderLayout.CENTER);
        }
        else {
            add(noContentMessage("No available content."), BorderLayout.CENTER);
        }
    }

    private void fillTheList() {
        try {
            articleList = articleRepository.read(0);
        }
        catch (SQLException exception) {
            remove(articleTable);
            add(noContentMessage("A database error occurred. Please try again."), BorderLayout.CENTER);
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
        String[] columnNames = { " Title", " Source", " Published At" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        articleTable = new JTable(tableModel);
        articleTable.setFillsViewportHeight(true);
        articleTable.setAutoCreateRowSorter(false);
        articleTable.setFont(articleTable.getFont().deriveFont(14f));
        articleTable.setRowHeight(32);
        articleTable.setRowMargin(4);
        articleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        articleTable.getTableHeader().setResizingAllowed(true);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column2 = articleTable.getColumnModel().getColumn(2);
        column2.setCellRenderer(right);
        column2.setPreferredWidth(125);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column1 = articleTable.getColumnModel().getColumn(1);
        column1.setCellRenderer(center);
        column1.setPreferredWidth(125);

        articleTable.getColumnModel().getColumn(0).setPreferredWidth(375);

        JTableHeader header = articleTable.getTableHeader();
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

        refreshTable();

        return new JScrollPane(articleTable);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Article article : articleList) {
            tableModel.addRow(new Object[] {
                    article.getTitle(),
                    article.getSource().getName(),
                    article.getPublishedAt().toLocalDate()
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
