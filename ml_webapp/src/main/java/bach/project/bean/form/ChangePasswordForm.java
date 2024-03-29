package bach.project.bean.form;

import javax.validation.constraints.NotBlank;

public class ChangePasswordForm {
    private String oldPassword;
    @NotBlank(message = "Must not be empty")
    private String newPassword1;
    @NotBlank(message = "Must not be empty")
    private String newPassword2;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword1() {
        return newPassword1;
    }

    public void setNewPassword1(String newPassword1) {
        this.newPassword1 = newPassword1;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }
}
