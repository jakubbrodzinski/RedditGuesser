package bach.project.bean.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "comment")
public class Comment implements Serializable {
    @Id
    private ObjectId commentId;
    private ObjectId linkId;
    private String body;
    private long score;
    private boolean isRemoved;

    public Comment(){}

    public Comment(ObjectId commentId, ObjectId linkId, String body, long score, boolean isRemoved) {
        this.commentId = commentId;
        this.linkId = linkId;
        this.body = body;
        this.score = score;
        this.isRemoved = isRemoved;
    }

    public ObjectId getCommentId() {
        return commentId;
    }

    public void setCommentId(ObjectId commentId) {
        this.commentId = commentId;
    }

    public ObjectId getLinkId() {
        return linkId;
    }

    public void setLinkId(ObjectId linkId) {
        this.linkId = linkId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", linkId=" + linkId +
                ", body='" + body + '\'' +
                ", score=" + score +
                ", isRemoved=" + isRemoved +
                '}';
    }
}
