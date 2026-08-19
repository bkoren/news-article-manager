package hr.algebra.app.forms;

import hr.algebra.dao.models.Author;
import hr.algebra.dao.models.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryPanel extends JPanel {
    private JTextField searchField;
    private JButton searchBtn;
    private JButton newBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private JTable categoryTable;
    private DefaultTableModel tableModel;

    private List<Category> categoryList = new ArrayList<Category>();

    public CategoryPanel() {
        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildSearchBar(), BorderLayout.WEST);
        topSection.add(buildBtnSection(), BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        if(!categoryList.isEmpty()) {
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
        String[] columnNames = { "Name", "Articles" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setFillsViewportHeight(true);

        refreshTable();

        return new JScrollPane(categoryTable);
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

        for (Category category : categoryList) {
            tableModel.addRow(new Object[] {
                    category.getName(),
            });
        }
    }
}
