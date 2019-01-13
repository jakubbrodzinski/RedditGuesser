package bach.project.bean.web;

import bach.project.bean.model.Comment;

public class CommentWeb {
    private String body;
    private long score;
    private boolean isRemoved;

    public CommentWeb(){}

    public CommentWeb(Comment comment){
        this.body=comment.getBody();
        this.score=comment.getScore();
        this.isRemoved=comment.isRemoved();
    }

    public CommentWeb(String body, long score, boolean isRemoved) {
        this.body = body;
        this.score = score;
        this.isRemoved = isRemoved;
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

    public boolean getIsRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(boolean removed) {
        isRemoved = removed;
    }
}
