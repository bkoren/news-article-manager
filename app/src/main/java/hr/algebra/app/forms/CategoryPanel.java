package hr.algebra.app.forms;

import hr.algebra.dao.models.Category;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.util.List;

public class CategoryPanel extends JPanel {
    private JTextField searchField;
    private JButton searchBtn;
    private JButton newBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private JScrollPane categoryDisplayTable;
    private DefaultTableModel tableModel;

    private JLabel messageLabel;
    private String searchQuery = "";

    private List<Category> mainList;

    private final CategoryRepositoryImpl categoryRepository;

    public CategoryPanel(CategoryRepositoryImpl categoryRepository) {
        this.categoryRepository = categoryRepository;

        categoryRepository.addListener(this::fillTheList);

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
            mainList = categoryRepository.read();
            refreshUi();
        }
        catch (SQLException exception) {
            if(categoryDisplayTable != null) {
                categoryDisplayTable.setVisible(false);
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
        List<Category> filteredList = searchQuery.isEmpty()
                ? List.copyOf(mainList)
                : mainList.stream()
                  .filter(c -> c.getName().toLowerCase().contains(searchQuery))
                  .toList();

        if(filteredList.isEmpty()) {
            if(categoryDisplayTable != null) {
                categoryDisplayTable.setVisible(false);
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

            if(categoryDisplayTable != null) {
                categoryDisplayTable.setVisible(true);
            }
        }

        if(tableModel == null) {
            categoryDisplayTable = buildTable();
            add(categoryDisplayTable, BorderLayout.CENTER);
        }

        tableModel.setRowCount(0);

        for (Category category : filteredList) {
            tableModel.addRow(new Object[] {
                    category.getName(),
                    category.getArticlesCount()
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

        JTable categoryData = new JTable(tableModel);
        categoryData.setFillsViewportHeight(true);
        categoryData.setAutoCreateRowSorter(false);
        categoryData.setFont(categoryData.getFont().deriveFont(14f));
        categoryData.setRowHeight(32);
        categoryData.setRowMargin(4);
        categoryData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column = categoryData.getColumnModel().getColumn(1);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column.setResizable(false);
        column.setCellRenderer(center);

        JTableHeader header = categoryData.getTableHeader();
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

        return new JScrollPane(categoryData);
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
