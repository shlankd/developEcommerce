package com.example.ecommerce.service;

import com.example.ecommerce.api_model.LoginBody;
import com.example.ecommerce.api_model.PasswordResetBody;
import com.example.ecommerce.api_model.RegistrationBody;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.entity.VerificationToken;
import com.example.ecommerce.exception.AlReadyExistsUserException;
import com.example.ecommerce.exception.EmailFailureException;
import com.example.ecommerce.exception.EmailNotFoundException;
import com.example.ecommerce.exception.NotVerifiedUserException;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.repository.VerificationTokenRepository;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

/**
 * This class tests methods from the UserService class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    /** An instance of imaginary email server for testing. */
    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    /** An instance to tests the methods from the EncryptionService class. */
    @Autowired
    private EncryptionService encryptionService;

    /** An instance to tests the methods from the UserService class. */
    @Autowired
    private UserService userService;

    /** A repository instance of verification tokens. */
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    /** An instance to tests the methods from the JWTService class. */
    @Autowired
    private JWTService jwtService;

    /** A repository instance of users of this ecommerce. */
    @Autowired
    private EcommUserRepository ecommUserRepository;

    /**
     * Tests the process of user registration.
     * @throws MessagingException
     *              Throws exceptions when there is a  failure of the imaginary email server.
     */
    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {

        RegistrationBody body = new RegistrationBody();

        body.setUsername("testUser1");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("first_name");
        body.setLastName("last_name");
        body.setPassword("MySecretPassword$123");

        // Test the registerUser throw exception AlReadyExistsUserException with an exists username.
        Assertions.assertThrows(AlReadyExistsUserException.class,
                () -> userService.registerUser(body), "The username should be already exists");

        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("testUser1@junit.com");

        // Test the registerUser throw exception AlReadyExistsUserException with an exists email.
        Assertions.assertThrows(AlReadyExistsUserException.class,
                () -> userService.registerUser(body), "The email should be already exists");

        // Back to the original email.
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");

        // Test the registerUser with no throw exception AlReadyExistsUserException.
        Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
                "The register user should be successful");


        // Test the email verification.
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString());
    }

    /**
     * Tests the loginUser method from the UserService class.
     * @throws NotVerifiedUserException Exception when the user not verified.
     * @throws EmailFailureException Exception in email verification.
     */
    @Test
    @Transactional
    public void testLoginUser() throws NotVerifiedUserException, EmailFailureException {
        LoginBody body = new LoginBody();

        // Sets user login with wrong inputs.
        body.setUsername("testUser1-NOT_EXISTS");
        body.setPassword("Password1$123$WRONG_PASSWORD");

        // Tests user login when the user is not exists.
        Assertions.assertNull(userService.loginUser(body), "The user should not exists.");

        // Sets with an exists username.
        body.setUsername("testUser1");

        // Tests user login with wrong password.
        Assertions.assertNull(userService.loginUser(body), "The password should be wrong.");

        // Sets with a correct password.
        body.setPassword("testPassword1$123");

        // Tests user login with correct inputs.
        Assertions.assertNotNull(userService.loginUser(body), "The user login process should be done successfully.");

        // Sets the user login body to testUser2 with a false email verification.
        body.setUsername("testUser2");
        body.setPassword("testPassword2$123");

        // Tests the email verification sender to user login with false email verification.
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false, "The user's email verified should be false.");
        }
        catch (NotVerifiedUserException e){
            Assertions.assertTrue(e.isVerifiedEmailSent(), "The email verification should be sent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }

        // Tests the another case of login user with false email verification
        // which the email verification have been sent in the last hour.
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false, "The user's email verified should be false.");
        }
        catch (NotVerifiedUserException e){
            Assertions.assertFalse(e.isVerifiedEmailSent(), "The email verification should not be sent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }

    /**
     * Tests the userVerify method from the UserService class.
     * @throws EmailFailureException Exception in email verification.
     */
    @Test
    @Transactional
    public void testUserVerify() throws EmailFailureException {

        // Test user verification with verification token that doesn't exist.
        Assertions.assertFalse(userService.userVerify("No Token"), "Input token that doesn't exists should return false.");

        EcommUser testUser2 = null;
        Optional<EcommUser> opTestUser2 = ecommUserRepository.findByUsernameIgnoreCase("testUser2");
        if(opTestUser2.isPresent()){
            testUser2 = opTestUser2.get();
        }
        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser2, "The testUser2 should not be null.");

        // Sets the login body.
        LoginBody body = new LoginBody();
        body.setUsername("testUser2");
        body.setPassword("testPassword2$123");

        // Handles exceptions from the login process.
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false, "The user's email verified should be false.");
        }
        catch (NotVerifiedUserException e){
            List<VerificationToken> tokenList = verificationTokenRepository.findByUser_IdOrderByIdDesc(testUser2.getId());
            String token = tokenList.get(0).getToken();
            Assertions.assertTrue(userService.userVerify(token));
            Assertions.assertNotNull(body, "The user's email verified should now be true.");
        }
    }

    /**
     * Tests the forgotPassword method from the UserService class.
     * @throws MessagingException
     */
    @Test
    @Transactional
    public void testForgotPassword() throws MessagingException {

        // Tests forgot password with email that doesn't exist.
        Assertions.assertThrows(EmailNotFoundException.class,
                () -> userService.forgotPassword("nullUser@junit.com"),
                "The not existing email should be reject");

        // Tests forgot password with email of 'testUser1' that exists. (from data.sql file)
        Assertions.assertDoesNotThrow(() -> userService.forgotPassword("testUser1@junit.com"));

        // Tests if the 'testUser1' received the email of forgot password.
        Assertions.assertEquals("testUser1@junit.com", greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString(),
                "The email of password reset should be sent.");
    }

    /**
     * Tests the passwordReset method from the UserService class.
     */
    @Test
    @Transactional
    public void testPasswordReset(){
        EcommUser user = ecommUserRepository.findByUsernameIgnoreCase("testUser1").get();
        String token = jwtService.generatePasswordResetJWT(user);
        PasswordResetBody passwordResetBody = new PasswordResetBody();
        passwordResetBody.setToken(token);
        passwordResetBody.setPassword("NewPassword$123");
        userService.passwordReset(passwordResetBody);
        user = ecommUserRepository.findByUsernameIgnoreCase("testUser1").get();
        Assertions.assertTrue(encryptionService.verifyPassword("NewPassword$123",
                user.getPassword()), "The new password set should be in the data base.");
    }
    //TODO: Tests the validity of the forgot password.
}
