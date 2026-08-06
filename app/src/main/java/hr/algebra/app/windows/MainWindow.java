package hr.algebra.app.windows;

import hr.algebra.app.forms.AdminPanel;
import hr.algebra.app.forms.ArticlePanel;
import hr.algebra.app.forms.AuthorPanel;
import hr.algebra.app.forms.CategoryPanel;
import hr.algebra.dao.models.Role;
import hr.algebra.dao.models.User;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame{
    private final JMenuBar menuBar = new JMenuBar();

    private final CardLayout containersLayout = new CardLayout();
    private final JPanel     displayContent   = new JPanel(containersLayout);

    private final User user;

    public MainWindow(User user) {
        this.user = user;

        ArticlePanel articlePanel = new ArticlePanel();
        displayContent.add(articlePanel, "articles");

        AuthorPanel authorPanel = new AuthorPanel();
        displayContent.add(authorPanel, "authors");

        CategoryPanel categoryPanel = new CategoryPanel();
        displayContent.add(categoryPanel, "categories");

        AdminPanel adminPanel = new AdminPanel();
        displayContent.add(adminPanel, "admin");

        containersLayout.show(displayContent, "articles");
        add(displayContent);

        buildUi();
    }

    private void buildUi() {
        setTitle("News App");
        setSize(700, 610);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton articlesMenu     = new JButton("Articles");
        JButton authorsMenu      = new JButton("Authors");
        JButton categoriesMenu   = new JButton("Categories");

        menuBar.add(articlesMenu);
        menuBar.add(authorsMenu);
        menuBar.add(categoriesMenu);

        if(user.getRole() == Role.ADMIN) {
            setTitle("");

            JButton adminMenu = new JButton("Admin");
            adminMenu.addActionListener(e -> {
                openAdminPanel();
            });
            menuBar.add(adminMenu);
        }

        articlesMenu.addActionListener(e -> {
            openArticlePanel();
        });

        authorsMenu.addActionListener(e -> {
            openAuthorsPanel();
        });

        categoriesMenu.addActionListener(e -> {
            openCategoriesPanel();
        });

        this.setJMenuBar(menuBar);
        this.setVisible(true);
    }

    private void openCategoriesPanel() {
        containersLayout.show(displayContent, "categories");
    }

    private void openAuthorsPanel() {
        containersLayout.show(displayContent, "authors");
    }

    private void openArticlePanel() {
        containersLayout.show(displayContent, "articles");
    }

    private void openAdminPanel() {
        containersLayout.show(displayContent, "admin");
    }
}
