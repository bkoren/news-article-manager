package hr.algebra.app.panels;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ArticlePanel extends BasePanel<Article> {
    private final ArticleRepositoryImpl articleRepository;

    public ArticlePanel(ArticleRepositoryImpl articleRepository) {
        this.articleRepository = articleRepository;

        articleRepository.addListener(this::buildMainList);

        buildAdditionalButtons();

        buildMainList();
        buildEvents();
    }

    @Override
    protected JScrollPane buildTable(String[] columnNames) {
        super.buildTable(columnNames);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column1 = entityData.getColumnModel().getColumn(1);
        column1.setCellRenderer(center);
        column1.setPreferredWidth(125);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumn column2 = entityData.getColumnModel().getColumn(2);
        column2.setCellRenderer(right);
        column2.setPreferredWidth(125);

        entityData.getTableHeader().setResizingAllowed(true);
        entityData.getColumnModel().getColumn(0).setPreferredWidth(375);

        TableCellRenderer original = tableHeader.getDefaultRenderer();

        tableHeader.setDefaultRenderer((t, value, sel, focus, row, col) -> {
            Component column = original.getTableCellRendererComponent(t, value, sel, focus, row, col);
            if (column instanceof JLabel label) {
                int alignment = switch (col) {
                    case 1, 2 -> SwingConstants.CENTER;
                    default -> SwingConstants.LEFT;
                };
                label.setHorizontalAlignment(alignment);
            }
            return column;
        });

        return new JScrollPane(entityData);
    }

    private void buildMainList() {
        try {
            mainEntityList = articleRepository.read(0);
            refreshUi();
        }
        catch (SQLException exception) {
            displayMainMessage("A database error occurred. Please try again.");
        }
    }

    private void buildAdditionalButtons() {
        viewBtn = new JButton("View");

        btnSection.add(viewBtn, 0);
    }

    private void buildEvents() {
        searchBtn.addActionListener(event -> {
            searchArticleLogic();
        });

        deleteBtn.addActionListener(event -> {
            deleteArticleLogic();
        });
    }


    protected void searchArticleLogic() {
        searchQuery = searchField.getText().trim().toLowerCase();

        refreshUi();
        searchQuery = "";
        searchField.setText("");
    }

    private void deleteArticleLogic() {
        try {
            if(DialogUtils.confirm(this, "Are you sure you want to delete " +
                    tableModel.getValueAt(entityData.getSelectedRow(), 1) + "?")) {

                articleRepository.delete((Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0));

                articleRepository.triggerAuthors();
                articleRepository.triggerCategories();
            }
        }
        catch (SQLException exception) {
            DialogUtils.showError(this, "A database error occurred. Please try again.");
        }
        catch (AssetException exception) {
            DialogUtils.showError(this, "Failed to delete the image of the article!");
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            DialogUtils.showError(this, "No article selected. Please select an article.");
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
            displayTable = buildTable(new String[] {"Id", "Title", "Source", "Published at"});
            add(displayTable, BorderLayout.CENTER);
        }

        tableModel.setRowCount(0);

        for (Article article : filteredEntityList) {
            tableModel.addRow(new Object[] {
                    article.getArticleId(),
                    article.getTitle(),
                    article.getSource().getName(),
                    article.getPublishedAt().toLocalDate()
            });
        }

        displayTable.setVisible(true);
    }
}
