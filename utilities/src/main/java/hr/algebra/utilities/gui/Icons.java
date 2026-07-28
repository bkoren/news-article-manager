package hr.algebra.utilities.gui;

import hr.algebra.utilities.config.ConfigException;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class Icons {

    private static final String ICON_PATH = "/icons/";

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    private Icons() { }

    public static ImageIcon load(String fileName) {
        return CACHE.computeIfAbsent(fileName, name -> {

            URL url = Icons.class.getResource(ICON_PATH + name);

            if (url == null) {
                throw new ConfigException("Icon not found on classpath: " + ICON_PATH + name);
            }

            return new ImageIcon(url);
        });
    }
}