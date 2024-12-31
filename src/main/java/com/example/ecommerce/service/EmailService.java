package com.example.ecommerce.service;

import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.exception.EmailFailureException;
import com.example.ecommerce.entity.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    /** The email address to send messages from. */
    @Value("${email.from}")
    private String fromAddress;

    /** The url of the front end for links. */
    @Value("${app.frontend.url}")
    private String url;

    /** The JavaMailSender instance. */
    private JavaMailSender javaMailSender;

    /**
     * Constructor of EmailService.
     * @param javaMailSender Sets the JavaMailSender object.
     */
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * creates a SimpleMailMessage object to send.
     * @return The created SimpleMailMessage object.
     */
    private SimpleMailMessage createSimpleMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(fromAddress);

        return simpleMailMessage;
    }


    /**
     * This function sends the verification email to the user.
     * @param verificationToken the verificationToken to be sent to the user.
     * @throws EmailFailureException Thrown if the send email failed.
     */
    public void emailVerificationSender(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Activate your account by verify your email");

        // NOTE: in real app or web with frontend the link to verify page gets the token and calls the API.

        message.setText("Please click on the link below for verify your email to activate your account.\n"
        + url + "/auth/verify?token=" + verificationToken.getToken());

        try{
            javaMailSender.send(message);
        }catch (MailException e){
            throw new EmailFailureException();
        }

    }

    /**
     * This function sends to the user's email the link to reset the user's password.
     * @param user The user that need to reset password.
     * @param token The password reset token that belongs to the user.
     * @throws EmailFailureException
     */
    public void emailPasswordResetSender(EcommUser user, String token) throws EmailFailureException {
        SimpleMailMessage message = createSimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Link for password reset.");
        message.setText("Click the link below for reset your password.\n" + url + "/auth/reset?token=" + token);
        try{
            javaMailSender.send(message);
        }catch(MailException e){
            throw new EmailFailureException();
        }
    }
}
