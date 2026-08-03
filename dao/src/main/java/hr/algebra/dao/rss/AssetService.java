package hr.algebra.dao.rss;

import hr.algebra.dao.exceptions.AssetException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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

    private String generateId(String imgUrl) throws AssetException {
        String imgUrlWithNoParams =
                imgUrl.substring(0, imgUrl.lastIndexOf('?') != -1 ?
                        imgUrl.lastIndexOf('?') :
                        imgUrl.length()
                );

        String ext =
                imgUrlWithNoParams.substring(imgUrlWithNoParams.lastIndexOf('.') != -1 ?
                        imgUrlWithNoParams.lastIndexOf('.') + 1:
                        imgUrlWithNoParams.length()
                );

        if(!(ext.contains("jpg") || ext.contains("jpeg") || ext.contains("png")))
            return null;

        return UUID.randomUUID().toString().substring(0, 12) + "." + ext;
    }

    String downloadImage(String imgUrl) throws AssetException, IOException {
        if (imgUrl == null)
            return null;

        URI url = URI.create(imgUrl);

        String id = generateId(imgUrl);
        if(id == null)
            return null;

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

        } catch (IOException exception) {
            return null;

        } finally {
            connection.disconnect();
        }
    }
}
