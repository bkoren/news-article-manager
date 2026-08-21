package hr.algebra.app.forms;

import hr.algebra.dao.models.Author;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorPanel extends JPanel {
    private JTextField searchField;
    private JButton searchBtn;
    private JButton newBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private JTable authorTable;
    private DefaultTableModel tableModel;

    private final AuthorRepositoryImpl authorRepository;

    private List<Author> authorList;

    public AuthorPanel(AuthorRepositoryImpl authorRepository) {
        this.authorRepository = authorRepository;

        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildSearchBar(), BorderLayout.WEST);
        topSection.add(buildBtnSection(), BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        try {
            authorList = authorRepository.read();
        }
        catch (SQLException exception) {
            add(noContentMessage("A database error occurred. Please try again."), BorderLayout.CENTER);
        }

        if(!authorList.isEmpty()) {
            add(buildTable(), BorderLayout.CENTER);
        }
        else {
            add(noContentMessage("No available content."), BorderLayout.CENTER);
        }
    }

    private void fillTheList() {
        try {
            authorList = authorRepository.read();
        }
        catch (SQLException exception) {
            remove(authorTable);
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
        String[] columnNames = { " Name", "Articles" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        authorTable = new JTable(tableModel);
        authorTable.setFillsViewportHeight(true);
        authorTable.setAutoCreateRowSorter(false);
        authorTable.setFont(authorTable.getFont().deriveFont(14f));
        authorTable.setRowHeight(32);
        authorTable.setRowMargin(4);
        authorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn column = authorTable.getColumnModel().getColumn(1);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column.setResizable(false);

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

        refreshTable();

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

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Author author : authorList) {
            tableModel.addRow(new Object[] {
                    author.getName(),
            });
        }
    }
}
