package bach.project.bean.form;

import javax.validation.constraints.NotBlank;

public class ChangePasswordViaTokenForm {
    private String resetPasswordToken;
    @NotBlank(message = "Must not be empty")
    private String newPassword1;
    @NotBlank(message = "Must not be empty")
    private String newPassword2;

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
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
