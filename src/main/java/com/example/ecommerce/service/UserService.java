package com.example.ecommerce.service;

import com.example.ecommerce.api_model.LoginBody;
import com.example.ecommerce.api_model.PasswordResetBody;
import com.example.ecommerce.api_model.RegistrationBody;
import com.example.ecommerce.entity.EcommRole;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.exception.EmailFailureException;
import com.example.ecommerce.exception.EmailNotFoundException;
import com.example.ecommerce.exception.NotVerifiedUserException;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.entity.VerificationToken;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.ecommerce.exception.AlReadyExistsUserException;


import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static com.example.ecommerce.util.EcommConstants.ROLE_USER;


/**
 * This class contains methods that service user's operations.
 */
@Service
public class UserService {

    private EcommUserRepository ecommUserRepository;

    private RoleRepository roleRepository;

    private EncryptionService encryptionService;

    private JWTService jwtService;

    private VerificationTokenRepository verificationTokenRepository;

    private EmailService emailService;

    /**
     * UserService constructor.
     * @param ecommUserRepository The EcommUserRepository object.
     * @param roleRepository The RoleRepository object.
     * @param encryptionService The EncryptionService object.
     * @param jwtService The JWTService object.
     * @param verificationTokenRepository The VerificationTokenRepository object.
     * @param emailService The EmailService object.
     */
    public UserService(EcommUserRepository ecommUserRepository, RoleRepository roleRepository,
                       EncryptionService encryptionService, JWTService jwtService,
                       VerificationTokenRepository verificationTokenRepository, EmailService emailService){
        this.ecommUserRepository = ecommUserRepository;
        this.roleRepository = roleRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    public Optional<EcommUser> findByUsernameIgnoreCase (String username){
        return ecommUserRepository.findByUsernameIgnoreCase(username);
    }

    /**
     * user registration by given data to fill.
     * @param registrationBody the registration information to fill.
     * @return registered user.
     * @throws AlReadyExistsUserException throw exception if the new user registration is already exists.
     */
    public EcommUser registerUser(RegistrationBody registrationBody) throws AlReadyExistsUserException, EmailFailureException {

        if(ecommUserRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent() ||
                ecommUserRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()){
            throw new AlReadyExistsUserException();
        }

        // Generates a new user entity.
        EcommUser user = new EcommUser();

        Optional<EcommRole> opRole = roleRepository.findByRoleName(ROLE_USER);

            if (opRole.isPresent()) {
                EcommRole userRole = opRole.get();
                user.getRoles().add(userRole);
            }

        // Sets the user's information fields that in the EcommUser entity.
        user.setUsername(registrationBody.getUsername());
        // Sets the password with encryption.
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());

        //Builds the verification token.
        VerificationToken verificationToken = buildVerificationToken(user);

        // Sends the email with the verification token.
        emailService.emailVerificationSender(verificationToken);

        // Save the ecommerce user in the repository (EcommUserRepository interface).
        user = ecommUserRepository.save(user);

        return user;
    }

    /**
     * builds a VerificationToken object for email verification that will be sent to the user.
     * @param user  The user that will be sent the build token.
     * @return The build VerificationToken object.
     */
    private VerificationToken buildVerificationToken(EcommUser user){
        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setGeneratedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);

        return verificationToken;
    }

    /**
     * Login user process that if the login is successful, returns to the user an authentication token
     * otherwise returns null.
     * @param loginBody The login body that request to login.
     * @return The authentication token. Null if the request was invalid.
     */
    public String loginUser(LoginBody loginBody) throws NotVerifiedUserException, EmailFailureException {

        // Finds the user from the repository by the given loginBody.
        Optional<EcommUser> opUser = ecommUserRepository.findByUsernameIgnoreCase(loginBody.getUsername());

        // Condition for check if the user exists in the repository.
        if(opUser.isPresent()){
           EcommUser user = opUser.get();

           // Condition for check the password validation.
           if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){

               // Condition for check if the email verified
               if(user.isVerifiedEmail()) {

                   // Returns authentication token if email verified is true.
                   return jwtService.generateJWT(user);
               }
               // If the email verification is false, sends verification email to the user.
               else{
                   boolean resendVerificationEmail = false;
                   List<VerificationToken> verificationTokens = user.getVerificationTokens();

                   // Condition for check if resend the verification email is needed.
                   // If the user's verification token list  is empty OR the last verification token sent more than hour ago.
                   if(verificationTokens.size() == 0 || verificationTokens.get(0).getGeneratedTimestamp()
                           .before(new Timestamp(System.currentTimeMillis() - (60*60*1000)))){

                       // Then sets the boolean instance resendVerificationEmail is true.
                       resendVerificationEmail = true;
                   }

                   // If the boolean instance resend verification email is true.
                   if(resendVerificationEmail){

                       // Then resend the verification email to the user.
                       VerificationToken newVerificationToken = buildVerificationToken(user);
                       verificationTokenRepository.save(newVerificationToken);
                       emailService.emailVerificationSender(newVerificationToken);
                   }

                   throw new NotVerifiedUserException(resendVerificationEmail);
               }
           }
        }

        return null;
    }

    /**
     *  Verifies a user from the given token.
     * @param token The token to use to verify a user.
     * @return True if it was verified, false if already verified or token invalid.
     */
    @Transactional
    public boolean userVerify(String token){

        // Gets the verification token if exists.
        Optional<VerificationToken> opToken = verificationTokenRepository.findByToken(token);

        // Condition if the verification token exists.
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();

            // Gets the user entity from the token.
            EcommUser user = verificationToken.getUser();

            // Condition if the user's email is not verified.
            if(!user.isVerifiedEmail()){

                // Sets the email verification true and save the changes of the user entity.
                user.setVerifiedEmail(true);
                ecommUserRepository.save(user);

                // Deletes all the verification tokens from Repository that where made for this user.
                // No use to store those verification tokens after the success of the user's verification.
                verificationTokenRepository.deleteByUser(user);

                return true;
            }
        }

        return false;
    }

    /**
     * Allows the user to reset the password by sending a link to the given user's email.
     * @param email User's email.
     * @throws EmailNotFoundException
     * @throws EmailFailureException
     */
    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {

        // Find the user by the email.
        Optional<EcommUser> opUser = ecommUserRepository.findByEmailIgnoreCase(email);

        if(opUser.isPresent()){
            EcommUser user = opUser.get();
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.emailPasswordResetSender(user, token);
        }
        else{
            throw new EmailNotFoundException();
        }
    }

    /**
     * Implements the password reset process for a given PasswordResetBody.
     * @param resetPassBody Contains token and password instances for reset password process.
     */
    public void passwordReset(PasswordResetBody resetPassBody){

        // Finds the user's email from the resetPassBody's token.
        String email = jwtService.getPasswordResetEmailClaim(resetPassBody.getToken());

        // Finds the user's account from the email.
        Optional<EcommUser> opUser = ecommUserRepository.findByEmailIgnoreCase(email);

        // Saves the user's account (if found) with the new password.
        if(opUser.isPresent()){
            EcommUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(resetPassBody.getPassword()));
            ecommUserRepository.save(user);
        }
    }

    /**
     * Checks if the authenticated user has match to the user's id.
     * @param user The authenticated user.
     * @param id The user's id.
     * @return Returns true if the authenticated user has a match of the user's id else returns false.
     */
    public boolean isAuthUserHasAMatchOfUserID(EcommUser user, Long id){
        return user.getId() == id;
    }

}
