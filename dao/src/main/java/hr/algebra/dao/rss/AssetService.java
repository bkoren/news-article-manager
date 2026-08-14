package hr.algebra.dao.rss;

import hr.algebra.dao.exceptions.AssetException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssetService {
    private final Path FOLDER = Paths.get("assets");

    public AssetService() throws AssetException {
        try {
            Files.createDirectories(FOLDER);
        }
        catch (IOException exception) {
            throw new AssetException(
                "Failed to create folder: " + FOLDER
                ,exception
            );
        }
    }

    public void clearFolder() throws AssetException{
        try(var files = Files.newDirectoryStream(FOLDER)) {
            for (Path file : files) {
                Files.delete(file);
            }
        }
        catch (IOException exception) {
            throw new AssetException(
                    "Failed to clear the assets folder at " + FOLDER
                    ,exception
            );
        }
    }

    public void removeImage(String imagePath) throws AssetException {
        if(imagePath == null) {
            return;
        }

        Path file = Paths.get(imagePath);
        try {
            Files.delete(file);
        }
        catch (IOException exception) {
            throw new AssetException(
                    "Failed to delete image at " + imagePath
                    ,exception
            );
        }
    }

    private String generateId(String imgUrl, String imgExt) {
        if(imgUrl == null) {
            return null;
        }

        return UUID.randomUUID().toString().substring(0, 12) + "." + imgExt;
    }

    String downloadImage(String imgUrl, String imgExt) throws IOException {
        String id = generateId(imgUrl, imgExt);
        if(id == null)
            return null;

        URI url;
        try {
            url = URI.create(imgUrl);
        }
        catch (IllegalArgumentException exception) {
            return null;
        }

        HttpURLConnection connection = (HttpURLConnection)
                url.toURL().openConnection();

        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "news-article-manager/1.0");

        try (InputStream stream = connection.getInputStream()) {
            Path target = FOLDER.resolve(id);
            Files.copy(stream, target);

            return target.toString();
        }
        catch (IOException exception) {
            return null;
        }
        finally {
            connection.disconnect();
        }
    }
}
