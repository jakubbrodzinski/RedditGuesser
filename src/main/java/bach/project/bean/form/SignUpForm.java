package bach.project.bean.form;

import bach.project.bean.annotation.Regexp;
import bach.project.utils.AppProperties;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class SignUpForm implements Serializable {

    @Regexp(regexp = AppProperties.EMIAL_REGEXP,message = "Invalid e-mail format")
    private String eMail;
    @NotBlank(message = "Must not be empty")
    private String password1;
    @NotBlank(message = "Must not be empty")
    private String password2;

    @NotBlank(message = "Must not be empty")
    private String firstName;
    @NotBlank(message = "Must not be empty")
    private String lastName;

    public SignUpForm(){}

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
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
}
