package com.example.ecommerce.controller;

import com.example.ecommerce.api_model.LoginBody;
import com.example.ecommerce.api_model.LoginResponse;
import com.example.ecommerce.api_model.PasswordResetBody;
import com.example.ecommerce.api_model.RegistrationBody;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.exception.AlReadyExistsUserException;
import com.example.ecommerce.exception.EmailFailureException;
import com.example.ecommerce.exception.EmailNotFoundException;
import com.example.ecommerce.exception.NotVerifiedUserException;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * This Rest Controller class handles the authentication requests.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    /**
     * AuthController constructor.
     * @param userService
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Post Mapping to handle registering users.
     * @param registrationBody the registration information to fill.
     * @return Response to front end.
     */
    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody){
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        }
        catch (AlReadyExistsUserException e){
            // Catch exception when registers a user that is already exists.
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (EmailFailureException e) {
            // Catch email verification exception from the registration if occurs.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * Post Mapping that handle user login event.
     * If there is no exception with the login than the user gets authentication token.
     * Else the user gets error status.
     * @param loginBody loginBody The login information of the user.
     * @return The authentication token if successful.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){

        String jwt = null;

        // Attempt to login the user.
        try {
            jwt = userService.loginUser(loginBody);
        }
        catch (NotVerifiedUserException e) {

            // Creates failure response for the user that not verified.
            LoginResponse failResponse = new LoginResponse();
            failResponse.setSuccess(false);
            String failReason = "NOT_VERIFIED_USER";
            if(e.isVerifiedEmailSent()){
                // Updates failure response for the user that not verified the after the resent email verification.
                failReason += "_RESENT_EMAIL";
            }
            failResponse.setReasonOfFailure(failReason);
            // Sends FORBIDDEN for the user with the failReason that has email verification false.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(failResponse);
        }
        catch (EmailFailureException ex) {
            // Sends INTERNAL_SERVER_ERROR for the user if the emailVerificationSender failed.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if(jwt == null){
            // Sends BAD_REQUEST if the user did not get jwt.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else{
            LoginResponse successResponse = new LoginResponse();
            successResponse.setJwt(jwt);
            successResponse.setSuccess(true); // Sets the success login process true.
            return ResponseEntity.ok(successResponse);
        }
    }

    /**
     * Post mapping that verifies email by the given email token that sent to certain account.
     * @param token The verification token that sent through email for a certain user.
     *              This is not the same as an authentication JWT.
     * @return 200 if successful. 409 if failure.
     */
    @PostMapping("/verify")
    public ResponseEntity emailVerify(@RequestParam String token){
        if (userService.userVerify(token)) {
            return ResponseEntity.ok().build();
        }
        else {
            // NOTE: The CONFLICT status is for where the user is already verified or the verification token doesn't exist.
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Post mapping that allows the user to reset the password by sending a link to the given user's email.
     * @param email The user's email that forgot password.
     * @return Returns status ok if email sender done successfully,
     *         bad request if email doesn't exist
     *         and an internal server error if there is a failure of sending the email.
     */
    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email){
        try{
            userService.forgotPassword(email);
            return ResponseEntity.ok().build();
        }
        catch(EmailFailureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        catch(EmailNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Post mapping for password reset by the given resetPassBody.
     * @param resetPassBody Type of PasswordResetBody contains token and password.
     * @return Returns status ok if the reset process done successfully.
     */
    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody resetPassBody){
        userService.passwordReset(resetPassBody);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets the current logged-in user profile and returns it.
     * @param user The @AuthenticationPrincipal method cast the authentication
     *             principal object to EcommUser class.
     * @return The user profile.
     */
    @GetMapping("/me")
    public EcommUser getCurrUserLoggedInProfile(@AuthenticationPrincipal EcommUser user){
        return user;
    }

}
