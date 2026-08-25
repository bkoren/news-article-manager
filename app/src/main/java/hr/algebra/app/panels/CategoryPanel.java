package hr.algebra.app.panels;

import hr.algebra.dao.models.Category;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CategoryPanel extends BasePanel<Category> {
    private final CategoryRepositoryImpl categoryRepository;

    public CategoryPanel(CategoryRepositoryImpl categoryRepository) {
        this.categoryRepository = categoryRepository;

        categoryRepository.addListener(this::buildMainList);

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
            mainEntityList = categoryRepository.read();
            refreshUi();
        }
        catch (SQLException exception) {
            displayMainMessage("A database error occurred. Please try again.");
        }
    }

    private void buildEvents() {
        searchBtn.addActionListener(event -> {
            searchCategoryLogic();
        });

        deleteBtn.addActionListener(event -> {
            deleteCategoryLogic();
        });

        newBtn.addActionListener((event -> {
            newCategoryLogic();
        }));

        editBtn.addActionListener((event -> {
            editCategoryLogic();
        }));
    }


    private void searchCategoryLogic() {
        searchQuery = searchField.getText().trim().toLowerCase();

        refreshUi();
        searchQuery = "";
        searchField.setText("");
    }

    private void newCategoryLogic() {
        JDialog newCategoryDialog = buildDialog("Insert new category");

        JPanel inputSection   = new JPanel();
        JTextField inputField = new JTextField(20);
        JButton confirmBtn    = new JButton("Insert");

        inputSection.add(inputField);
        inputSection.add(confirmBtn);
        newCategoryDialog.add(inputSection);

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
            finally {
                newCategoryDialog.dispose();
            }
        }));

        newCategoryDialog.setVisible(true);
    }

    private void editCategoryLogic() {
        try {
            tableModel.getValueAt(entityData.getSelectedRow(), 0);
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            DialogUtils.showError(this, "No category selected. Please select category.");
            return;
        }
        catch (NullPointerException exception) {
            return;
        }

        JDialog editCategoryDialog = buildDialog("Edit category");

        JPanel inputSection   = new JPanel();
        JTextField inputField = new JTextField(20);
        JButton confirmBtn    = new JButton("Submit");

        inputSection.add(inputField);
        inputSection.add(confirmBtn);
        editCategoryDialog.add(inputSection);

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
                        (Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0), inputData));
            }
            catch (SQLException ex) {
                DialogUtils.showError(this, "A database error occurred. Please try again.");
            }
            finally {
                editCategoryDialog.dispose();
            }
        }));

        editCategoryDialog.setVisible(true);
    }

    private void deleteCategoryLogic() {
        try {
            if(DialogUtils.confirm(this, "Are you sure you want to delete " +
                    tableModel.getValueAt(entityData.getSelectedRow(), 1) + "?")) {

                categoryRepository.delete((Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0));
            }
        }
        catch (SQLException exception) {
            DialogUtils.showError(this, "A database error occurred. Please try again.");
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            DialogUtils.showError(this, "No category selected. Please select category.");
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

        for (Category category : filteredEntityList) {
            tableModel.addRow(new Object[] {
                    category.getCategoryId(),
                    category.getName(),
                    category.getArticlesCount()
            });
        }

        displayTable.setVisible(true);
    }
}
