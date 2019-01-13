package bach.project.bean.model;

import bach.project.bean.model.Link;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Document(collection = "user")
public class User implements UserDetails {
    @Id
    private ObjectId id;

    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private String activationToken;
    private String passwordResetToken;
    private List<Link> linkSearchHistory;

    public User() {}

    public User(ObjectId id, String userName, String firstName, String lastName, String password, String activationToken, String passwordResetToken, List<Link> linkSearchHistory) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.activationToken = activationToken;
        this.passwordResetToken = passwordResetToken;
        this.linkSearchHistory = linkSearchHistory;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(()-> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activationToken==null || activationToken.equals("");
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public List<Link> getLinkSearchHistory() {
        return linkSearchHistory;
    }

    public void setLinkSearchHistory(List<Link> linkSearchHistory) {
        this.linkSearchHistory = linkSearchHistory;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", activationToken='" + activationToken + '\'' +
                ", passwordResetToken='" + passwordResetToken + '\'' +
                ", linkSearchHistory=" + linkSearchHistory +
                '}';
    }
}
