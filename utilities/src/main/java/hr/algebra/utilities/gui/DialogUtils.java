package hr.algebra.utilities.gui;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.util.Optional;

public final class DialogUtils {

    private DialogUtils() { }

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

    public static Optional<File> saveFile(Component parent, String extension) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select file");
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

    public static Optional<File> uploadImg(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose an image");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Images (png, jpg)", "png", "jpg", "jpeg"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);

        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return Optional.of(chooser.getSelectedFile());
        }

        return Optional.empty();
    }
}