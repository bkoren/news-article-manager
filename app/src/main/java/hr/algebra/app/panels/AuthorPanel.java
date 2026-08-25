package hr.algebra.app.panels;

import hr.algebra.dao.models.Author;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AuthorPanel extends BasePanel<Author> {
    private final AuthorRepositoryImpl authorRepository;

    public AuthorPanel(AuthorRepositoryImpl authorRepository) {
        this.authorRepository = authorRepository;

        authorRepository.addListener(this::buildMainList);

        buildMainList();
        buildEvents();
    }

    @Override
    protected JScrollPane buildTable(String[] columnNames) {
        super.buildTable(columnNames);
        super.modifyColumns();

        return new JScrollPane(entityData);
    }

    private void buildMainList() {
        try {
            mainEntityList = authorRepository.read();
            refreshUi();
        }
        catch (SQLException exception) {
            displayMainMessage("A database error occurred. Please try again.");
        }
    }

    private void buildEvents() {
        searchBtn.addActionListener(event -> {
            searchAuthorLogic();
        });

        deleteBtn.addActionListener(event -> {
            deleteAuthorLogic();
        });

        newBtn.addActionListener((event -> {
            newAuthorLogic();
        }));

        editBtn.addActionListener((event -> {
            editAuthorLogic();
        }));
    }


    private void searchAuthorLogic() {
        searchQuery = searchField.getText().trim().toLowerCase();

        refreshUi();
        searchQuery = "";
        searchField.setText("");
    }

    private void newAuthorLogic() {
        JDialog newAuthorDialog = buildDialog("Insert new author");

        JPanel inputSection   = new JPanel();
        JTextField inputField = new JTextField(20);
        JButton confirmBtn    = new JButton("Insert");

        inputSection.add(inputField);
        inputSection.add(confirmBtn);
        newAuthorDialog.add(inputSection);

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
            finally {
                newAuthorDialog.dispose();
            }
        }));

        newAuthorDialog.setVisible(true);
    }

    private void editAuthorLogic() {
        try {
            tableModel.getValueAt(entityData.getSelectedRow(), 0);
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            DialogUtils.showError(this, "No author selected. Please select an author.");
            return;
        }
        catch (NullPointerException exception) {
            return;
        }

        JDialog editAuthorDialog = buildDialog("Edit author");

        JPanel inputSection   = new JPanel();
        JTextField inputField = new JTextField(20);
        JButton confirmBtn    = new JButton("Submit");

        inputSection.add(inputField);
        inputSection.add(confirmBtn);
        editAuthorDialog.add(inputSection);

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
                        (Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0), inputData));

            }
            catch (SQLException ex) {
                DialogUtils.showError(this, "A database error occurred. Please try again.");
            }
            finally {
                editAuthorDialog.dispose();
            }

        }));

        editAuthorDialog.setVisible(true);
    }

    private void deleteAuthorLogic() {
        try {
            if(DialogUtils.confirm(this, "Are you sure you want to delete " +
                    tableModel.getValueAt(entityData.getSelectedRow(), 1) + "?")) {

                authorRepository.delete((Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0));
            }
        }
        catch (SQLException exception) {
            DialogUtils.showError(this, "A database error occurred. Please try again.");
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            DialogUtils.showError(this, "No author selected. Please select an author.");
        }
        catch (NullPointerException exception) {
            return;
        }
    }

    @Override
    protected void refreshUi() {
        super.refreshUi();

        if(filteredEntityList.isEmpty()) {
            if(displayTable != null) {
                displayTable.setVisible(false);
            }
            displayMainMessage("No available content.");
            return;
        }

        if(this.isAncestorOf(mainMessageLabel)) {
            remove(mainMessageLabel);
        }

        if(tableModel == null) {
            displayTable = buildTable(new String[] { "Id", "Name", "Articles" });
            add(displayTable, BorderLayout.CENTER);
        }

        tableModel.setRowCount(0);

        for (Author author : filteredEntityList) {
            tableModel.addRow(new Object[] {
                    author.getAuthorId(),
                    author.getName(),
                    author.getArticlesCount()
            });
        }

        displayTable.setVisible(true);
    }
}
