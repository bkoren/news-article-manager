package hr.algebra.app.forms;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Author;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

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
    private JTable categoryData;

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

        deleteBtn.addActionListener(event -> {
            try {
                if(DialogUtils.confirm(this, "Are you sure you want to delete " +
                        tableModel.getValueAt(categoryData.getSelectedRow(), 1) + "?")) {

                    categoryRepository.delete((Integer) tableModel.getValueAt(categoryData.getSelectedRow(), 0));
                }
            }
            catch (SQLException exception) {
                DialogUtils.showError(this, "A database error occurred. Please try again.");
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No category selected. Please select category.");
            }
        });

        newBtn.addActionListener((event -> {
            JDialog newArticleDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Insert new category");
            newArticleDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            newArticleDialog.setLayout(new FlowLayout());

            JPanel inputSection = new JPanel();
            JTextField inputField = new JTextField(20);
            JButton confirmBtn = new JButton("Insert");

            inputSection.add(inputField);
            inputSection.add(confirmBtn);
            newArticleDialog.add(inputSection);

            confirmBtn.addActionListener((e -> {
                String inputData = inputField.getText();

                if(inputData.isEmpty()) {
                    return;
                }

                if (inputData.length() < 3) {
                    DialogUtils.showError(this, "Name needs to contain at least three characters. ");
                    return;
                }

                try {
                    categoryRepository.create(new Category(0, inputData));
                }
                catch (SQLException ex) {
                    DialogUtils.showError(this, "A database error occurred. Please try again.");
                }
            }));

            newArticleDialog.setMinimumSize(new Dimension(400, 100));
            newArticleDialog.setLocationRelativeTo(this);
            newArticleDialog.setVisible(true);
        }));

        editBtn.addActionListener((event -> {
            try {
                int id = (Integer) tableModel.getValueAt(categoryData.getSelectedRow(), 0);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No category selected. Please select category.");
                return;
            }

            JDialog newArticleDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit category");
            newArticleDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            newArticleDialog.setLayout(new FlowLayout());

            JPanel inputSection = new JPanel();
            JTextField inputField = new JTextField(20);
            JButton confirmBtn = new JButton("Submit");

            inputSection.add(inputField);
            inputSection.add(confirmBtn);
            newArticleDialog.add(inputSection);

            confirmBtn.addActionListener((e -> {
                String inputData = inputField.getText();

                if(inputData.isEmpty()) {
                    return;
                }

                if (inputData.length() < 3) {
                    DialogUtils.showError(this, "Name needs to contain at least three characters. ");
                    return;
                }

                try {
                    categoryRepository.update(new Category(
                            (Integer) tableModel.getValueAt(categoryData.getSelectedRow(), 0), inputData));

                    newArticleDialog.dispose();
                }
                catch (SQLException ex) {
                    DialogUtils.showError(this, "A database error occurred. Please try again.");
                }
            }));

            newArticleDialog.setMinimumSize(new Dimension(400, 100));
            newArticleDialog.setLocationRelativeTo(this);
            newArticleDialog.setVisible(true);
        }));
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
                    category.getCategoryId(),
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
        String[] columnNames = { "Id", "Name", "Articles" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryData = new JTable(tableModel);
        categoryData.setFillsViewportHeight(true);
        categoryData.setAutoCreateRowSorter(false);
        categoryData.setFont(categoryData.getFont().deriveFont(14f));
        categoryData.setRowHeight(32);
        categoryData.setRowMargin(4);
        categoryData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryData.removeColumn(categoryData.getColumnModel().getColumn(0));

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
