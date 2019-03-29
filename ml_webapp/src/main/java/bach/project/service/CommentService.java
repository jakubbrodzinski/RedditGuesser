package bach.project.service;

import bach.project.bean.model.Comment;
import bach.project.bean.model.Link;
import org.bson.types.ObjectId;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByLinkId(ObjectId linkId);
    void deleteWholeLinkByHexIds(ObjectId userId,ObjectId linkId);
    void addNewPrediction(ObjectId userId, Link link,List<Comment> comments);
}
