package bach.project.controller;


import bach.project.bean.enums.AccountStatus;
import bach.project.bean.enums.ErrorCode;
import bach.project.bean.exceptions.InvalidTokenException;
import bach.project.bean.form.ChangePasswordViaTokenForm;
import bach.project.bean.form.SignUpForm;
import bach.project.service.UserService;
import bach.project.utils.AppProperties;
import bach.project.utils.MvcUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;

@Controller
@RequestMapping(path = AuthController.ROOT_MAPPING)
@PermitAll
public class AuthController {
    public static final String ROOT_MAPPING = "/";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = ROOT_MAPPING, method = RequestMethod.GET)
    public String redirectToSignIn() {
        return "redirect:" + "/sign_in";
    }

    @RequestMapping("denied")
    public String accessDeniedPage() {
        return "/denied.html";
    }

    //---------------sign in/up
    @RequestMapping(path = "/sign_in", method = RequestMethod.GET)
    public String signIn(@RequestParam(name = "user", required = false) String userName, @RequestParam(name = "error", required = false) Integer errorCode, @RequestParam(name = "status", required = false) Integer status, Model model) {
        if (errorCode != null)
            model.addAttribute("errorCode", ErrorCode.getCodeByStatus(errorCode));
        if (status != null)
            model.addAttribute("accountStatus", AccountStatus.getAccountStatusByInteger(status));
        if (userName != null)
            model.addAttribute("userName", userName);
        return "/auth/sign_in";
    }

    @RequestMapping(path = "/sign_up", method = RequestMethod.GET)
    public String signUp(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "auth/sign_up";
    }

    @RequestMapping(path = "/sign_up", method = RequestMethod.POST)
    public String signUp(Model model, @Valid @ModelAttribute SignUpForm signUpForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "auth/sign_up";
        if (userService.existsUserByUserName(signUpForm.geteMail())) {
            bindingResult.rejectValue("eMail", "email.exists", "Account with this e-Mail already exists");
            return "auth/sign_up";
        }
        if (!MvcUtils.checkPassword(signUpForm.getPassword1())) {
            bindingResult.rejectValue("password1", "password1.not.strong.enough", "Password should contain numbers,small letters, capital letter and be at least 9 characters long.");
            return "auth/sign_up";
        } else if (!signUpForm.getPassword1().equals(signUpForm.getPassword2())) {
            bindingResult.rejectValue("password1", "password1.doeesnt.match", "Passwords doesn't match");
            return "auth/sign_up";
        }

        userService.registerUser(signUpForm);

        return "redirect:" + "/sign_in?status=1";
    }

    //----------------reset password mechanism
    @RequestMapping(path = "/forgot_password", method = RequestMethod.GET)
    public String initialForgotPasswordStep() {
        return "/auth/forgot_password1";
    }

    @RequestMapping(path = "/forgot_password", method = RequestMethod.POST)
    public String initialForgotPasswordStep(@RequestParam(name = "username") String userName, Model model) {
        if (!userName.matches(AppProperties.EMIAL_REGEXP)) {
            model.addAttribute("errorCode", ErrorCode.WRONG_USERNAME);
            return "/auth/forgot_password1";
        } else if (!userService.createResetPasswordToken(userName)) {
            model.addAttribute("errorCode", ErrorCode.NOT_ACTIVATED);
            model.addAttribute("userName", userName);
            return "/auth/forgot_password1";
        } else {
            return "redirect:" + "/sign_in?status=4";
        }
    }

    @RequestMapping(path = "/reset_password", method = RequestMethod.GET)
    public String resetPasswordViaToken(@RequestParam(name = "t") String token, Model model) {
        ChangePasswordViaTokenForm form = new ChangePasswordViaTokenForm();
        form.setResetPasswordToken(token);
        model.addAttribute("form", form);

        return "/auth/forgot_password2";
    }

    @RequestMapping(path = "/reset_password", method = RequestMethod.POST)
    public String resetPasswordViaToken(@Valid @ModelAttribute(name = "form") ChangePasswordViaTokenForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors())
            return "/auth/forgot_password2";
        if (form.getResetPasswordToken() == null || form.getResetPasswordToken().length() != 10) {
            return "redirect:" + "/sign_in?error=3";
        }
        if (form.getNewPassword1() == null || !form.getNewPassword1().equals(form.getNewPassword2())) {
            bindingResult.rejectValue("newPassword1", "password1.doeesnt.match", "Passwords doesn't match");
            return "/auth/forgot_password2";
        }else if (!MvcUtils.checkPassword(form.getNewPassword1())){
            bindingResult.rejectValue("newPassword1", "password1.not.strong.enough", "Password should contain numbers,small letters, capital letter and be at least 9 characters long.");
            return "/auth/forgot_password2";
        }
        try {
            boolean result = userService.resetPassword(form.getResetPasswordToken(), form.getNewPassword1());
        } catch (InvalidTokenException e) {
            return "redirect:" + "/sign_in?error=3";
        }
        return "redirect:" + "/sign_in?status=3";
    }


    //----------------activate account
    @RequestMapping(path = "/activate_account", method = RequestMethod.GET)
    public String activateViaToken(@RequestParam(name = "t") String activationToken, Model model) {
        if (activationToken.length() != 10 || !userService.activateAccount(activationToken)) {
            model.addAttribute("errorCode", ErrorCode.INVALID_TOKEN);
        } else {
            model.addAttribute("accountStatus", AccountStatus.ACTIVATED);
        }
        return "auth/sign_in";
    }

    @RequestMapping(path = "/resend_token", method = RequestMethod.GET)
    public String resendActivationToken(@RequestParam(name = "username") String userName, Model model) {
        if (!userService.refreshActivationToken(userName))
            model.addAttribute("errorCode", ErrorCode.WRONG_USERNAME);
        else
            model.addAttribute("accountStatus", AccountStatus.TOKEN_SENT);

        return "auth/sign_in";
    }
}
