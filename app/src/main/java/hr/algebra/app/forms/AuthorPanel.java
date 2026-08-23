package hr.algebra.app.forms;

import hr.algebra.dao.models.Author;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.util.List;

public class AuthorPanel extends JPanel {
    private JTextField searchField;
    private JButton searchBtn;
    private JButton newBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private DefaultTableModel tableModel;

    private JScrollPane authorDisplayTable;
    private JLabel messageLabel;
    private String searchQuery = "";

    private final AuthorRepositoryImpl authorRepository;

    private List<Author> mainList;

    public AuthorPanel(AuthorRepositoryImpl authorRepository) {
        this.authorRepository = authorRepository;

        authorRepository.addListener(this::fillTheList);

        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildSearchBar(), BorderLayout.WEST);
        topSection.add(buildBtnSection(), BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        addEvents();

        fillTheList();
    }

    private void addEvents() {
        searchBtn.addActionListener(event -> {
            searchQuery = searchField.getText().trim().toLowerCase();

            refreshUi();
            searchQuery = "";
            searchField.setText("");
        });
    }

    private void fillTheList() {
        try {
            mainList = authorRepository.read();
            refreshUi();
        }
        catch (SQLException exception) {
            if(authorDisplayTable != null) {
                authorDisplayTable.setVisible(false);
            }
            if(messageLabel != null) {
                remove(messageLabel);
            }
            messageLabel = setMainMessage("A database error occurred. Please try again.");
            add(messageLabel, BorderLayout.CENTER);

            this.revalidate();
            this.repaint();
        }
    }

    private void refreshUi() {
        List<Author> filteredList = searchQuery.isEmpty()
                ? List.copyOf(mainList)
                : mainList.stream()
                    .filter(a -> a.getName().toLowerCase().contains(searchQuery))
                    .toList();

        if(filteredList.isEmpty()) {
            if(authorDisplayTable != null) {
                authorDisplayTable.setVisible(false);
            }

            if(messageLabel != null) {
                remove(messageLabel);
            }
            messageLabel = setMainMessage("No available content.");
            add(messageLabel, BorderLayout.CENTER);

            this.revalidate();
            this.repaint();

            return;
        }
        else {
            if(messageLabel != null) {
                remove(messageLabel);
            }

            if(authorDisplayTable != null) {
                authorDisplayTable.setVisible(true);
            }
        }

        if(tableModel == null) {
            authorDisplayTable = buildTable();
            add(authorDisplayTable, BorderLayout.CENTER);
        }

        tableModel.setRowCount(0);
        for (Author author : filteredList) {
            tableModel.addRow(new Object[] {
                    author.getName(),
                    author.getArticlesCount()
            });
        }
    }

    private JLabel setMainMessage(String message) {
        messageLabel = new JLabel(message);

        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);

        return messageLabel;
    }

    private JScrollPane buildTable() {
        String[] columnNames = { "Name", "Articles" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable authorTable = new JTable(tableModel);
        authorTable.setFillsViewportHeight(true);
        authorTable.setAutoCreateRowSorter(false);
        authorTable.setFont(authorTable.getFont().deriveFont(14f));
        authorTable.setRowHeight(32);
        authorTable.setRowMargin(4);
        authorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column = authorTable.getColumnModel().getColumn(1);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column.setResizable(false);
        column.setCellRenderer(center);

        JTableHeader header = authorTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setFocusable(false);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));

        TableCellRenderer original = header.getDefaultRenderer();

        header.setDefaultRenderer((t, value, sel, focus, row, col) -> {
            Component c = original.getTableCellRendererComponent(t, value, sel, focus, row, col);
            if (c instanceof JLabel label) {
                int alignment = switch (col) {
                    case 0  -> SwingConstants.LEFT;
                    case 1  -> SwingConstants.CENTER;
                    default -> SwingConstants.LEFT;
                };
                label.setHorizontalAlignment(alignment);
            }
            return c;
        });

        for (MouseListener l : header.getMouseListeners()) {
            header.removeMouseListener(l);
        }
        for (MouseMotionListener l : header.getMouseMotionListeners()) {
            header.removeMouseMotionListener(l);
        }

        refreshUi();

        return new JScrollPane(authorTable);
    }

    private JPanel buildBtnSection() {
        JPanel btnSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        newBtn    = new JButton("New");
        editBtn   = new JButton("Edit");
        deleteBtn = new JButton("Delete");

        btnSection.add(Box.createVerticalStrut(40));
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
