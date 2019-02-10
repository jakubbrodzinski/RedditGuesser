package bach.project.service;

import bach.project.bean.model.User;
import bach.project.bean.exceptions.InvalidTokenException;
import bach.project.bean.form.ChangeDataForm;
import bach.project.bean.form.SignUpForm;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    boolean existsUserByUserName(String userName);
    User getUserByObjectId(ObjectId objectId);
    Optional<User> getUserByUserName(String userName);

    void registerUser(SignUpForm signUpForm);
    void changePassword(User user, String plainTextPassword);
    void changePersonalData(User user, ChangeDataForm changeDataForm);

    boolean refreshActivationToken(String userName);
    boolean activateAccount(String activationToken);
    boolean createResetPasswordToken(String userName);
    boolean resetPassword(String resetPaswordToken,String rawPassword) throws InvalidTokenException;

    void removeUser(String userName);
}

