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

    private String generateId(String img, String imgExt) {
        if(img == null) {
            return null;
        }

        if(imgExt == null) {
            imgExt = img.substring(img.lastIndexOf('.') + 1);
        }

        return UUID.randomUUID().toString().substring(0, 12) + "." + imgExt;
    }


    public String downloadImage(String imgUrl, String imgExt) throws IOException {
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

    public Path saveImgToFolder(Path imgPath) throws IOException {
        String id = generateId(imgPath.toString(), null);

        Path target = FOLDER.resolve(id);
        Files.copy(imgPath, target);

        return target;
    }

    public void removeImage(String imagePath) throws AssetException {
        if(imagePath == null) {
            return;
        }

        Path file = Paths.get(imagePath);
        try {
            if(!file.getFileName().toString().equals("default.jpg")) {
                Files.delete(file);
            }
        }
        catch (IOException exception) {
            throw new AssetException(
                    "Failed to delete image at " + imagePath
                    ,exception
            );
        }
    }

    public void clearFolder() throws AssetException{
        try(var files = Files.newDirectoryStream(FOLDER)) {
            for (Path file : files) {
                if(!file.getFileName().toString().equals("default.jpg")) {
                    Files.delete(file);
                }
            }
        }
        catch (IOException exception) {
            throw new AssetException(
                    "Failed to clear the assets folder at " + FOLDER
                    ,exception
            );
        }
    }
}
