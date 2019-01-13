package bach.project.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {
    private static final String APP_URL="http://localhost:8080/";
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;

    public MailServiceImpl(JavaMailSender mailSender,@Qualifier("mailTemplateEngine") TemplateEngine htmlTemplateEngine){
        this.mailSender=mailSender;
        this.htmlTemplateEngine=htmlTemplateEngine;
    }

    @Override
    public boolean sendVerificationTokenEmail(String activationToken, String userName) {
        final Context ctx = new Context();
        ctx.setVariable("activationLink", generateActivationLink(activationToken));
        String eMailTitle = "Reddit Guesser - activate your account";

        return this.send(userName, eMailTitle, ctx, "activateAccountEmail");
    }

    @Override
    public boolean sendPasswordResetEmail(String passwordResetToken, String userName) {
        final Context ctx = new Context();
        ctx.setVariable("resetPasswordLink", generatePasswordResetLink(passwordResetToken));
        String eMailTitle = "Reddit Guesser - reset your password";

        return this.send(userName, eMailTitle, ctx, "resetPasswordEmail");
    }

    private boolean send(String destinationEmail, String subject, Context ctx, String eMailTemplate) {
        MimeMessage eMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper eMailMessageHelper;
        try {
            eMailMessageHelper = new MimeMessageHelper(eMailMessage, true);
            eMailMessageHelper.setTo(destinationEmail);
            eMailMessageHelper.setSubject(subject);

            final String htmlContent = this.htmlTemplateEngine.process(eMailTemplate, ctx);
            eMailMessageHelper.setText(htmlContent, true);
            mailSender.send(eMailMessage);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateActivationLink(String activationToken) {
        return APP_URL + "activate_account?t=" + activationToken;
    }

    private String generatePasswordResetLink(String passwordResetToken) {
        return APP_URL + "reset_password?t=" + passwordResetToken;
    }
}
