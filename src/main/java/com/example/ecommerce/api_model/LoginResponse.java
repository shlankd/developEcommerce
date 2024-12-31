package com.example.ecommerce.api_model;

/**
 * This class contains the response that sent from login request.
 */
public class LoginResponse {

    /** The JWT token for authentication. */
    private String jwt;

    /** Boolean instance to indicate if the login process succeeded or not.  */
    private boolean success;

    /** Contains the text that describes the reason of the failed login process.  */
    private String reasonOfFailure;



    /** Getters and Setters. */

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReasonOfFailure() {
        return reasonOfFailure;
    }

    public void setReasonOfFailure(String reasonOfFailure) {
        this.reasonOfFailure = reasonOfFailure;
    }
}
