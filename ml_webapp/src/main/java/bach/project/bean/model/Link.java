package bach.project.bean.model;


import org.bson.types.ObjectId;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Link implements Serializable {
    private ObjectId linkId;
    private LocalDateTime searchDateTime;
    private String threadTitle;
    private Integer percentage;
    private Integer ammount;

    public Link(){}

    public Link(ObjectId linkId, LocalDateTime searchDateTime, String threadTitle) {
        this.linkId = linkId;
        this.searchDateTime = searchDateTime;
        this.threadTitle = threadTitle;
    }

    public ObjectId getLinkId() {
        return linkId;
    }

    public void setLinkId(ObjectId linkId) {
        this.linkId = linkId;
    }

    public LocalDateTime getSearchDateTime() {
        return searchDateTime;
    }

    public void setSearchDateTime(LocalDateTime searchDateTime) {
        this.searchDateTime = searchDateTime;
    }

    public String getThreadTitle() {
        return threadTitle;
    }


    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getAmmount() {
        return ammount;
    }

    public void setAmmount(Integer ammount) {
        this.ammount = ammount;
    }

    @Override
    public String toString() {
        return "Link{" +
                "linkId=" + linkId +
                ", searchDateTime=" + searchDateTime +
                ", threadTitle='" + threadTitle + '\'' +
                ", percentage=" + percentage +
                ", ammount=" + ammount +
                '}';
    }
}
