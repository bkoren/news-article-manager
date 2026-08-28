package hr.algebra.dao.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Source {

    @XmlAttribute
    private final int sourceId;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "feedUrl")
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
    public boolean equals(Object o) {
        if (!(o instanceof Source source)) return false;
        return Objects.equals(feedUrl, source.feedUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(feedUrl);
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
