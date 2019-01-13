package bach.project.controller;

import bach.project.bean.form.ChangeDataForm;
import bach.project.bean.form.ChangePasswordForm;
import bach.project.bean.form.CommentPredictionForm;
import bach.project.bean.form.LinkPredictionForm;
import bach.project.bean.model.Link;
import bach.project.bean.model.User;
import bach.project.bean.web.CommentWeb;
import bach.project.bean.web.LinkWeb;
import bach.project.service.CommentService;
import bach.project.service.PredictionService;
import bach.project.service.UserService;
import bach.project.utils.MvcUtils;
import org.bson.types.ObjectId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = AccountController.ROOT_MAPPING)
@PreAuthorize("hasRole('USER')")
public class AccountController {
    public static final String ROOT_MAPPING = "/account";

    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder;
    private final UserService userService;
    private final CommentService commentService;
    private final PredictionService predictionService;

    public AccountController(Pbkdf2PasswordEncoder pbkdf2PasswordEncoder, UserService userService, CommentService commentService, PredictionService predictionService) {
        this.pbkdf2PasswordEncoder = pbkdf2PasswordEncoder;
        this.userService = userService;
        this.commentService = commentService;
        this.predictionService = predictionService;
    }

    @RequestMapping(path = "/settings", method = RequestMethod.GET)
    public String accountCredentialsDetails(Model model, @AuthenticationPrincipal User user) {
        prepareAccountCredentialsDetailsPage(model, userService.getUserByObjectId(user.getId()),null,null);

        return "account/settings";
    }

    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public String changePassword(Model model, @AuthenticationPrincipal User user, @Valid @ModelAttribute ChangePasswordForm changePasswordForm, BindingResult bindingResult, @ModelAttribute ChangeDataForm changeDataForm) {
        User currentUser = userService.getUserByObjectId(user.getId());
        if (!bindingResult.hasErrors()) {
            if (!pbkdf2PasswordEncoder.matches(changePasswordForm.getOldPassword(), currentUser.getPassword())) {
                bindingResult.rejectValue("oldPassword", "wrong.old.password", "Incorrect password");
            } else if (!changePasswordForm.getNewPassword1().equals(changePasswordForm.getNewPassword2())) {
                bindingResult.rejectValue("newPassword1", "passwords.dont.match", "Passwords don't match");
            }else if(!MvcUtils.checkPassword(changePasswordForm.getNewPassword1())){
                bindingResult.rejectValue("newPassword1", "newPassword1.not.strong.enough", "Password should contain numbers,small letters, capital letter and be at least 9 characters long.");
            }else {
                userService.changePassword(user, changePasswordForm.getNewPassword1());
                model.addAttribute("changed_pw",Boolean.TRUE);
            }
        }
        prepareAccountCredentialsDetailsPage(model,currentUser,null,bindingResult.hasErrors()?changePasswordForm : null);

        return "account/settings";
    }

