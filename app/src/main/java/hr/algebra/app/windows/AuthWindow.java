package hr.algebra.app.windows;

import com.formdev.flatlaf.FlatDarkLaf;
import hr.algebra.app.forms.LoginPanel;
import hr.algebra.app.forms.RegisterPanel;
import hr.algebra.utilities.gui.Icons;

import javax.swing.*;
import java.awt.*;

public class AuthWindow extends JFrame {
    public AuthWindow() {
        setTitle("News App - Sign in");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs            = new JTabbedPane();
        LoginPanel loginPanel       = new LoginPanel();
        RegisterPanel registerPanel = new RegisterPanel();

        tabs.addTab("Login", loginPanel);
        tabs.addTab("Register", registerPanel);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        add(tabs);

        setIconImage(Icons.load("favicon.png").getImage());
        setSize(380, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        getRootPane().setDefaultButton(loginPanel.getSignInButton());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) {
                getRootPane().setDefaultButton(loginPanel.getSignInButton());
            } else {
                getRootPane().setDefaultButton(registerPanel.getRegisterButton());
            }
        });
    }
}
