package hr.algebra.app.forms;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final JTextField     usernameField  = new JTextField();
    private final JPasswordField passwordField  = new JPasswordField();
    private final JButton        signInButton   = new JButton("Sign in");
    private final JLabel         usernameLabel  = new JLabel("Username");
    private final JLabel         passwordLabel  = new JLabel("Password");
    private final JLabel         newHereLabel   = new JLabel("New here?");
    private final JButton        registerLink   = new JButton("Click here to register");

    public LoginPanel() {
        buildUi();
    }

    private void buildUi() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        Font labelFont = usernameLabel.getFont().deriveFont(14f);

        gc.gridx = 0;
        gc.weightx = 1.0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(10, 40, 4, 40);
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(Color.LIGHT_GRAY);
        add(usernameLabel, gc);

        gc.gridy = 1;
        usernameField.setFont(labelFont);
        usernameField.putClientProperty("FlatLaf.style", "arc: 12; margin: 6, 8, 6, 8");
        add(usernameField, gc);

        gc.gridy = 2;
        gc.insets = new Insets(20, 40, 4, 40);
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.LIGHT_GRAY);
        add(passwordLabel, gc);

        gc.gridy = 3;
        gc.insets = new Insets(4, 40, 4, 40);
        passwordField.setFont(labelFont);
        passwordField.putClientProperty("FlatLaf.style", "arc: 12; margin: 6, 8, 6, 8");
        add(passwordField, gc);

        gc.gridy = 4;
        gc.insets = new Insets(75, 40, 5, 40);
        signInButton.putClientProperty("JButton.buttonType", "default");
        signInButton.putClientProperty("FlatLaf.style", "arc: 12; margin: 7, 0, 7, 0");
        add(signInButton, gc);

        newHereLabel.setFont(labelFont);
        newHereLabel.setForeground(Color.LIGHT_GRAY);
        registerLink.setFont(registerLink.getFont().deriveFont(13.5f));
        registerLink.putClientProperty("JButton.buttonType", "borderless");
        registerLink.putClientProperty("FlatLaf.style",
                "foreground: $Component.accentColor; margin: 0,0,0,0; focusWidth: 0");

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        registerRow.add(newHereLabel);
        registerRow.add(registerLink);

        gc.gridy = 5;
        gc.insets = new Insets(4, 40, 0, 40);
        add(registerRow, gc);
    }

    public JButton getSignInButton() {
        return signInButton;
    }

    public JButton getRegisterLink() {
        return registerLink;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }
}