    @RequestMapping(path = "/changeData", method = RequestMethod.POST)
    public String changeData(Model model, @AuthenticationPrincipal User user, @ModelAttribute ChangePasswordForm changePasswordForm, @Valid @ModelAttribute ChangeDataForm changeDataForm, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            userService.changePersonalData(user, changeDataForm);
            prepareAccountCredentialsDetailsPage(model, userService.getUserByObjectId(user.getId()),null,null);
            model.addAttribute("changed_data",Boolean.TRUE);
        } else {
            prepareAccountCredentialsDetailsPage(model,user,changeDataForm,null);
        }
        return "account/settings";
    }

    private void prepareAccountCredentialsDetailsPage(Model model, User user,ChangeDataForm changeDataForm,ChangePasswordForm changePasswordForm) {
        if(changePasswordForm==null) {
            model.addAttribute("changePasswordForm", new ChangePasswordForm());
        }
        if(changeDataForm==null){
            changeDataForm = new ChangeDataForm();
            changeDataForm.seteMail(user.getUserName());
            changeDataForm.setFirstName(user.getFirstName());
            changeDataForm.setLastName(user.getLastName());
            model.addAttribute("changeDataForm", changeDataForm);
        }

    }

    @RequestMapping(path = "/panel", method = RequestMethod.GET)
    public String mainPanel(Model model, @AuthenticationPrincipal User user) {
        List<LinkWeb> linkWebList = Optional.ofNullable(userService.getUserByObjectId(user.getId()).getLinkSearchHistory()).orElse(Collections.emptyList()).stream().map(link -> {
            LinkWeb linkWeb = new LinkWeb(link);
            if (link.getPercentage() == null)
                linkWeb.setCommentWebList(commentService.findCommentsByLinkId(link.getLinkId()).stream().map(CommentWeb::new).collect(Collectors.toList()));
            return linkWeb;
        }).sorted(Comparator.comparing(LinkWeb::getSearchDateTime).reversed()).collect(Collectors.toList());

        model.addAttribute("linkList", linkWebList);

        return "/account/history_panel";
    }

    @RequestMapping(path = "/panel/{hexId}/details", method = RequestMethod.GET)
    public String linkDetails(@PathVariable(name = "hexId") String hexId, Model model, @AuthenticationPrincipal User user) {
        Optional<Link> optionalLink = userService.getUserByObjectId(user.getId()).getLinkSearchHistory().stream().filter(l -> l.getLinkId().toHexString().equals(hexId)).findAny();
        if (!optionalLink.isPresent()) {
            return "/denied";
        }
        LinkWeb linkWeb = new LinkWeb(optionalLink.get());
        List<CommentWeb> commentsWebByLinkId = commentService.findCommentsByLinkId(new ObjectId(hexId)).stream().map(CommentWeb::new).collect(Collectors.toList());
        linkWeb.setCommentWebList(commentsWebByLinkId);

        model.addAttribute("linkWeb", linkWeb);

        return "/account/pred_details";
    }

    @RequestMapping(path = "/deleteLink", method = RequestMethod.POST)
    public String deleteLink(@RequestParam(name = "link") String linkId, @AuthenticationPrincipal User user) {
        commentService.deleteWholeLinkByHexIds(user.getId(), new ObjectId(linkId));
        return "redirect:" + ROOT_MAPPING + "/panel";
    }

    @RequestMapping(path = "/predict", method = RequestMethod.GET)
    public String predictionPage(Model model) {
        model.addAttribute("linkPredictionForm", new LinkPredictionForm());
        model.addAttribute("commentPredictionForm", new CommentPredictionForm());
        return "/account/predict";
    }

    @RequestMapping(path = "/predictSingleComment", method = RequestMethod.POST)
    public String predictSingleComment(@AuthenticationPrincipal User user, @Valid @ModelAttribute CommentPredictionForm commentPredictionForm, BindingResult bindingResult, @ModelAttribute LinkPredictionForm linkPredictionForm) {
        if (bindingResult.hasErrors())
            return "/account/preddict";

        predictionService.predictSingleComment(user, Optional.ofNullable(commentPredictionForm.getThreadTitle()), commentPredictionForm.getCommentBody());

        return "redirect:" + ROOT_MAPPING + "/panel";
    }

    @RequestMapping(path = "/predictLink", method = RequestMethod.POST)
    public String predictWholeLink(@AuthenticationPrincipal User user, @ModelAttribute CommentPredictionForm commentPredictionForm, @Valid @ModelAttribute LinkPredictionForm linkPredictionForm, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("right",true);
            return "/account/predict";
        }
        //DEV
        long startTime = System.nanoTime();
        predictionService.predictWholeLink(user, linkPredictionForm.getLink(), linkPredictionForm.getPercentage(), linkPredictionForm.getAmmount());
        long endTime = System.nanoTime();
        System.out.println("DURATION FOR " + linkPredictionForm.getAmmount() + "comments: " + (endTime - startTime) / 1000000 + "ms");
        return "redirect:" + ROOT_MAPPING + "/panel";
    }
}
