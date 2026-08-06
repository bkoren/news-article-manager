package hr.algebra.app;

import com.sun.tools.javac.Main;
import hr.algebra.app.windows.AuthWindow;
import com.formdev.flatlaf.FlatDarkLaf;
import hr.algebra.app.windows.MainWindow;
import hr.algebra.dao.models.User;
import hr.algebra.dao.repositories.user.UserRepositoryImpl;

import javax.swing.*;
import java.sql.SQLException;

public class Program {
    public static void main(String[] args) throws SQLException {
        FlatDarkLaf.setup();
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 999);

        UserRepositoryImpl repo = new UserRepositoryImpl();
        User user = repo.getByUsername("admin");

        SwingUtilities.invokeLater(() -> {
            new MainWindow(user);
        });
    }
}