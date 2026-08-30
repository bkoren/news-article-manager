package hr.algebra.app.windows;

import hr.algebra.app.panels.AdminPanel;
import hr.algebra.app.panels.ArticlePanel;
import hr.algebra.app.panels.AuthorPanel;
import hr.algebra.app.panels.CategoryPanel;
import hr.algebra.dao.exceptions.AssetException;
import hr.algebra.dao.models.Role;
import hr.algebra.dao.models.User;
import hr.algebra.dao.repositories.article.ArticleRepositoryImpl;
import hr.algebra.dao.repositories.author.AuthorRepositoryImpl;
import hr.algebra.dao.repositories.category.CategoryRepositoryImpl;
import hr.algebra.dao.repositories.source.SourceRepositoryImpl;
import hr.algebra.dao.rss.RssImportService;
import hr.algebra.utilities.gui.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame{
    private final CardLayout containersLayout = new CardLayout();
    private final JPanel     displayContent   = new JPanel(containersLayout);

    JMenuBar menuBar;
    JButton  articlesMenu;
    JButton  authorsMenu;
    JButton  categoriesMenu;
    JButton  adminMenu;

    private final User user;
    public MainWindow(User user) {
        this.user = user;

        SourceRepositoryImpl    sourceRepository   = new SourceRepositoryImpl();
        AuthorRepositoryImpl    authorRepository   = new AuthorRepositoryImpl();
        CategoryRepositoryImpl  categoryRepository = new CategoryRepositoryImpl();
        ArticleRepositoryImpl   articleRepository  = new ArticleRepositoryImpl(
                authorRepository,
                categoryRepository
        );

        RssImportService importService = new RssImportService(
                authorRepository,
                categoryRepository,
                sourceRepository,
                articleRepository
        );

        try {
            importService.importAllSourcesToDB();
        }
        catch (SQLException exception) {
            DialogUtils.showError(this, "A database error occurred. Application will not work properly.");
        }

        displayContent.add(new ArticlePanel(articleRepository), "articles");
        displayContent.add(new AuthorPanel(authorRepository), "authors");
        displayContent.add(new CategoryPanel(categoryRepository), "categories");
        displayContent.add(new AdminPanel(
                sourceRepository,
                authorRepository,
                categoryRepository,
                articleRepository,
                importService
        ), "admin");

        containersLayout.show(displayContent, "articles");
        add(displayContent);

        buildUi();
    }

    private void buildUi() {
        buildMainWindow();
        buildMainMenu();
        buildEvents();

        this.setJMenuBar(menuBar);
        this.setVisible(true);

        openArticlePanel();
    }

    private void buildMainWindow() {
        setTitle("News App");
        setSize(700, 620);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private void buildMainMenu() {
        articlesMenu = new JButton("Articles");
        articlesMenu.setContentAreaFilled(false);
        articlesMenu.setBorderPainted(false);

        authorsMenu = new JButton("Authors");
        authorsMenu.setContentAreaFilled(false);
        authorsMenu.setBorderPainted(false);

        categoriesMenu = new JButton("Categories");
        categoriesMenu.setContentAreaFilled(false);
        categoriesMenu.setBorderPainted(false);

        menuBar = new JMenuBar();
        menuBar.add(articlesMenu);
        menuBar.add(authorsMenu);
        menuBar.add(categoriesMenu);

        if(user.getRole() == Role.ADMIN) {
            adminMenu= new JButton("Admin");
            adminMenu.setContentAreaFilled(false);
            adminMenu.setBorderPainted(false);
            adminMenu.addActionListener(e -> {
                openAdminPanel();
            });
            menuBar.add(adminMenu);
        }

    }
    private void buildEvents() {
        articlesMenu.addActionListener(e -> {
            openArticlePanel();
        });

        authorsMenu.addActionListener(e -> {
            openAuthorsPanel();
        });

        categoriesMenu.addActionListener(e -> {
            openCategoriesPanel();
        });

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
