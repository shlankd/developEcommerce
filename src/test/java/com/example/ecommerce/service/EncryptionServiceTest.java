package com.example.ecommerce.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class tests methods from the EncryptionService class.
 */
//@AutoConfigureMockMvc temporary
@SpringBootTest
@AutoConfigureMockMvc
public class EncryptionServiceTest {

    /** An instance to tests the methods from the EncryptionService class. */
    @Autowired
    private EncryptionService encryptionService;

    /**
     * Tests encryptPassword and verifyPassword methods from the EncryptionService class.
     */
    @Test
    public void testEncryptionPassword() {
        String password = "PaSswOrd!123";

        String hash = encryptionService.encryptPassword(password);

        // Test verify password with hashed password.
        Assertions.assertTrue(encryptionService.verifyPassword(password, hash), "Verify with hashed password that should be valid.");

        // Test verify password with not hashed password.
        Assertions.assertFalse(encryptionService.verifyPassword(password+"Boo", hash), "Verify with not hashed password that should not be valid.");
    }
}
