package hr.algebra.utilities.gui;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.util.Optional;

public final class Dialog {

    private Dialog() { }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static boolean confirm(Component parent, String message) {
        int answer = JOptionPane.showConfirmDialog(
                parent, message,
                "Please confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return answer == JOptionPane.YES_OPTION;
    }

    public static Optional<File> chooseSaveFile(Component parent, String extension) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save file");

        chooser.setFileFilter(new FileNameExtensionFilter(
                extension.toUpperCase() + " files", extension)
        );

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return Optional.empty();
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith("." + extension)) {
            file = new File(file.getAbsolutePath() + "." + extension);
        }
        return Optional.of(file);
    }
}