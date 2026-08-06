package hr.algebra.app.forms;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private final JTextField     usernameField    = new JTextField();
    private final JPasswordField passwordField    = new JPasswordField();
    private final JPasswordField confirmField     = new JPasswordField();

    JButton loginLink;
    JButton registerButton;

    public RegisterPanel() {
        buildUi();
    }

    private void buildUi() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.weightx = 1.0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(10, 40, 4, 40);
        JLabel usernameLabel = new JLabel("Username");
        Font labelFont = usernameLabel.getFont().deriveFont(14f);
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(Color.LIGHT_GRAY);
        add(usernameLabel, gc);

        gc.gridy = 1;
        usernameField.setFont(labelFont);
        usernameField.putClientProperty("FlatLaf.style", "arc: 12; margin: 6, 8, 6, 8");
        add(usernameField, gc);

        gc.gridy = 2;
        gc.insets = new Insets(10, 40, 4, 40);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.LIGHT_GRAY);
        add(passwordLabel, gc);

        gc.gridy = 3;
        gc.insets = new Insets(4, 40, 4, 40);
        passwordField.setFont(labelFont);
        passwordField.putClientProperty("FlatLaf.style", "arc: 12; margin: 6, 8, 6, 8");
        add(passwordField, gc);

        gc.gridy = 4;
        gc.insets = new Insets(10, 40, 4, 40);
        JLabel confirmLabel = new JLabel("Confirm");
        confirmLabel.setFont(labelFont);
        confirmLabel.setForeground(Color.LIGHT_GRAY);
        add(confirmLabel, gc);

        gc.gridy = 5;
        gc.insets = new Insets(4, 40, 4, 40);
        confirmField.setFont(labelFont);
        confirmField.putClientProperty("FlatLaf.style", "arc: 12; margin: 6, 8, 6, 8");
        add(confirmField, gc);

        gc.gridy = 6;
        gc.insets = new Insets(10, 40, 5, 40);
        registerButton = new JButton("Create account");
        registerButton.putClientProperty("JButton.buttonType", "default");
        registerButton.putClientProperty("FlatLaf.style", "arc: 12; margin: 7, 0, 7, 0");
        add(registerButton, gc);

        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(labelFont);
        haveAccountLabel.setForeground(Color.LIGHT_GRAY);
        loginLink = new JButton("Sign in here");
        loginLink.setFont(loginLink.getFont().deriveFont(13.5f));
        loginLink.putClientProperty("JButton.buttonType", "borderless");
        loginLink.putClientProperty("FlatLaf.style",
                "foreground: $Component.accentColor; margin: 0,0,0,0; focusWidth: 0");

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        registerRow.add(haveAccountLabel);
        registerRow.add(loginLink);

        gc.gridy = 7;
        gc.insets = new Insets(4, 40, 0, 40);
        add(registerRow, gc);
    };

    public JButton getRegisterButton() {
        return registerButton;
    }

    public JButton getLoginLink() {
        return loginLink;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public char[] getConfirm() {
        return confirmField.getPassword();
    }
}
