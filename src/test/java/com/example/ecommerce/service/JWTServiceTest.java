package com.example.ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.repository.EcommUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class tests methods from the JWTService class.
 */
//@AutoConfigureMockMvc temporary
@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {

    /** An instance to tests the methods from the JWTService class. */
    @Autowired
    private JWTService jwtService;

    /** A repository instance of users of this ecommerce. */
    @Autowired
    private EcommUserRepository ecommUserRepository;

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    /**
     * Tests the verification user for login with an invalid verification token.
     */
    @Test
    public void testInvalidVerificationTokenForLogin() {

        // Username: "testUser1" from the data.sql file.
        EcommUser user = ecommUserRepository.findByUsernameIgnoreCase("testUser1").get();

        // Generates jwt token without build verification token.
        String token = jwtService.generateVerificationJWT(user);

        // Test an invalid verification token.
        Assertions.assertNull(jwtService.getUsernameClaim(token), "Should not get the username from this verification token.");
    }

    /**
     * Tests the getUsernameClaim method that gets the username from the authentication token.
     */
    @Test
    public void testValidVerificationTokenForLogin() {

        // Username: "testUser1" from the data.sql file.
        EcommUser user = ecommUserRepository.findByUsernameIgnoreCase("testUser1").get();

        // Generates token with only the username key.
        String token = jwtService.generateJWT(user);

        // Test a valid verification token.
        Assertions.assertEquals(user.getUsername(), jwtService.getUsernameClaim(token), "Should get the username from this verification token.");
    }

    /**
     * Tests for login process the JWT verification that rejects a token with a strange JWT
     * that generated from different algorithm from this algorithm.
     */
    @Test
    public void testStrangeGeneratedJWTForLogin() {

        // Generates strange JWT token with different sign algorithm
        // on the valid user with the username 'testUser1' from the data.sql file.
        String token = JWT.create().withClaim("USERNAME", "testUser1")
                .sign(Algorithm.HMAC256("NotASecret"));

        // Tests if the getUsername method throws SignatureVerificationException.
        Assertions.assertThrows(SignatureVerificationException.class, () -> jwtService.getUsernameClaim(token));
    }

    /**
     * Tests the JWT verification for login process that
     * rejects a token with a correct JWT algorithm sign but with no this issuer.
     */
    @Test
    public void testJWTCorrectAlgorithmButWithNoIssuerForLogin() {

        // Generates JWT token with the correct sign algorithm (with this algorithmKey).
        String token = JWT.create().withClaim("USERNAME", "testUser1")
                .sign(Algorithm.HMAC256(algorithmKey));

        // Tests if the getUsername method throws MissingClaimException with jst token with no this issuer.
        Assertions.assertThrows(MissingClaimException.class, () -> jwtService.getUsernameClaim(token));
    }

    /**
     * Tests for password reset process the JWT verification that rejects a token with a strange JWT
     * that generated from different algorithm from this algorithm.
     */
    @Test
    public void testStrangeGeneratedJWTForPasswordReset() {

        // Generates strange JWT token with different sign algorithm
        // on the valid user with the username 'testUser1' from the data.sql file.
        String token = JWT.create().withClaim("PASSWORD_RESET_EMAIL", "testUser1@junit.com")
                .sign(Algorithm.HMAC256("NotASecret"));

        // Tests if the getUsername method throws SignatureVerificationException.
        Assertions.assertThrows(SignatureVerificationException.class,
                () -> jwtService.getPasswordResetEmailClaim(token));
    }

    /**
     * Tests the JWT verification for password reset process that
     * rejects a with a correct JWT algorithm sign but with no this issuer.
     */
    @Test
    public void testJWTCorrectAlgorithmButWithNoIssuerForPasswordReset() {

        // Generates JWT token with the correct sign algorithm (with this algorithmKey).
        String token = JWT.create().withClaim("PASSWORD_RESET_EMAIL", "testUser1@junit.com")
                .sign(Algorithm.HMAC256(algorithmKey));

        // Tests if the getUsername method throws MissingClaimException with jst token with no this issuer.
        Assertions.assertThrows(MissingClaimException.class,
                () -> jwtService.getPasswordResetEmailClaim(token));
    }

    /**
     * Tests the password reset process.
     */
    @Test
    public void testPasswordResetToken() {
        EcommUser user = ecommUserRepository.findByUsernameIgnoreCase("testUser1").get();
        String token = jwtService.generatePasswordResetJWT(user);
        Assertions.assertEquals(user.getEmail(), jwtService.getPasswordResetEmailClaim(token),
                "The email from the JWT should be equal to the user's email of testUser1");
    }
}
