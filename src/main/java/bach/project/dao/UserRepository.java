package bach.project.dao;

import bach.project.bean.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    Optional<User> findByActivationToken(String activationToken);
    Optional<User> findByPasswordResetToken(String passwordResetToken);

}
