package hr.algebra.dao.rss;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RssImportService {
    private final RssItemMapper mapper;

    public RssImportService() {
        mapper = new RssItemMapper();
    }

    public List<ParsedItem> importFrom(RssSource source)
            throws ParserConfigurationException, IOException, SAXException {

        AssetService asset = new AssetService();
        RssParser parser = new RssParser(source);

        List<ParsedItem> parsed = new ArrayList<>();
        for (RssItem item : parser.parseItems()) {
            parsed.add(mapper.map(
                    item,
                    asset.downloadImage(item.imageUrl()),
                    source)
            );
        }

        return parsed;
    }
}
