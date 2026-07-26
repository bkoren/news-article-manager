package hr.algebra.dao.models;

public class Source {
    private final int sourceId;
    private String name;
    private String feedUrl;

    public Source(int sourceId, String name, String feedUrl) {
        this.sourceId = sourceId;
        this.name = name;
        this.feedUrl = feedUrl;
    }

    public int getSourceId() {
        return sourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    @Override
    public String toString() {
        return "Source{" +
                "sourceId=" + sourceId +
                ", name='" + name + '\'' +
                ", feedUrl='" + feedUrl + '\'' +
                '}';
    }
}
