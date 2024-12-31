package com.example.ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ecommerce.entity.EcommUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service for handling JWTs (Jason Web Token) for user authentication.
 */
@Service
public class JWTService {

    /** The secret key to encrypt the JWTs with. */
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    /** The issuer that the JWT is signed with. */
    @Value("${jwt.issuer}")
    private String issuer;

    /** An instance that indicates the time duration of the JWT from its generation until it is expired. */
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    /** Indicates the time duration of the JWT from its generation until it is expired for password reset. */
    @Value("${jwt.expiryInSecondsForPasswordReset}")
    private int expiryInSecondsForPasswordReset;

    /** The algorithm generated post construction. */
    private Algorithm algorithm;

    /** The JWT claims keys. */
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String PASSWORD_RESET_EMAIL_KEY = "PASSWORD_RESET_EMAIL";

    /**
     * Post construction method.
     */
    @PostConstruct
    public void postConstruct(){
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    /**
     * Generates a JWT to a given user.
     * @param user The user to generate JWT for.
     * @return The JWT.
     */
    public String generateJWT(EcommUser user){
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                // Expires JWT in 7 days.
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Generates a verification token for email verification.
     * @param user The user that request the generate verification token for.
     * @return The generated token.
     */
    public String generateVerificationJWT(EcommUser user){
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
                // Expires JWT in 7 days.
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Generates token (JWT) for user to reset password.
     * @param user The user that need to reset the password.
     * @return The generated token.
     */
    public String generatePasswordResetJWT(EcommUser user){
        return JWT.create()
                .withClaim(PASSWORD_RESET_EMAIL_KEY, user.getEmail())
                // Expires JWT in 30 minutes.
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSecondsForPasswordReset)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Gets the password reset email claim from the given JWT.
     * @param token The JWT to decode.
     * @return the password reset email from the given token.
     */
    public String getPasswordResetEmailClaim(String token) {

        // Decode and verifies the given token based on the algorithm and issuer.
        // note: Go to verify declarations to see the exceptions to JWTVerificationException (change delete)
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);

        return jwt.getClaim(PASSWORD_RESET_EMAIL_KEY).asString();
    }

    /**
     * Gets the username claim from the given JWT.
     * @param token The JWT to decode.
     * @return The username stored inside from the given token.
     */
    public String getUsernameClaim(String token){

        // Decode and verifies the given token based on the algorithm and issuer.
        // note: Go to verify declarations to see the exceptions to JWTVerificationException (change delete)
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);

        return jwt.getClaim(USERNAME_KEY).asString();
    }
}
