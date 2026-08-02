package hr.algebra.dao.rss;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.UUID;

public class AssetService {
    private final Path FOLDER = Paths.get("assets");

    public AssetService() throws IOException {
        Files.createDirectories(FOLDER);
    }

    String downloadImage(String imgUrl) throws IOException {
        try(var files = Files.newDirectoryStream(FOLDER)) {
            for (Path file : files) {
                Files.delete(file);
            }
        }

        URI url = URI.create(imgUrl);
        Path target = FOLDER.resolve(generateId());

        try(InputStream stream = url.toURL().openStream()) {
            Files.copy(stream, target);
        }

        return target.toString();
    }

    private String generateId() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 12);
    }
}
