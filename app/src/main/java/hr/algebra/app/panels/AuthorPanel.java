package hr.algebra.app.panels;

import hr.algebra.dao.models.Author;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

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
    private JTable authorData;

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

        deleteBtn.addActionListener(event -> {
            try {
                if(DialogUtils.confirm(this, "Are you sure you want to delete " +
                        tableModel.getValueAt(authorData.getSelectedRow(), 1) + "?")) {

                    authorRepository.delete((Integer) tableModel.getValueAt(authorData.getSelectedRow(), 0));
                }
            }
            catch (SQLException exception) {
                DialogUtils.showError(this, "A database error occurred. Please try again.");
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No author selected. Please select an author.");
            }
        });

        newBtn.addActionListener((event -> {
            JDialog newArticleDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Insert new author");
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
                    authorRepository.create(new Author(0, inputData));
                }
                catch (SQLException ex) {
                    DialogUtils.showError(this, "A database error occurred. Please try again.");
                }

                newArticleDialog.dispose();
            }));

            newArticleDialog.setMinimumSize(new Dimension(400, 100));
            newArticleDialog.setLocationRelativeTo(this);
            newArticleDialog.setVisible(true);
        }));

        editBtn.addActionListener((event -> {
            try {
                int id = (Integer) tableModel.getValueAt(authorData.getSelectedRow(), 0);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No author selected. Please select an author.");
                return;
            }

            JDialog newArticleDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit author");
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
                    authorRepository.update(new Author(
                            (Integer) tableModel.getValueAt(authorData.getSelectedRow(), 0), inputData));

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
                    author.getAuthorId(),
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
        String[] columnNames = { "Id", "Name", "Articles" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        authorData = new JTable(tableModel);
        authorData.setFillsViewportHeight(true);
        authorData.setAutoCreateRowSorter(false);
        authorData.setFont(authorData.getFont().deriveFont(14f));
        authorData.setRowHeight(32);
        authorData.setRowMargin(4);
        authorData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        authorData.removeColumn(authorData.getColumnModel().getColumn(0));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column = authorData.getColumnModel().getColumn(1);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column.setResizable(false);
        column.setCellRenderer(center);

        JTableHeader header = authorData.getTableHeader();
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

        return new JScrollPane(authorData);
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
