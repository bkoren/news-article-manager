package hr.algebra.dao.rss;

import java.time.LocalDateTime;
import java.util.List;

public record RssItem(
        String title,
        String link,
        String description,
        String pubDate,
        String imageUrl,
        List<String> categories,
        List<String> authors
) {
    public RssItem {
        categories = List.copyOf(categories);
        authors    = List.copyOf(authors);
    }
}
