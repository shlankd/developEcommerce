package com.example.ecommerce.exception;

/**
 * Exception that indicates that the user does not have verified email.
 */
public class NotVerifiedUserException extends Exception{

    /** Boolean instance for verified if the verification email sent to the user. */
    private boolean isVerifiedEmailSent;

    /**
     * Constructor of UserVerificationFaildException.
     * @param isVerifiedEmailSent verified instance to check if the verification email sent to the user.
     */
    public NotVerifiedUserException(boolean isVerifiedEmailSent) {
        this.isVerifiedEmailSent = isVerifiedEmailSent;
    }

    public boolean isVerifiedEmailSent() {
        return isVerifiedEmailSent;
    }
}
