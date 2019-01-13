package bach.project.bean.form;

import bach.project.bean.annotation.Regexp;
import bach.project.utils.AppProperties;

import javax.validation.constraints.NotBlank;

public class ChangeDataForm {
    @Regexp(regexp = AppProperties.EMIAL_REGEXP,message = "Invalid e-mail format")
    private String eMail;

    @NotBlank(message = "Must not be empty")
    private String firstName;
    @NotBlank(message = "Must not be empty")
    private String lastName;

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
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
