package bach.project.service;

import bach.project.bean.model.Comment;
import bach.project.bean.model.Link;
import bach.project.bean.model.User;
import bach.project.dao.CommentRepository;
import bach.project.dao.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(UserRepository userRepository, CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<Comment> findCommentsByLinkId(ObjectId linkId) {
        return commentRepository.findByLinkId(linkId);
    }

    @Override
    public void deleteWholeLinkByHexIds(ObjectId userId, ObjectId linkId) {
        Optional<User> userOptional = userRepository.findById(userId);

        userOptional.ifPresent(u -> {
            commentRepository.deleteAll(commentRepository.findByLinkId(linkId));
            u.getLinkSearchHistory().removeIf(l -> l.getLinkId().equals(linkId));
            userRepository.save(u);
        });
    }

    @Override
    public void addNewPrediction(ObjectId userId, Link link, List<Comment> comments) {
        Optional<User> userOptional = userRepository.findById(userId);
        ObjectId linkObjectId = new ObjectId();
        link.setLinkId(linkObjectId);
        link.setSearchDateTime(LocalDateTime.now());
        comments.forEach(c -> c.setLinkId(linkObjectId));
        userOptional.ifPresent(u -> {
            if (u.getLinkSearchHistory() == null)
                u.setLinkSearchHistory(List.of(link));
            else
                u.getLinkSearchHistory().add(link);
            userRepository.save(u);
            commentRepository.insert(comments);
        });
    }
}
