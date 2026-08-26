package hr.algebra.app.panels;

import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class ArticlePanel extends BasePanel<Article> {

    private static final String IMAGE_FOLDER = "assets";

    private static final int DIALOG_WIDTH  = 640;
    private static final int DIALOG_HEIGHT = 550;

    private static final int IMAGE_WIDTH  = DIALOG_WIDTH;
    private static final int IMAGE_HEIGHT = 240;

    private static final Color CATEGORY_COLOR = new Color(38, 108, 191);

    private static final int CATEGORY_CORNER = 16;

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

    private JLabel buildCategoryChip(Category category) {
        JLabel chip = new JLabel(category.getName()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                graphics2D.setColor(getBackground());
                graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(),
                        CATEGORY_CORNER, CATEGORY_CORNER);

                graphics2D.dispose();

                super.paintComponent(graphics);
            }
        };
        chip.setOpaque(false);

        chip.setBackground(CATEGORY_COLOR);
        chip.setForeground(Color.WHITE);

        chip.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        return chip;
    }

    private void buildEvents() {
        searchBtn.addActionListener(event -> {
            searchArticleLogic();
        });

        deleteBtn.addActionListener(event -> {
            try {
                int articleId  = (Integer) tableModel.getValueAt(entityData.getSelectedRow(), 0);

                deleteArticleLogic(articleId);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                DialogUtils.showError(this, "No article selected. Please select an article");
            }
            catch (NullPointerException exception) {
                return;
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
                return;
            }
        });
    }

    private JLabel buildImageBox(Article article) {
        JLabel imageBox = new JLabel();

        imageBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 10, true));
        imageBox.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        imageBox.setHorizontalAlignment(SwingConstants.CENTER);
        imageBox.setVerticalAlignment(SwingConstants.CENTER);

        if (article == null || article.getImagePath() == null) {
            imageBox.setText("No image available.");
            return imageBox;
        }

        Path imageFile = Paths.get(IMAGE_FOLDER)
                .resolve(Paths.get(article.getImagePath()).getFileName());

        if (!Files.exists(imageFile)) {
            imageBox.setText("Image not found.");
            return imageBox;
        }

        ImageIcon originalImage = new ImageIcon(imageFile.toString());

        Image scaledImage = originalImage.getImage()
                .getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);

        imageBox.setIcon(new ImageIcon(scaledImage));

        return imageBox;
    }


    private void viewArticleLogic(int articleId) {
        Article selectedArticle = mainEntityList.stream()
                .filter(article -> article.getArticleId() == articleId)
                .findFirst()
                .orElse(null);
        assert selectedArticle != null;

        JDialog viewArticleDialog = buildDialog("View article");

        viewArticleDialog.setLayout(new BorderLayout());
        viewArticleDialog.add(buildImageBox(selectedArticle), BorderLayout.NORTH);

        JLabel title = new JLabel(selectedArticle.getTitle());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JLabel mainInfo = new JLabel(
                selectedArticle.getSource().getName() + "  |  " +
                selectedArticle.getPublishedAt().toString().replaceFirst("T", " ") + "  |  " +
                (!selectedArticle.getAuthors().isEmpty()
                        ? (String.join(" | ", selectedArticle.getAuthors().toString().replace("[", "").replace("]", "")))
                        : "No author available."
                )
        );

        mainInfo.setFont(title.getFont().deriveFont(Font.ITALIC, 12f));

        JPanel topSection = new JPanel();

        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        topSection.add(title);
        topSection.add(Box.createVerticalStrut(7));
        topSection.add(mainInfo);
        topSection.add(Box.createVerticalStrut(7));
        topSection.setBorder(BorderFactory.createEmptyBorder(7,5,5,5));

        JPanel categories = new JPanel(new FlowLayout(FlowLayout.LEFT));

        for(Category category : selectedArticle.getCategories()) {
            categories.add(buildCategoryChip(category));
        }
        JPanel contentSection = new JPanel(new BorderLayout());

        contentSection.add(topSection, BorderLayout.NORTH);
        contentSection.add(categories, BorderLayout.CENTER);

        viewArticleDialog.add(contentSection, BorderLayout.CENTER);

        viewArticleDialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        viewArticleDialog.setResizable(false);
        viewArticleDialog.setLocationRelativeTo(this);
        viewArticleDialog.setVisible(true);
    }

    protected void searchArticleLogic() {
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
