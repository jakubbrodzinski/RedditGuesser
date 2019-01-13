package bach.project.service;

public interface MailService {
    boolean sendVerificationTokenEmail(String activationToken, String userName);
    boolean sendPasswordResetEmail(String passwordResetToken, String userName);
}
