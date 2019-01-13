package bach.project.service;

import bach.project.bean.model.User;
import bach.project.bean.exceptions.InvalidTokenException;
import bach.project.bean.form.ChangeDataForm;
import bach.project.bean.form.SignUpForm;
import bach.project.dao.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder;
    private final MailService mailService;

    public UserServiceImpl(UserRepository userRepository,Pbkdf2PasswordEncoder pbkdf2PasswordEncoder,MailService mailService){
        this.userRepository=userRepository;
        this.pbkdf2PasswordEncoder=pbkdf2PasswordEncoder;
        this.mailService=mailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username).orElseThrow(()->new UsernameNotFoundException("Wasn't able to find User with userName such as "+username));
    }

    @Override
    public void registerUser(SignUpForm signUpForm) {
        User user=new User();
        user.setFirstName(signUpForm.getFirstName());
        user.setLastName(signUpForm.getLastName());
        user.setUserName(signUpForm.geteMail());
        user.setPassword(pbkdf2PasswordEncoder.encode(signUpForm.getPassword1()));
        String activationToken=StringUtils.randomAlphanumeric(10);
        user.setActivationToken(activationToken);

        userRepository.insert(user);
        mailService.sendVerificationTokenEmail(activationToken,signUpForm.geteMail());
    }

    @Override
    public boolean refreshActivationToken(String userName) {
        String activationToken=StringUtils.randomAlphanumeric(10);
        return userRepository.findByUserName(userName).filter(u->u.getActivationToken()!=null).map(u->{
            u.setActivationToken(activationToken);
            userRepository.save(u);
            return mailService.sendVerificationTokenEmail(activationToken,userName);
        }).orElse(Boolean.FALSE);
    }

    @Override
    public boolean existsUserByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean activateAccount(String activationToken) {
        return userRepository.findByActivationToken(activationToken).map(u->{
            u.setActivationToken(null);
            userRepository.save(u);
            return Boolean.TRUE;
        }).orElse(Boolean.FALSE);
    }

    @Override
    public boolean createResetPasswordToken(String userName) {
        return userRepository.findByUserName(userName).map(u->{
            if(u.getActivationToken()!=null)
                return Boolean.FALSE;
            String token=StringUtils.randomAlphanumeric(10);
            u.setPasswordResetToken(token);
            userRepository.save(u);
            mailService.sendPasswordResetEmail(token,userName);
            return Boolean.TRUE;
        }).orElse(Boolean.FALSE);
    }

    @Override
    public boolean resetPassword(String resetPaswordToken,String rawPassword) throws InvalidTokenException {
        return userRepository.findByPasswordResetToken(resetPaswordToken).map(u->{
            u.setPasswordResetToken(null);
            u.setPassword(pbkdf2PasswordEncoder.encode(rawPassword));
            userRepository.save(u);
            return Boolean.TRUE;
        }).orElseThrow(()->new InvalidTokenException());
    }

    @Override
    public void changePassword(User user,String plainTextPassword) {
        userRepository.findById(user.getId()).ifPresent(u->{
            u.setPassword(pbkdf2PasswordEncoder.encode(plainTextPassword));
            userRepository.save(u);
        });
    }

    @Override
    public void changePersonalData(User user, ChangeDataForm changeDataForm) {
        userRepository.findById(user.getId()).ifPresent(u->{
            u.setFirstName(changeDataForm.getFirstName());
            u.setLastName(changeDataForm.getLastName());
            u.setUserName(changeDataForm.geteMail());
            userRepository.save(u);
        });
    }

    @Override
    public User getUserByObjectId(ObjectId objectId) {
        return userRepository.findById(objectId).get();
    }
}
