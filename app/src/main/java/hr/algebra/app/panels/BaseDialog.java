package hr.algebra.app.panels;

import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Author;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.models.Source;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BaseDialog extends JDialog {
    private ArticleRepositoryImpl articleRepository;

    private SourceRepositoryImpl   sourceRepository;
    private CategoryRepositoryImpl categoryRepository;
    private AuthorRepositoryImpl   authorRepository;

    private Article article;

    private final int DIALOG_WIDTH;
    private final int DIALOG_HEIGHT;

    private JLabel imageBox;

    private JButton           addImgBtn;
    private JTextField        titleInput;
    private JTextField        linkInput;
    private JTextArea         descriptionInput;
    private JComboBox<Author> authorInput;
    private JComboBox<Source> sourcesInput;
    private JButton           addCategoryBtn;
    private JButton           removeCategoryBtn;
    private JButton           saveBtn;

    private final DefaultListModel<Category> targetCategoriesModel = new DefaultListModel<>();
    private final DefaultListModel<Category> startCategoriesModel = new DefaultListModel<>();

    private JList<Category> targetCategories;
    private JList<Category> startCategories;

    protected BaseDialog(ArticleRepositoryImpl articleRepository, Article selectedArticle, String dialogTitle) {
        this.articleRepository = articleRepository;
        this.article = selectedArticle;

        DIALOG_HEIGHT = 620;
        DIALOG_WIDTH  = 600;

        this.setTitle(dialogTitle);
        this.buildDialog();
    }

    protected BaseDialog(String dialogTitle) {
        DIALOG_HEIGHT = 120;
        DIALOG_WIDTH  = 360;

        this.setTitle(dialogTitle);
        this.buildDialog();
    }


    protected void buildDialog() {
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setLayout(new BorderLayout());
        this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(this);
    }

    protected void buildImageBox(int width, int height) {
        imageBox = new JLabel();
        imageBox.setPreferredSize(new Dimension(width, height));

        String imgPath = (article != null && article.getImagePath() != null) ?
                article.getImagePath() :
                "assets\\default.jpg";

        Path imageFile = Paths.get("assets")
                .resolve(Paths.get(imgPath).getFileName());

        if (!Files.exists(imageFile)) {
            imageBox.setText("Image not found.");
            imageBox.setHorizontalAlignment(SwingUtilities.CENTER);

            this.add(imageBox, BorderLayout.NORTH);
            return;
        }

        ImageIcon originalImage = new ImageIcon(imageFile.toString());

        Image scaledImage = originalImage.getImage()
                .getScaledInstance(width, height, Image.SCALE_SMOOTH);

        imageBox.setIcon(new ImageIcon(scaledImage));

        this.add(imageBox, BorderLayout.NORTH);
    }


    public JPanel buildTopSection() {
        JPanel result = new JPanel();

        JLabel title = new JLabel(article.getTitle());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JLabel mainInfo = new JLabel(
                article.getSource().getName() + "  |  " +
                        article.getPublishedAt().toString().replaceFirst("T", " ") + "  |  " +
                        (!article.getAuthors().isEmpty()
                                ? (String.join(" | ",(article.getAuthors().stream().map(Author::getName).toList())))
                                : "No author available."
                        )
        );

        mainInfo.setFont(title.getFont().deriveFont(Font.ITALIC, 12f));

        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.add(title);
        result.add(Box.createVerticalStrut(7));
        result.add(mainInfo);
        result.add(Box.createVerticalStrut(7));
        result.setBorder(BorderFactory.createEmptyBorder(7,5,5,5));

        return result;
    }

    public JPanel buildBodySection() {
        JPanel result = new JPanel(new BorderLayout());

        JPanel categoriesView = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for(Category category : article.getCategories()) {
            categoriesView.add(buildCategoryChip(category));
        }

        if (categoriesView.getComponentCount() > 0) {
            categoriesView.setSize(620, 600);
            categoriesView.doLayout();

            Component lastChip = categoriesView.getComponent(categoriesView.getComponentCount() - 1);
            int usedHeight = lastChip.getY() + lastChip.getHeight() + 5;

            categoriesView.setPreferredSize(new Dimension(640 - 20, usedHeight));
        }

        JTextArea descriptionView = new JTextArea(article.getDescription());
        descriptionView.setEditable(false);
        descriptionView.setLineWrap(true);
        descriptionView.setWrapStyleWord(true);
        descriptionView.setFont(descriptionView.getFont().deriveFont(Font.PLAIN, 14f));

        JScrollPane descriptionScrollView = new JScrollPane(descriptionView);
        descriptionScrollView.setBorder(null);
        descriptionScrollView.setOpaque(false);

        result.add(categoriesView, BorderLayout.NORTH);
        result.add(descriptionScrollView, BorderLayout.CENTER);

        return result;
    }

    public JPanel buildBottomSection() {
        JPanel result = new JPanel(new BorderLayout());
        result.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String linkText = "Open original link";

        JLabel linkView = new JLabel(linkText);
        linkView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                linkView.setText("<html><u>" + linkText + "</u></html>");
                linkView.setForeground(new Color(64, 132, 224));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                linkView.setText(linkText);
                linkView.setForeground(linkView.getForeground());
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                if (!Desktop.isDesktopSupported()
                        || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    DialogUtils.showError(BaseDialog.this,
                            "Opening a browser is not supported on this system.");
                    return;
                }

                try {
                    Desktop.getDesktop().browse(new URI(article.getLink()));
                }
                catch (IOException | URISyntaxException | IllegalArgumentException exception) {
                    DialogUtils.showError(BaseDialog.this,
                            "Could not open the link in the browser.");
                }
            }
        });

        JButton exportBtn = new JButton("Export");
        exportBtn.setBackground(Color.BLUE);

        exportBtn.addActionListener(event -> exportLogic());

        result.add(linkView, BorderLayout.WEST);
        result.add(exportBtn, BorderLayout.EAST);

        return result;
    }


    public JPanel buildLeftColumn() {
        JPanel result = new JPanel();
        result.setBorder(BorderFactory.createEmptyBorder(5,10,5,5));
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));

        buildImageBox(320, 200);
        imageBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, imageBox.getPreferredSize().height));
        imageBox.setHorizontalAlignment(SwingConstants.CENTER);
        imageBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        addImgBtn = new JButton("Add image.");
        addImgBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Title");
        titleLabel.setFont(titleLabel.getFont().deriveFont(16f));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleInput = new JTextField();
        titleInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleInput.getPreferredSize().height));

        JLabel linkLabel = new JLabel("Link");
        linkLabel.setFont(titleLabel.getFont().deriveFont(16f));
        linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        linkInput = new JTextField();
        linkInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleInput.getPreferredSize().height));

        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(16f));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionInput = new JTextArea();
        descriptionInput.setRows(5);
        descriptionInput.setLineWrap(true);

        JScrollPane descriptionScrollInput = new JScrollPane(descriptionInput);
        descriptionScrollInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionScrollInput.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, descriptionScrollInput.getPreferredSize().height)
        );

        JLabel sourceLabel = new JLabel("Source");
        sourceLabel.setFont(sourceLabel.getFont().deriveFont(16f));
        sourceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        try {
            sourceRepository = new SourceRepositoryImpl();

            sourcesInput = buildJComboBox(sourceRepository.read());
        }
        catch (SQLException exception) {
            sourcesInput.removeAllItems();
            sourcesInput.setEnabled(false);

            DialogUtils.showError(this, "Error occurred, sources can't be shown!");
        }

        JLabel authorsLabel = new JLabel("Author");
        authorsLabel.setFont(authorsLabel.getFont().deriveFont(16f));
        authorsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        try {
            authorRepository = new AuthorRepositoryImpl();

            authorInput = buildJComboBox(authorRepository.read());
        }
        catch (SQLException exception) {
            authorInput.removeAllItems();
            authorInput.setEnabled(false);

            DialogUtils.showError(this, "Error occurred, authors can't be shown!");
        }

        result.add(imageBox);
        result.add(Box.createVerticalStrut(10));
        result.add(addImgBtn);
        result.add(Box.createVerticalStrut(10));
        result.add(titleLabel);
        result.add(titleInput);
        result.add(Box.createVerticalStrut(10));
        result.add(linkLabel);
        result.add(linkInput);
        result.add(Box.createVerticalStrut(10));
        result.add(descriptionLabel);
        result.add(descriptionScrollInput);
        result.add(Box.createVerticalStrut(10));
        result.add(sourceLabel);
        result.add(sourcesInput);
        result.add(Box.createVerticalStrut(10));
        result.add(authorsLabel);
        result.add(authorInput);

        return result;
    }

    public JPanel buildRightColumn() {
        JPanel result = new JPanel();
        result.setBorder(BorderFactory.createEmptyBorder(5,5,5,15));
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));

        JLabel dropHereLabel = new JLabel("This article's categories - drop here");
        dropHereLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        targetCategories = buildCategoryJList(targetCategoriesModel);
        buildTargetEvents();

        JLabel arrowUpLabel = new JLabel("▲ drag up");
        arrowUpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        arrowUpLabel.setForeground(new Color(224, 240, 255));

        JLabel dragFromLabel = new JLabel("Available categories - drag from here");
        dragFromLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            categoryRepository = new CategoryRepositoryImpl();
            categoryRepository.read().forEach(startCategoriesModel::addElement);
        }
        catch (SQLException exception) {
            startCategoriesModel.removeAllElements();

            DialogUtils.showError(this, "Error occurred, categories can't be shown!");
        }

        startCategories = buildCategoryJList(startCategoriesModel);
        buildStartEvents();

        addCategoryBtn = new JButton("Add ▲");
        removeCategoryBtn = new JButton("Remove ▼");

        addCategoryBtn.addActionListener(event -> {
            Category selectedCategory = startCategories.getSelectedValue();
            if(selectedCategory != null) {
                targetCategoriesModel.addElement(selectedCategory);
                startCategoriesModel.removeElement(selectedCategory);
            }
        });

        removeCategoryBtn.addActionListener(event -> {
            Category selectedCategory = targetCategories.getSelectedValue();
            if(selectedCategory != null) {
                startCategoriesModel.addElement(selectedCategory);
                targetCategoriesModel.removeElement(selectedCategory);
            }
        });

        JPanel btnSection = new JPanel();
        btnSection.add(addCategoryBtn);
        btnSection.add(removeCategoryBtn);

        result.add(dropHereLabel);
        result.add(Box.createVerticalStrut(7));
        result.add(new JScrollPane(targetCategories));
        result.add(Box.createVerticalStrut(7));
        result.add(arrowUpLabel);
        result.add(Box.createVerticalStrut(7));
        result.add(dragFromLabel);
        result.add(Box.createVerticalStrut(7));
        result.add(new JScrollPane(startCategories));
        result.add(Box.createVerticalStrut(7));
        result.add(btnSection);

        return  result;
    }

    public JPanel buildBottomContent() {
        JPanel result = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        saveBtn = new JButton("Save");
        saveBtn.addActionListener(event -> {
           if(!ValidateInputs()) {
               return;
           }

           if(article == null) {
               callRepository(0);

               return;
           }

           callRepository(article.getArticleId());
        });

        result.add(saveBtn);

        return result;
    }


    private void callRepository(int articleId) {
        Article articleForDB = new Article(
                articleId,
                titleInput.getText(),
                descriptionInput.getText().trim(),
                buildLink(),
                LocalDateTime.now(),
                null,
                (Source) sourcesInput.getSelectedItem()
        );

        if(authorInput.getSelectedItem() != null) {
            articleForDB.setAuthors(List.of((Author) authorInput.getSelectedItem()));
        }

        if(!targetCategoriesModel.isEmpty()) {
            List<Category> categories = new ArrayList<>();

            for(int i = 0; i < targetCategoriesModel.getSize(); i++) {
                categories.add(targetCategoriesModel.get(i));
            }

            articleForDB.setCategories(categories);
        }

        new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                if(articleForDB.getArticleId() == 0) {
                     return articleRepository.create(articleForDB);
                }

                articleRepository.update(articleForDB);

                return 0;
            }

            @Override
            protected void done() {
                try {
                    int operation = get();

                    String msg = (operation == 0) ?
                            "Article successfully created." :
                            "Article successfully updated.";

                    DialogUtils.showInfo(BaseDialog.this, msg);
                }
                catch (ExecutionException | InterruptedException e) {
                    DialogUtils.showInfo(BaseDialog.this, "Unknown error occurred. ");
                }
            }

        }.execute();
    }


    private void buildStartEvents() {
        startCategories.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                targetCategories.getSelectionModel().clearSelection();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }
        });
    }

    private void buildTargetEvents() {
        targetCategories.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCategories.getSelectionModel().clearSelection();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }
        });
    }


    private <T> JComboBox<T> buildJComboBox(List<T> items) {
        JComboBox<T> result = new JComboBox<>();
        result.setAlignmentX(Component.LEFT_ALIGNMENT);
        result.setFont(result.getFont().deriveFont(14f));
        result.setMaximumSize(new Dimension(Integer.MAX_VALUE, result.getPreferredSize().height));

        items.forEach(result::addItem);

        result.setSelectedItem(null);
        result.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Source source) {
                    setText(source.getName());
                }

                if (value instanceof Author author) {
                    setText(author.getName());
                }

                return this;
            }
        });

        return result;
    }

    private JList<Category> buildCategoryJList(DefaultListModel<Category> model) {
        JList<Category> result = new JList<>(model);
        result.setDragEnabled(true);
        result.setDropMode(DropMode.INSERT);
        result.setVisibleRowCount(15);
        result.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof Category category) {
                    setText(category.getName());
                }

                return this;
            }
        });

        return result;
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
                        16, 16);

                graphics2D.dispose();

                super.paintComponent(graphics);
            }
        };
        chip.setOpaque(false);

        chip.setBackground(new Color(38, 108, 191));
        chip.setForeground(Color.WHITE);

        chip.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        return chip;
    }

    private String buildLink() {
        String result = linkInput.getText().trim();

        if(!result.startsWith("https://")) {
            return "https://" + result;
        }

        return result;
    }


    private boolean ValidateInputs() {
        if(titleInput.getText().isEmpty()) {
            DialogUtils.showError(this, "Title can't be empty!");
            return false;
        }

        if(linkInput.getText().isEmpty()) {
            DialogUtils.showError(this, "Link can't be empty!");
            return false;
        }

        if(sourcesInput.getSelectedItem() == null) {
            DialogUtils.showError(this, "Source must be selected!");
            return false;
        }

        return true;
    }

    private void exportLogic() {
        File file = DialogUtils.chooseSaveFile(this, "xml").orElse(null);
        if(file == null) {
            return;
        }

        try {
            JAXBContext JAXB = JAXBContext.newInstance(Article.class);

            Marshaller convertor = JAXB.createMarshaller();
            convertor.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            convertor.marshal(article, file);

            DialogUtils.showInfo(this, "Article was exported successfully.");
        }
        catch (JAXBException exception) {
            DialogUtils.showError(this, "Error occurred..");
        }
    }


    public void setCurrentValues() {
        titleInput.setText(article.getTitle());
        linkInput.setText(article.getLink());
        descriptionInput.setText(article.getDescription());

        sourcesInput.setSelectedItem(article.getSource());

        if(!article.getAuthors().isEmpty()) {
            authorInput.setSelectedItem(article.getAuthors().getFirst());
        }

        article.getCategories().forEach(targetCategoriesModel::addElement);
    }
}
