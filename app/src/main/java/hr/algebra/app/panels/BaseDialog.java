package hr.algebra.app.panels;

import hr.algebra.dao.models.Article;
import hr.algebra.dao.models.Category;
import hr.algebra.dao.models.Source;
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

public class BaseDialog extends JDialog {
    private Article article;

    private final int DIALOG_WIDTH;
    private final int DIALOG_HEIGHT;

    private final String IMAGE_FOLDER = "assets";

    private JLabel    imageBox;

    private JButton             addImgBtn;
    private JTextField          titleInput;
    private JTextArea           descriptionInput;
    private JComboBox<Category> categoriesInput;
    private JComboBox<Source>   sourcesInput;

    protected BaseDialog(Article selectedArticle, String dialogTitle) {
        this.article = selectedArticle;

        DIALOG_HEIGHT = 640;
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

        imageBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 10, true));
        imageBox.setPreferredSize(new Dimension(width, height));

        if (article == null || article.getImagePath() == null) {
            imageBox.setText("No image available.");
            return;
        }

        Path imageFile = Paths.get(IMAGE_FOLDER)
                .resolve(Paths.get(article.getImagePath()).getFileName());

        if (!Files.exists(imageFile)) {
            imageBox.setText("Image not found.");
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
                                ? (String.join(" | ", article.getAuthors().toString().replace("[", "").replace("]", "")))
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

        result.add(categoriesView, BorderLayout.NORTH);
        result.add(descriptionView, BorderLayout.CENTER);

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
                    DialogUtils.showError(BaseDialog.super.getParent(),
                            "Opening a browser is not supported on this system.");
                    return;
                }

                try {
                    Desktop.getDesktop().browse(new URI(article.getLink()));
                }
                catch (IOException | URISyntaxException | IllegalArgumentException exception) {
                    DialogUtils.showError(BaseDialog.super.getParent(),
                            "Could not open the link in the browser.");
                }
            }
        });

        JButton exportBtn = new JButton("Export");
        exportBtn.setBackground(Color.BLUE);

        exportBtn.addActionListener(event -> {
            exportLogic();
        });

        result.add(linkView, BorderLayout.WEST);
        result.add(exportBtn, BorderLayout.EAST);

        return result;
    }

    public JPanel buildLeftColumn() {
        JPanel result = new JPanel();
        result.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
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

        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(16f));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionInput = new JTextArea();
        descriptionInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionInput.setRows(3);
        descriptionInput.setLineWrap(true);
        descriptionInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, descriptionInput.getPreferredSize().height * 3));

        JLabel sourceLabel = new JLabel("Source");
        sourceLabel.setFont(sourceLabel.getFont().deriveFont(16f));
        sourceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sourcesInput = new JComboBox<>();
        sourcesInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourcesInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, sourcesInput.getPreferredSize().height));

        JLabel categoriesLabel = new JLabel("Categories");
        categoriesLabel.setFont(categoriesLabel.getFont().deriveFont(16f));
        categoriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        categoriesInput = new JComboBox<>();
        categoriesInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoriesInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, categoriesInput.getPreferredSize().height));

        result.add(imageBox);
        result.add(Box.createVerticalStrut(10));
        result.add(addImgBtn);
        result.add(Box.createVerticalStrut(10));
        result.add(titleLabel);
        result.add(titleInput);
        result.add(Box.createVerticalStrut(10));
        result.add(descriptionLabel);
        result.add(descriptionInput);
        result.add(Box.createVerticalStrut(10));
        result.add(sourceLabel);
        result.add(sourcesInput);
        result.add(Box.createVerticalStrut(10));
        result.add(categoriesLabel);
        result.add(categoriesInput);

        return result;
    }

    public JPanel buildRightColumn() {
        JPanel result = new JPanel();

        JLabel authorsLabel = new JLabel("This article's authors - drop here");

        result.add(authorsLabel);

        return  result;
    }

    public JPanel buildBottomContent() {
        JPanel result = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton saveBtn = new JButton("Save");
        result.add(saveBtn);

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
}
