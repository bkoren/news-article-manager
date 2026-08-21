package hr.algebra.app.windows;

import hr.algebra.app.forms.AdminPanel;
import hr.algebra.app.forms.ArticlePanel;
import hr.algebra.app.forms.AuthorPanel;
import hr.algebra.app.forms.CategoryPanel;
import hr.algebra.dao.models.Role;
import hr.algebra.dao.models.User;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.dao.repositories.source.SourceRepository;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame{
    private final CardLayout containersLayout = new CardLayout();
    private final JPanel     displayContent   = new JPanel(containersLayout);

    private final SourceRepositoryImpl sourceRepository;
    private final AuthorRepositoryImpl authorRepository;
    private final CategoryRepositoryImpl categoryRepository;
    private final ArticleRepositoryImpl articleRepository;

    private final User user;
    public MainWindow(User user) {
        this.user = user;

        sourceRepository = new SourceRepositoryImpl();
        authorRepository = new AuthorRepositoryImpl();
        categoryRepository = new CategoryRepositoryImpl();
        articleRepository = new ArticleRepositoryImpl(
                authorRepository,
                categoryRepository
        );

        ArticlePanel articlePanel = new ArticlePanel(articleRepository);
        displayContent.add(articlePanel, "articles");

        AuthorPanel authorPanel = new AuthorPanel(authorRepository);
        displayContent.add(authorPanel, "authors");

        CategoryPanel categoryPanel = new CategoryPanel(categoryRepository);
        displayContent.add(categoryPanel, "categories");

        AdminPanel adminPanel = new AdminPanel(
                sourceRepository,
                authorRepository,
                categoryRepository,
                articleRepository
        );
        displayContent.add(adminPanel, "admin");

        containersLayout.show(displayContent, "articles");
        add(displayContent);

        buildUi();
    }

    private void buildUi() {
        setTitle("News App");
        setSize(700, 620);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton articlesMenu = new JButton("Articles");
        articlesMenu.setContentAreaFilled(false);
        articlesMenu.setBorderPainted(false);

        JButton authorsMenu = new JButton("Authors");
        authorsMenu.setContentAreaFilled(false);
        authorsMenu.setBorderPainted(false);

        JButton categoriesMenu = new JButton("Categories");
        categoriesMenu.setContentAreaFilled(false);
        categoriesMenu.setBorderPainted(false);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(articlesMenu);
        menuBar.add(authorsMenu);
        menuBar.add(categoriesMenu);

        if(user.getRole() == Role.ADMIN) {
            JButton adminMenu = new JButton("Admin");
            adminMenu.setContentAreaFilled(false);
            adminMenu.setBorderPainted(false);
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

        openArticlePanel();
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
