package hr.algebra.dao.rss;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssParser {
    private final RssSource source;

    private String imgExtension;

    RssParser(RssSource source) {
        this.source = source;
    }

    List<RssItem> parseItems() throws ParserConfigurationException, IOException, SAXException {
        List<RssItem> items = new ArrayList<>();

        Document document = fillTheDocument();
        NodeList itemNodes = document.getElementsByTagName("item");
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Node node = itemNodes.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element itemElement = (Element) node;
            items.add(
                new RssItem(
                        getTagValue(itemElement, "title"),
                        getTagValue(itemElement, "link"),
                        getTagValue(itemElement, "description"),
                        getTagValue(itemElement, "pubDate"),
                        extractImgPath(itemElement),
                        getTagValues(itemElement, "category"),
                        getTagValues(itemElement, "dc:creator")
                )
            );
        }

        return items;
    }

    private Document fillTheDocument() throws ParserConfigurationException, SAXException, IOException {
        HttpURLConnection connection = (HttpURLConnection)
                URI.create(source.getFeedUrl()).toURL().openConnection();

        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "news-article-manager/1.0");
        if (connection.getResponseCode() != 200)
            throw new IOException("Failed to fetch RSS feed from " + source.getName());

        try (InputStream stream = connection.getInputStream()) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            return builder.parse(stream);
        }
        finally {
            connection.disconnect();
        }

    }

    private String getTagValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0)
            return null;

        return nodes.item(0).getTextContent().trim();
    }

    private List<String> getTagValues(Element parent, String tagName) {
        List<String> values = new ArrayList<>();

        NodeList nodes = parent.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++)
            values.add(nodes.item(i).getTextContent().trim());

        return values;
    }

    private String extractImgPath(Element itemElement) {
        String imgPath;

        NodeList enclosure = itemElement.getElementsByTagName("enclosure");
        if (enclosure.getLength() > 0) {
            imgPath = ((Element) enclosure.item(0)).getAttribute("url");

            if(isValid(imgPath)) {
                imgExtension = extractExtension(imgPath);

                return removeParams(imgPath);
            }
        }

        NodeList encoded = itemElement.getElementsByTagName("content:encoded");
        if (encoded.getLength() > 0) {
            String html = encoded.item(0).getTextContent();
            imgPath = imgEncodedExt(html);

            if(imgPath != null && isValid(imgPath)) {
                imgExtension = extractExtension(imgPath);

                return removeParams(imgPath);
            }
        }

        NodeList itunes = itemElement.getElementsByTagName("itunes:image");
        if(itunes.getLength() > 0) {
            imgPath = ((Element) itunes.item(0)).getAttribute("href");

            if(isValid(imgPath)) {
                imgExtension = extractExtension(imgPath);

                return removeParams(imgPath);
            }
        }

        return null;
    }

    private String imgEncodedExt(String value) {
        Matcher matcher = Pattern.compile("<img[^>]+src=\"([^\"]+)\"")
                .matcher(RemoveIllegalChar(value));

        return matcher.find() ?
                matcher.group(1) :
                null;
    }

    private String extractExtension(String imgUrl) {
        String imgUrlWithNoParams = removeParams(imgUrl);

        return imgUrlWithNoParams.substring(imgUrlWithNoParams.lastIndexOf('.') != -1 ?
                imgUrlWithNoParams.lastIndexOf('.') + 1:
                imgUrlWithNoParams.length()
        );
    }

    private String removeParams(String imgUrl) {
        return imgUrl.substring(0, imgUrl.lastIndexOf('?') != -1 ?
                imgUrl.lastIndexOf('?') :
                imgUrl.length()
        );
    }

    private CharSequence RemoveIllegalChar(String value) {
        return value
                .replace("&#038", "&")
                .replace("&amp", "&");
    }

    private boolean isValid(String imgUrl) {
        String extension = extractExtension(imgUrl);

        return extension.contains("jpg") || extension.contains("jpeg") || extension.contains("png");
    }

    public String giveBackImgExt() {
        return imgExtension;
    }
}
