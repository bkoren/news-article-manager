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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssParser {
    private final RssSource source;

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
                            imgPathExt(itemElement),
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

    private String imgPathExt(Element itemElement) {
        NodeList enclosure = itemElement.getElementsByTagName("enclosure");
        if (enclosure.getLength() > 0) {
            Element item = (Element) enclosure.item(0);
            return item.getAttribute("url");
        }

        NodeList encoded = itemElement.getElementsByTagName("content:encoded");
        if (encoded.getLength() > 0) {
            String html = encoded.item(0).getTextContent();
            return imgEncodedExt(html);
        }

        return null;
    }

    private static final Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
    private String imgEncodedExt(String value) {
        Matcher matcher = pattern.matcher(RemoveIllegalChar(value));

        return matcher.find() ? matcher.group(1) : null;
    }

    private CharSequence RemoveIllegalChar(String value) {
        return value
                .replace("&#038", "&")
                .replace("&amp", "&");
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
}
