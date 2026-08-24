package hr.algebra.app.windows;

import hr.algebra.app.panels.LoginPanel;
import hr.algebra.app.panels.RegisterPanel;
import hr.algebra.dao.models.User;
import hr.algebra.dao.repositories.user.UserRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;
import hr.algebra.utilities.security.Password;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Arrays;

public class AuthWindow extends JFrame {
    LoginPanel loginPanel       = new LoginPanel();
    RegisterPanel registerPanel = new RegisterPanel();

    JTabbedPane tabs = new JTabbedPane();

    UserRepositoryImpl userRepository;

    public AuthWindow() {
        userRepository = new UserRepositoryImpl();

        buildUi();
    }

    private void buildUi() {
        setTitle("News App - Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setIconImage(new ImageIcon("/icons/favicon.png").getImage());
        setSize(380, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        tabs.addTab("Login", loginPanel);
        tabs.addTab("Register", registerPanel);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        add(tabs);

        loginPanel.getRegisterLink().addActionListener(e ->
                tabs.setSelectedIndex(1)
        );

        registerPanel.getLoginLink().addActionListener(e ->
                tabs.setSelectedIndex(0)
        );

        loginPanel.getSignInButton().addActionListener(e -> {
            if(!loginPanel.getUsername().isEmpty() && loginPanel.getPassword().length > 0) {
                loginLogic();
            }
        });

        registerPanel.getRegisterButton().addActionListener(e -> {
            if(!registerPanel.getUsername().isEmpty() && registerPanel.getPassword().length > 0) {
                registerLogic();
            }
        });

        getRootPane().setDefaultButton(loginPanel.getSignInButton());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) {
                getRootPane().setDefaultButton(loginPanel.getSignInButton());
            }
            else {
                getRootPane().setDefaultButton(registerPanel.getRegisterButton());
            }
        });

        this.setVisible(true);
    }

    private void registerLogic() {
        String username =  registerPanel.getUsername();
        try {
            if(userRepository.exists(username)) {
                DialogUtils.showError(this, "Username is already registered.");
                return;
            }

            char[] password = registerPanel.getPassword();
            char[] confirm  = registerPanel.getConfirm();

            if(password.length < 6) {
                DialogUtils.showError(this, "Password must be at least 6 characters long.");
                return;
            }

            if (!Arrays.equals(password, confirm)) {
                DialogUtils.showError(this, "The passwords do not match.");
                return;
            }

            userRepository.register(new User(0, username, Password.hash(new String(password))));

            DialogUtils.showInfo(this, "Registration complete. Please sign in.");

            tabs.setSelectedIndex(0);
        }
        catch (SQLException exception) {
            DialogUtils.showError(this, "A database error occurred. Please try again.");
        }
    }

    private void loginLogic() {
        String username = loginPanel.getUsername();
        try {
            if(userRepository.exists(username)) {
                DialogUtils.showError(this, "Invalid username or password.");
                return;
            }

            User user = userRepository.getByUsername(username);
            char[] password = loginPanel.getPassword();

            if(!Password.verify(new String(password), user.getPasswordHash())) {
                DialogUtils.showError(this, "Wrong password,");
                return;
            }

            OpenMainWindow(user);
            this.dispose();

        } catch (SQLException exception) {
            DialogUtils.showError(this, "A database error occurred. Please try again.");
        }
    }

    private void OpenMainWindow(User user) {
        MainWindow mainWindow = new MainWindow(user);
        mainWindow.setVisible(true);
    }
}
