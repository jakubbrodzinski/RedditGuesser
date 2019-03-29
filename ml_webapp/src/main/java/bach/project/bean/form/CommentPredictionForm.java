package bach.project.bean.form;

import javax.validation.constraints.NotBlank;

public class CommentPredictionForm {
    private String threadTitle;
    @NotBlank(message = "Must not be empty")
    private String commentBody;

    public String getThreadTitle() {
        return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }
}
