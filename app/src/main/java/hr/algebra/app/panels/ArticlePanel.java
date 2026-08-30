package hr.algebra.app.panels;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;

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
        searchBtn.addActionListener(event -> searchArticleLogic());

        deleteBtn.addActionListener(event -> {
            try {
                int articleId  = (Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0);

                deleteArticleLogic(articleId);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No article selected. Please select an article");
            }
            catch (NullPointerException exception) {
                // ignore;
            }
        });

        viewBtn.addActionListener(event -> {
            try {
                int articleId  = (Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0);

                viewArticleLogic(articleId);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No article selected. Please select an article");
            }
            catch (NullPointerException exception) {
                // ignore;
            }
        });

        newBtn.addActionListener(event -> newArticleLogic());

        editBtn.addActionListener(event -> {
            try {
                int articleId  = (Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0);

                editArticleLogic(articleId);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No article selected. Please select an article");
            }
            catch (NullPointerException exception) {
                // ignore;
            }
        });
    }


    private void editArticleLogic(int articleId) {
        Article selectedArticle = getSelectedArticle(articleId);
        assert selectedArticle != null;

        BaseDialog editArticleDialog = new BaseDialog(articleRepository, selectedArticle, "Edit article");

        JPanel leftColumn = editArticleDialog.buildLeftColumn();
        JPanel rightColumn = editArticleDialog.buildRightColumn();

        JPanel columns = new JPanel(new GridLayout(1, 2, 5, 0));
        columns.add(leftColumn);
        columns.add(rightColumn);

        JPanel bottomContent = editArticleDialog.buildBottomContent();

        JPanel content = new JPanel(new BorderLayout(0, 5));
        content.add(columns, BorderLayout.CENTER);
        content.add(bottomContent, BorderLayout.SOUTH);

        editArticleDialog.add(content, BorderLayout.CENTER);
        editArticleDialog.setCurrentValues();
        editArticleDialog.setLocationRelativeTo(this);
        editArticleDialog.setVisible(true);
    }

    private void newArticleLogic() {
        BaseDialog newArticleDialog = new BaseDialog(articleRepository, null, "New article");

        JPanel leftColumn = newArticleDialog.buildLeftColumn();
        JPanel rightColumn = newArticleDialog.buildRightColumn();

        JPanel columns = new JPanel(new GridLayout(1, 2, 5, 0));
        columns.add(leftColumn);
        columns.add(rightColumn);

        JPanel bottomContent = newArticleDialog.buildBottomContent();

        JPanel content = new JPanel(new BorderLayout(0, 5));
        content.add(columns, BorderLayout.CENTER);
        content.add(bottomContent, BorderLayout.SOUTH);

        newArticleDialog.add(content, BorderLayout.CENTER);
        newArticleDialog.setLocationRelativeTo(this);
        newArticleDialog.setVisible(true);
    }

    private void viewArticleLogic(int articleId) {
        Article selectedArticle = getSelectedArticle(articleId);
        assert selectedArticle != null;

        BaseDialog viewArticleDialog = new BaseDialog(null, selectedArticle, "View article");

        JPanel topSection = viewArticleDialog.buildTopSection();
        JPanel bodySection = viewArticleDialog.buildBodySection();
        JPanel bottomSection = viewArticleDialog.buildBottomSection();

        JPanel contentSection = new JPanel(new BorderLayout());
        contentSection.add(topSection, BorderLayout.NORTH);
        contentSection.add(bodySection, BorderLayout.CENTER);
        contentSection.add(bottomSection, BorderLayout.SOUTH);

        viewArticleDialog.add(contentSection, BorderLayout.CENTER);
        viewArticleDialog.setLocationRelativeTo(this);
        viewArticleDialog.setVisible(true);
    }

    private void searchArticleLogic() {
        searchQuery = searchField.getText().trim().toLowerCase();

        refreshUi();
        searchQuery = "";
        searchField.setText("");
    }

    private void deleteArticleLogic(int articleId) {
        try {
            if(DialogUtils.confirm(this, "Are you sure you want to delete " + articleId + "?")) {
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
    }


    private Article getSelectedArticle(int articleId) {
        return mainEntityList.stream()
                .filter(article -> article.getArticleId() == articleId)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void refreshUi() {
        super.refreshUi();

        if(filteredEntityList.isEmpty()) {
            if(displayTable != null) {
                displayTable.setVisible(false);
            }
            displayMainMessage("No available articles.");
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
