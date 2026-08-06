package hr.algebra.app.windows;

import hr.algebra.app.forms.LoginPanel;
import hr.algebra.app.forms.RegisterPanel;
import hr.algebra.dao.models.User;
import hr.algebra.dao.repositories.user.UserRepositoryImpl;
import hr.algebra.utilities.gui.DialogUtils;
import hr.algebra.utilities.gui.Icons;
import hr.algebra.utilities.security.Password;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Arrays;

public class AuthWindow extends JFrame {
    JTabbedPane tabs            = new JTabbedPane();

    LoginPanel loginPanel       = new LoginPanel();
    RegisterPanel registerPanel = new RegisterPanel();

    UserRepositoryImpl userRepository = new UserRepositoryImpl();

    public AuthWindow() throws SQLException {
        buildUi();
    }

    private void buildUi() {
        setTitle("News App - Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setIconImage(Icons.load("favicon.png").getImage());
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
            String username = loginPanel.getUsername();
            try {
                if(userRepository.exists(username)) {
                    User user = userRepository.getByUsername(username);

                    char[] password = loginPanel.getPassword();
                    if(Password.verify(new String(password), user.getPasswordHash())) {
                        OpenMainWindow(user);

                        this.dispose();
                    }
                    else {
                        DialogUtils.showError(this, "Wrong password!");
                    }
                }
                else {
                    DialogUtils.showError(this, "User doesn't exist.");
                }
            } catch (SQLException exception) {
                DialogUtils.showError(this, "Could not reach the database.");
            }
        });

        registerPanel.getRegisterButton().addActionListener(e -> {
            String username =  registerPanel.getUsername();
            try {
                if(!userRepository.exists(username)) {
                    char[] password = registerPanel.getPassword();
                    char[] confirm  = registerPanel.getConfirm();

                    if(password.length > 6) {
                        if (Arrays.equals(password, confirm)) {
                            String passwordHash = Password.hash(new String(password));

                            userRepository.register(new User(0, username, passwordHash));

                            DialogUtils.showInfo(this, "Registration successful.");

                            tabs.setSelectedIndex(0);
                        } else {
                            DialogUtils.showError(this, "Passwords doesn't match.");
                        }
                    }
                    else {
                        DialogUtils.showError(this, "Password must contain at least 6 digits.");
                    }
                }
                else {
                    DialogUtils.showError(this, "Username already in use.");
                }
            }
            catch (SQLException exception) {
                DialogUtils.showError(this, "Could not reach the database.");
            }
        });

        getRootPane().setDefaultButton(loginPanel.getSignInButton());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) {
                getRootPane().setDefaultButton(loginPanel.getSignInButton());
            } else {
                getRootPane().setDefaultButton(registerPanel.getRegisterButton());
            }
        });
    }

    private void OpenMainWindow(User user) {
        MainWindow mainWindow = new MainWindow(user);
        mainWindow.setVisible(true);
    }
}
