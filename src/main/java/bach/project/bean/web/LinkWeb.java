package bach.project.bean.web;

import bach.project.bean.model.Link;

import java.time.LocalDateTime;
import java.util.List;

public class LinkWeb {

    private String hexId;
    private LocalDateTime searchDateTime;
    private String threadTitle;
    private Integer percentage;
    private List<CommentWeb> commentWebList;

    public LinkWeb(){}

    public LinkWeb(Link link){
        this.hexId=link.getLinkId().toHexString();
        this.searchDateTime=link.getSearchDateTime();
        this.threadTitle=link.getThreadTitle();
        this.percentage=link.getPercentage();
    }

    public LinkWeb(Link link,List<CommentWeb> commentWebs){
        this.hexId=link.getLinkId().toHexString();
        this.searchDateTime=link.getSearchDateTime();
        this.threadTitle=link.getThreadTitle();
        this.percentage=link.getPercentage();
        this.commentWebList=commentWebs;
    }

    public String getHexId() {
        return hexId;
    }

    public void setHexId(String hexId) {
        this.hexId = hexId;
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

    public List<CommentWeb> getCommentWebList() {
        return commentWebList;
    }

    public void setCommentWebList(List<CommentWeb> commentWebList) {
        this.commentWebList = commentWebList;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
}
