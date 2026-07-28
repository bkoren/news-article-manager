package hr.algebra.utilities.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Config {
    private Config() { }

    private static final Properties PROPERTIES = new Properties();

    private static final String FILE_NAME = "config.properties";

    static {
        Path path = Path.of(FILE_NAME);

        try (InputStream in = Files.newInputStream(path)) {
            PROPERTIES.load(in);
        }
        catch (IOException e) {
            throw new ConfigException(
                    "Could not read " + FILE_NAME + ". Expected it in the project root: "
                    + path.toAbsolutePath(), e
            );
        }
    }


    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new ConfigException("Missing setting '" + key + "' in " + FILE_NAME);
        }
        return value;
    }
}