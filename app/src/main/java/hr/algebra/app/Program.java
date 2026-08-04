package hr.algebra.app;

import hr.algebra.app.windows.AuthWindow;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Program {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 999);
        SwingUtilities.invokeLater(() ->
                new AuthWindow().setVisible(true));
    }
}